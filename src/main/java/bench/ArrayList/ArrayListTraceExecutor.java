package bench.ArrayList;

import estruturas.ArrayList.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Executor de traces CSV para avaliar desempenho do estruturas.ArrayList (sua
 * implementação).
 *
 * Formato esperado do CSV (gerado pelo seu script):
 * op,a,b
 *
 * Mapeamento:
 * ADD_END,a, -> list.add(a)
 * ADD_AT,a,b -> list.add(a, b)
 * GET,a, -> list.get(a)
 * REMOVE_AT,a, -> list.remove(a)
 * INDEX_OF,a, -> list.indexOf(a)
 *
 * Métricas coletadas por trace:
 * - Tempo total (ns)
 * - Tempo por operação (ns acumulado por tipo)
 * - Contagem por operação
 * - Heap usado antes/depois
 * - Pico aproximado de heap usado durante execução (amostragem periódica)
 *
 * Metodologia:
 * - Warm-up: executa o mesmo trace N vezes SEM registrar (para estabilizar
 * JIT).
 * - Medição: executa 1 vez registrando métricas.
 * - Repetições: se o trace variar por seed, você terá múltiplos arquivos (o
 * executor mede cada arquivo).
 */
public class ArrayListTraceExecutor {

    // Ajuste caminhos conforme seu projeto (mantive o mesmo padrão do gerador)
    private static final File TRACE_DIR = new File("data/traceArrayList");
    private static final File OUT_DIR = new File("data/resultsArrayList");
    // Warm-up por trace (JIT)
    private static final int WARMUP_RUNS = 3;

    // Se true, ignora operações inválidas (IndexOutOfBounds) e conta falhas.
    // Se false, falha o trace imediatamente.
    private static final boolean IGNORE_INVALID_OPS = true;

    // Amostragem de heap durante execução (a cada N operações)
    private static final int HEAP_SAMPLE_EVERY = 50_000;

    // Caso seu trace tenha GET com índice inválido (no gerador não deveria), isso
    // decide o que fazer.
    private static final int DEFAULT_GET_FALLBACK = 0;

    public static void main(String[] args) throws Exception {
        if (!TRACE_DIR.exists() || !TRACE_DIR.isDirectory()) {
            throw new IllegalStateException("TRACE_DIR não existe ou não é diretório: " + TRACE_DIR.getAbsolutePath());
        }

        OUT_DIR.mkdirs();
        File outCsv = new File(OUT_DIR, "results.csv");

        List<File> traces = listCsvFiles(TRACE_DIR);
        traces.sort(Comparator.comparing(File::getName));

        System.out.println("[INFO] Traces encontrados: " + traces.size());
        System.out.println("[INFO] Warm-up runs por trace: " + WARMUP_RUNS);
        System.out.println("[INFO] Saída: " + outCsv.getAbsolutePath());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outCsv))) {
            // Cabeçalho (flat) para facilitar pandas
            bw.write(String.join(",",
                    "trace_file",
                    "scenario",
                    "n_ops",
                    "warmup_runs",
                    "time_total_ns",
                    "ops_per_sec",
                    "heap_used_before_bytes",
                    "heap_used_after_bytes",
                    "heap_peak_bytes",
                    "invalid_ops",
                    "count_ADD_END", "time_ADD_END_ns",
                    "count_ADD_AT", "time_ADD_AT_ns",
                    "count_GET", "time_GET_ns",
                    "count_REMOVE_AT", "time_REMOVE_AT_ns",
                    "count_INDEX_OF", "time_INDEX_OF_ns"));
            bw.newLine();

            for (File trace : traces) {
                System.out.println("\n[TRACE] " + trace.getName());

                // Warm-up (JIT)
                for (int i = 0; i < WARMUP_RUNS; i++) {
                    runTrace(trace, false);
                }

                // Medição
                RunResult rr = runTrace(trace, true);

                bw.write(rr.toCsvLine());
                bw.newLine();
                bw.flush();

                System.out.println("[OK] time_total_ms=" + (rr.timeTotalNs / 1_000_000.0)
                        + " | ops=" + rr.nOps
                        + " | invalid=" + rr.invalidOps
                        + " | heap_before_MB=" + (rr.heapBefore / (1024.0 * 1024.0))
                        + " | heap_after_MB=" + (rr.heapAfter / (1024.0 * 1024.0))
                        + " | heap_peak_MB=" + (rr.heapPeak / (1024.0 * 1024.0)));
            }
        }
    }

    private static List<File> listCsvFiles(File dir) {
        File[] files = dir.listFiles((d, name) -> name.toLowerCase(Locale.ROOT).endsWith(".csv"));
        if (files == null)
            return Collections.emptyList();
        return Arrays.asList(files);
    }

    /**
     * Executa um trace. Se measure=false, roda em modo warm-up (sem GC
     * estabilizador e sem métricas detalhadas).
     * Se measure=true, coleta métricas e retorna RunResult.
     */
    private static RunResult runTrace(File trace, boolean measure) throws IOException, InterruptedException {
        if (measure)
            stabilizeGC();

        long heapBefore = measure ? usedHeapBytes() : -1L;
        long heapPeak = heapBefore;

        ArrayList list = new ArrayList(); // SUA implementação

        EnumMap<Op, Long> timeByOp = new EnumMap<>(Op.class);
        EnumMap<Op, Integer> countByOp = new EnumMap<>(Op.class);
        for (Op op : Op.values()) {
            timeByOp.put(op, 0L);
            countByOp.put(op, 0);
        }

        long invalidOps = 0L;
        long nOps = 0L;

        long tStart = measure ? System.nanoTime() : 0L;

        try (BufferedReader br = new BufferedReader(new FileReader(trace))) {
            String line = br.readLine(); // header
            if (line == null)
                throw new IllegalStateException("Trace vazio: " + trace.getName());

            while ((line = br.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] parts = splitCsvLine(line);
                String opStr = parts[0].trim();

                Op op = Op.from(opStr);
                if (op == null) {
                    // desconhecido: ignora ou falha
                    if (IGNORE_INVALID_OPS) {
                        invalidOps++;
                        continue;
                    }
                    throw new IllegalStateException("Operação desconhecida: " + opStr + " em " + trace.getName());
                }

                long opStart = measure ? System.nanoTime() : 0L;
                boolean ok = executeOp(list, op, parts);
                long opEnd = measure ? System.nanoTime() : 0L;

                nOps++;

                if (!ok) {
                    invalidOps++;
                    if (!IGNORE_INVALID_OPS) {
                        throw new IllegalStateException("Operação inválida em " + trace.getName() + " linha: " + line);
                    }
                } else {
                    if (measure) {
                        timeByOp.put(op, timeByOp.get(op) + (opEnd - opStart));
                        countByOp.put(op, countByOp.get(op) + 1);
                    }
                }

                if (measure && (nOps % HEAP_SAMPLE_EVERY == 0)) {
                    long cur = usedHeapBytes();
                    if (cur > heapPeak)
                        heapPeak = cur;
                }
            }
        }

        long tEnd = measure ? System.nanoTime() : 0L;
        long timeTotalNs = measure ? (tEnd - tStart) : -1L;

        if (measure)
            stabilizeGC();
        long heapAfter = measure ? usedHeapBytes() : -1L;
        if (measure) {
            long cur = usedHeapBytes();
            if (cur > heapPeak)
                heapPeak = cur;
        }

        RunResult rr = new RunResult();
        rr.traceFile = trace.getName();
        rr.scenario = inferScenario(trace.getName());
        rr.nOps = nOps;
        rr.warmupRuns = WARMUP_RUNS;
        rr.timeTotalNs = timeTotalNs;
        rr.heapBefore = heapBefore;
        rr.heapAfter = heapAfter;
        rr.heapPeak = heapPeak;
        rr.invalidOps = invalidOps;

        rr.count_ADD_END = countByOp.get(Op.ADD_END);
        rr.time_ADD_END = timeByOp.get(Op.ADD_END);

        rr.count_ADD_AT = countByOp.get(Op.ADD_AT);
        rr.time_ADD_AT = timeByOp.get(Op.ADD_AT);

        rr.count_GET = countByOp.get(Op.GET);
        rr.time_GET = timeByOp.get(Op.GET);

        rr.count_REMOVE_AT = countByOp.get(Op.REMOVE_AT);
        rr.time_REMOVE_AT = timeByOp.get(Op.REMOVE_AT);

        rr.count_INDEX_OF = countByOp.get(Op.INDEX_OF);
        rr.time_INDEX_OF = timeByOp.get(Op.INDEX_OF);

        return rr;
    }

    private static boolean executeOp(ArrayList list, Op op, String[] parts) {
        try {
            switch (op) {
                case ADD_END: {
                    int element = parseIntSafe(parts, 1, 0);
                    list.add(element);
                    return true;
                }
                case ADD_AT: {
                    int index = parseIntSafe(parts, 1, 0);
                    int element = parseIntSafe(parts, 2, 0);
                    list.add(index, element);
                    return true;
                }
                case GET: {
                    int index = parseIntSafe(parts, 1, DEFAULT_GET_FALLBACK);
                    list.get(index);
                    return true;
                }
                case REMOVE_AT: {
                    int index = parseIntSafe(parts, 1, 0);
                    list.remove(index);
                    return true;
                }
                case INDEX_OF: {
                    int element = parseIntSafe(parts, 1, 0);
                    list.indexOf(element);
                    return true;
                }
                default:
                    return false;
            }
        } catch (RuntimeException ex) {
            // IndexOutOfBoundsException etc.
            return false;
        }
    }

    private static int parseIntSafe(String[] parts, int idx, int fallback) {
        if (parts.length <= idx)
            return fallback;
        String s = parts[idx];
        if (s == null)
            return fallback;
        s = s.trim();
        if (s.isEmpty())
            return fallback;
        return Integer.parseInt(s);
    }

    /**
     * Split simples para CSV sem aspas/escapes (seu gerador não produz aspas).
     */
    private static String[] splitCsvLine(String line) {
        // garante sempre 3 colunas
        String[] raw = line.split(",", -1);
        if (raw.length >= 3)
            return raw;
        String[] padded = new String[] { "", "", "" };
        for (int i = 0; i < raw.length; i++)
            padded[i] = raw[i];
        return padded;
    }

    private static String inferScenario(String filename) {
        // esperado: scenario_<n>_seed<k>.csv ou scenario_1M.csv etc.
        int idx = filename.indexOf('_');
        if (idx <= 0)
            return "unknown";
        return filename.substring(0, idx);
    }

    private static void stabilizeGC() throws InterruptedException {
        System.gc();
        Thread.sleep(50);
        System.gc();
        Thread.sleep(50);
    }

    private static long usedHeapBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private enum Op {
        ADD_END, ADD_AT, GET, REMOVE_AT, INDEX_OF;

        static Op from(String s) {
            if (s == null)
                return null;
            try {
                return Op.valueOf(s.trim());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    /**
     * Resultado “flat” para CSV.
     */
    private static class RunResult {
        String traceFile;
        String scenario;
        long nOps;
        int warmupRuns;

        long timeTotalNs;
        long heapBefore;
        long heapAfter;
        long heapPeak;
        long invalidOps;

        int count_ADD_END;
        long time_ADD_END;

        int count_ADD_AT;
        long time_ADD_AT;

        int count_GET;
        long time_GET;

        int count_REMOVE_AT;
        long time_REMOVE_AT;

        int count_INDEX_OF;
        long time_INDEX_OF;

        double opsPerSec() {
            if (timeTotalNs <= 0)
                return 0.0;
            return nOps / (timeTotalNs / 1_000_000_000.0);
        }

        String toCsvLine() {
            return String.join(",",
                    esc(traceFile),
                    esc(scenario),
                    String.valueOf(nOps),
                    String.valueOf(warmupRuns),
                    String.valueOf(timeTotalNs),
                    String.valueOf(opsPerSec()),
                    String.valueOf(heapBefore),
                    String.valueOf(heapAfter),
                    String.valueOf(heapPeak),
                    String.valueOf(invalidOps),

                    String.valueOf(count_ADD_END), String.valueOf(time_ADD_END),
                    String.valueOf(count_ADD_AT), String.valueOf(time_ADD_AT),
                    String.valueOf(count_GET), String.valueOf(time_GET),
                    String.valueOf(count_REMOVE_AT), String.valueOf(time_REMOVE_AT),
                    String.valueOf(count_INDEX_OF), String.valueOf(time_INDEX_OF));
        }

        private String esc(String s) {
            if (s == null)
                return "";
            // sem vírgulas no nome; se tiver, você pode implementar aspas
            return s.replace(",", "_");
        }
    }
}