package dev.ProjetoEDA.service.bench;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Bench {

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decrescente;
    protected static List<Integer> entradas;

    protected static final int REPETICOES = 18;

    public abstract void run();

    protected abstract void test(BufferedWriter aux) throws IOException;

    protected void experimento(
            int tamanhoEntrada,
            String tipoDado,
            String casoTest,
            String estrutura,
            BufferedWriter writer
    ) {

        List<Integer> dados = getDados(tipoDado);

        long[] tempos = new long[REPETICOES];
        long[] memorias = new long[REPETICOES];

        for (int s = 0; s < REPETICOES; s++) {

            long memoriaAntes = getProcessRssBytes();

            long tempoAntes = System.nanoTime();

            executarCaso(dados, tamanhoEntrada, casoTest);

            long tempoDepois = System.nanoTime();

            long memoriaDepois = getProcessRssBytes();

            tempos[s] = tempoDepois - tempoAntes;
            memorias[s] = memoriaDepois - memoriaAntes;
        }

        long tempoMediana = calcularMediana(tempos);
        long memoriaMediana = calcularMediana(memorias);

        try {
            writer.write(
                    tamanhoEntrada + "," +
                    tipoDado + "," +
                    casoTest + "," +
                    estrutura + "," +
                    tempoMediana + "," +
                    memoriaMediana + "\n"
            );
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    protected void executarCaso(List<Integer> dados, int tamanhoEntrada, String casoTest) {

        switch (casoTest) {

            case "100I0R0S":
                executarI100_R0_S0(dados, tamanhoEntrada);
                break;

            case "50I50R0S":
                executarI50_R50_S0(dados, tamanhoEntrada);
                break;

            case "75I25R0S":
                executarI75_R25_S0(dados, tamanhoEntrada);
                break;

            case "50I25R25S":
                executarI50_R25_S25(dados, tamanhoEntrada);
                break;

            case "50I0R50S":
                executarI50_R0_S50(dados, tamanhoEntrada);
                break;

            default:
                throw new IllegalArgumentException("Caso inválido: " + casoTest);
        }
    }

    protected abstract void executarI100_R0_S0(List<Integer> dados, int n);

    protected abstract void executarI50_R50_S0(List<Integer> dados, int n);

    protected abstract void executarI75_R25_S0(List<Integer> dados, int n);

    protected abstract void executarI50_R25_S25(List<Integer> dados, int n);

    protected abstract void executarI50_R0_S50(List<Integer> dados, int n);

    protected List<Integer> getDados(String tipo) {

        if (tipo.equals("random")) return random;

        if (tipo.equals("crescente")) return crescente;

        if (tipo.equals("decrescente")) return decrescente;

        return entradas;
    }

    protected long calcularMediana(long[] valores) {

        Arrays.sort(valores);

        int n = valores.length;

        if (n % 2 == 1) {
            return valores[n / 2];
        }

        return (valores[n / 2 - 1] + valores[n / 2]) / 2;
    }

    protected void lerDados() throws IOException {

        String caminhoRandom =
                "src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv";

        String caminhoCrescente =
                "src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv";

        String caminhoDecrescente =
                "src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv";

        String caminhoEntradas =
                "src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntrada.csv";

        random = Files.lines(Paths.get(caminhoRandom))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        crescente = Files.lines(Paths.get(caminhoCrescente))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        decrescente = Files.lines(Paths.get(caminhoDecrescente))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        entradas = Files.lines(Paths.get(caminhoEntradas))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    // RSS (Resident Set Size) do processo Java em bytes
    private static long getProcessRssBytes() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/self/status"))) {
            String s;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("VmRSS:")) {
                    String[] parts = s.trim().split("\\s+");
                    long kb = Long.parseLong(parts[1]); // vem em kB
                    return kb * 1024L; // converte pra bytes
                }
            }
        } catch (IOException ignored) {}
        return -1L;
    }

}