package dev.ProjetoEDA.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ProjetoEDA.model.Estrutura;

/**
 * Classe abstrata base para benchmarks de estruturas.
 *
 * <p>Executa automaticamente:</p>
 * <ul>
 *   <li>Benchmarks de referência por operação: ADD, SEARCH e REMOVE</li>
 *   <li>Benchmarks de workload misto:
 *     <ul>
 *       <li>100I0R0S</li>
 *       <li>75I25R0S</li>
 *       <li>50I25R25S</li>
 *       <li>50I0R50S</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>A coluna de memória do CSV mantém o mesmo formato do projeto, mas
 * representa a memória ocupada pela estrutura após a construção do estado
 * correspondente ao experimento. Para evitar que a JVM elimine a estrutura
 * antes da leitura da heap, uma referência temporária é preservada durante
 * a medição.</p>
 */
public abstract class Bench {

    /**
     * Operações suportadas.
     */
    public enum Operacao {
        ADD,
        SEARCH,
        REMOVE
    }

    /**
     * Casos mistos suportados.
     */
    public enum CasoMisto {
        C100I0R0S("100I0R0S"),
        C75I25R0S("75I25R0S"),
        C50I25R25S("50I25R25S"),
        C50I0R50S("50I0R50S");

        private final String nome;

        CasoMisto(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }

    protected static final String[] ORDENS = { "random", "crescente", "decrescente" };

    protected static final Operacao[] OPERACOES_ISOLADAS = {
        Operacao.ADD,
        Operacao.SEARCH,
        Operacao.REMOVE
    };

    protected static final CasoMisto[] CASOS_MISTOS = {
        CasoMisto.C100I0R0S,
        CasoMisto.C75I25R0S,
        CasoMisto.C50I25R25S,
        CasoMisto.C50I0R50S
    };

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decrescente;

    protected static int entrada;

    private static boolean dadosCarregados = false;

    /**
     * Referência auxiliar para manter objetos vivos durante a medição
     * de memória e evitar otimizações do JIT/GC antes da leitura final.
     */
    private static volatile Object memoriaSink;

    /**
     * Warmup da JVM.
     */
    protected static final int RODADAS_WARMUP = 3;

    /**
     * Número de amostras reais.
     */
    protected static final int RODADAS_MEDICAO = 9;

    /**
     * Quantidade de operações por amostra em ADD e SEARCH.
     */
    protected static final int REPETICOES_POR_AMOSTRA = 2000;

    /**
     * Quantidade de amostras unitárias para REMOVE.
     *
     * <p>Cada repetição recria a estrutura com tamanho n e remove
     * apenas um elemento, evitando que o custo acumulado de várias
     * remoções sequenciais pareça quadrático.</p>
     */
    protected static final int REPETICOES_REMOVE = 200;

    public void definirEntrada(int tamanhoEntrada) {
        if (tamanhoEntrada <= 0) {
            throw new IllegalArgumentException("O tamanho da entrada deve ser maior que zero.");
        }

        entrada = tamanhoEntrada;
    }

    /**
     * Executa todos os benchmarks.
     */
    public void run() {
        try {
            lerDados();
            executarOperacoesIsoladas();
            executarCasosMistos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void executarOperacoesIsoladas() throws IOException {
        for (Operacao operacao : OPERACOES_ISOLADAS) {
            for (String ordem : ORDENS) {
                executarExperimentoOperacao(ordem, operacao);
            }
        }
    }

    protected void executarCasosMistos() throws IOException {
        for (CasoMisto caso : CASOS_MISTOS) {
            for (String ordem : ORDENS) {
                executarExperimentoWorkload(ordem, caso);
            }
        }
    }

    protected void executarWarmupOperacao(List<Integer> dados, int n, Operacao operacao) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            medirOperacao(dados, n, operacao, i);
        }
    }

    /**
     * Benchmark de referência por operação.
     */
    protected void executarExperimentoOperacao(String ordem, Operacao operacao) throws IOException {
        List<Integer> dados = getDadosPorOrdem(ordem);
        String pathSaida = gerarPathArquivoSaidaOperacao(ordem, operacao);

        try (BufferedWriter writer = inicializarArquivoDeSaida(pathSaida)) {
            for (int n : gerarEscalas()) {
                executarWarmupOperacao(dados, n, operacao);

                long[] tempos = new long[RODADAS_MEDICAO];
                long[] memorias = new long[RODADAS_MEDICAO];

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoOperacao resultado = medirOperacao(dados, n, operacao, rodada);
                    tempos[rodada] = resultado.tempoMedio;
                    memorias[rodada] = medirMemoriaEstrutura(dados, n);
                }

                gravarLinhaResultado(
                        writer,
                        n,
                        operacao.name(),
                        calcularMediana(tempos),
                        calcularMediana(memorias)
                );
            }
        }
    }

    /**
     * Mede uma operação de forma controlada.
     */
    protected ResultadoOperacao medirOperacao(List<Integer> dados, int n, Operacao operacao, int rodada) {
        switch (operacao) {
            case ADD:
                return medirAdd(dados, n, rodada);
            case SEARCH:
                return medirSearch(dados, n, rodada);
            case REMOVE:
                return medirRemove(dados, n, rodada);
            default:
                throw new IllegalArgumentException("Operação inválida: " + operacao);
        }
    }

    /**
     * Mede ADD em bloco.
     */
    protected ResultadoOperacao medirAdd(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_POR_AMOSTRA; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valorNovo = dados.get(n + r + rodada);

            long inicio = System.nanoTime();
            estrutura.add(valorNovo);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_POR_AMOSTRA;
        return new ResultadoOperacao(tempoMedio, 0);
    }

    /**
     * Mede SEARCH em uma estrutura previamente populada com n elementos.
     */
    protected ResultadoOperacao medirSearch(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_POR_AMOSTRA; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valorBusca = dados.get(n - 1);

            long inicio = System.nanoTime();
            estrutura.search(valorBusca);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_POR_AMOSTRA;
        return new ResultadoOperacao(tempoMedio, 0);
    }

    /**
     * Mede REMOVE de forma unitária.
     *
     * <p>Cada repetição recria uma estrutura de tamanho n e remove um único
     * elemento. Isso faz o gráfico refletir o custo da operação remove,
     * e não o custo acumulado de uma sequência de remoções.</p>
     */
    protected ResultadoOperacao medirRemove(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_REMOVE; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valor = dados.get(n - 1);

            long inicio = System.nanoTime();
            estrutura.remove(valor);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_REMOVE;
        return new ResultadoOperacao(tempoMedio, 0);
    }

    /**
     * Workload misto.
     *
     * <p>Esse modo continua útil para simular uso real, mas não deve ser usado
     * como referência primária de Big-O da operação individual.</p>
     */
    protected void executarExperimentoWorkload(String ordem, CasoMisto caso) throws IOException {
        List<Integer> dados = getDadosPorOrdem(ordem);
        String pathSaida = gerarPathArquivoSaidaWorkload(ordem, caso);

        try (BufferedWriter writer = inicializarArquivoDeSaida(pathSaida)) {
            for (int n : gerarEscalas()) {
                executarWarmupWorkload(dados, n, caso, ordem);

                long[] temposAdd = new long[RODADAS_MEDICAO];
                long[] temposSearch = new long[RODADAS_MEDICAO];
                long[] temposRemove = new long[RODADAS_MEDICAO];
                long[] memorias = new long[RODADAS_MEDICAO];

                boolean temAdd = false;
                boolean temSearch = false;
                boolean temRemove = false;

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoWorkload resultado = executarRoundWorkload(dados, n, caso, ordem, rodada);

                    if (resultado.qtdAdd > 0) {
                        temposAdd[rodada] = resultado.tempoAdd / resultado.qtdAdd;
                        temAdd = true;
                    }

                    if (resultado.qtdSearch > 0) {
                        temposSearch[rodada] = resultado.tempoSearch / resultado.qtdSearch;
                        temSearch = true;
                    }

                    if (resultado.qtdRemove > 0) {
                        temposRemove[rodada] = resultado.tempoRemove / resultado.qtdRemove;
                        temRemove = true;
                    }

                    memorias[rodada] = medirMemoriaWorkload(dados, n, caso, ordem, rodada);
                }

                long memoriaMediana = calcularMediana(memorias);

                if (temAdd) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.ADD.name(),
                            calcularMediana(temposAdd),
                            memoriaMediana
                    );
                }

                if (temSearch) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.SEARCH.name(),
                            calcularMediana(temposSearch),
                            memoriaMediana
                    );
                }

                if (temRemove) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.REMOVE.name(),
                            calcularMediana(temposRemove),
                            memoriaMediana
                    );
                }
            }
        }
    }

    protected void executarWarmupWorkload(List<Integer> dados, int n, CasoMisto caso, String ordem) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            executarRoundWorkload(dados, n, caso, ordem, i);
        }
    }

    protected ResultadoWorkload executarRoundWorkload(
            List<Integer> dados,
            int n,
            CasoMisto caso,
            String ordem,
            int rodada
    ) {
        ResultadoWorkload resultado = new ResultadoWorkload();

        Estrutura estrutura = criarEstrutura();
        java.util.ArrayList<Integer> ativos = new java.util.ArrayList<>();

        int addExecutados = 0;

        Random randomizador = new Random(
                31L * n
                        + 17L * rodada
                        + 13L * Math.abs(ordem.hashCode())
                        + 97L * caso.ordinal()
        );

        for (int passo = 0; passo < n; passo++) {
            Operacao operacao = descobrirOperacaoWorkload(caso, passo, n);

            long inicio = System.nanoTime();

            switch (operacao) {
                case ADD:
                    int valorInsercao = dados.get(addExecutados);
                    estrutura.add(valorInsercao);
                    ativos.add(valorInsercao);
                    addExecutados++;
                    resultado.qtdAdd++;
                    resultado.tempoAdd += (System.nanoTime() - inicio);
                    break;

                case SEARCH:
                    if (!ativos.isEmpty()) {
                        int indiceBusca = randomizador.nextInt(ativos.size());
                        int valorBusca = ativos.get(indiceBusca);
                        estrutura.search(valorBusca);
                        resultado.qtdSearch++;
                        resultado.tempoSearch += (System.nanoTime() - inicio);
                    }
                    break;

                case REMOVE:
                    if (!ativos.isEmpty()) {
                        int indiceRemocao = randomizador.nextInt(ativos.size());
                        int valorRemocao = ativos.remove(indiceRemocao);
                        estrutura.remove(valorRemocao);
                        resultado.qtdRemove++;
                        resultado.tempoRemove += (System.nanoTime() - inicio);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Operação inválida no workload: " + operacao);
            }
        }

        return resultado;
    }

    protected Operacao descobrirOperacaoWorkload(CasoMisto caso, int passo, int n) {
        int qtdAdd = quantidadeAdd(caso, n);
        int qtdSearch = quantidadeSearch(caso, n);

        switch (caso) {
            case C100I0R0S:
                return Operacao.ADD;
            case C75I25R0S:
                return passo < qtdAdd ? Operacao.ADD : Operacao.REMOVE;
            case C50I25R25S:
                if (passo < qtdAdd) {
                    return Operacao.ADD;
                } else if (passo < qtdAdd + qtdSearch) {
                    return Operacao.SEARCH;
                } else {
                    return Operacao.REMOVE;
                }
            case C50I0R50S:
                return passo < qtdAdd ? Operacao.ADD : Operacao.SEARCH;
            default:
                throw new IllegalArgumentException("Caso misto inválido: " + caso.getNome());
        }
    }

    protected int quantidadeAdd(CasoMisto caso, int n) {
        switch (caso) {
            case C100I0R0S:
                return n;
            case C75I25R0S:
                return (int) Math.round(n * 0.75);
            case C50I25R25S:
            case C50I0R50S:
                return n / 2;
            default:
                return 0;
        }
    }

    protected int quantidadeSearch(CasoMisto caso, int n) {
        switch (caso) {
            case C50I25R25S:
                return n / 4;
            case C50I0R50S:
                return n - quantidadeAdd(caso, n);
            default:
                return 0;
        }
    }

    /**
     * Gera no máximo 10 escalas entre 100 e a entrada máxima.
     */
    protected int[] gerarEscalas() {
        int minimo = 100;
        int maximo = entrada;

        if (maximo <= minimo) {
            return new int[]{maximo};
        }

        int quantidade = 10;
        java.util.Set<Integer> escalas = new java.util.LinkedHashSet<>();

        double logMin = Math.log10(minimo);
        double logMax = Math.log10(maximo);

        for (int i = 0; i < quantidade; i++) {
            double t = (double) i / (quantidade - 1);
            double valor = Math.pow(10, logMin + (logMax - logMin) * t);
            int escala = (int) Math.round(valor);

            if (escala <= maximo) {
                escalas.add(escala);
            }
        }

        escalas.add(maximo);

        return escalas.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Distribui buscas ao longo de toda a estrutura.
     */
    protected int distribuirIndice(int iteracao, int total, int tamanho) {
        if (tamanho <= 1) {
            return 0;
        }

        int indice = (int) (((long) iteracao * tamanho) / Math.max(1, total));
        return Math.min(indice, tamanho - 1);
    }

    protected long calcularMediana(long[] valores) {
        long[] copia = Arrays.copyOf(valores, valores.length);
        Arrays.sort(copia);

        int meio = copia.length / 2;

        if (copia.length % 2 == 0) {
            return (copia[meio - 1] + copia[meio]) / 2;
        }

        return copia[meio];
    }

    protected List<Integer> getDadosPorOrdem(String ordem) {
        switch (ordem) {
            case "random":
                return random;
            case "crescente":
                return crescente;
            case "decrescente":
                return decrescente;
            default:
                throw new IllegalArgumentException("Ordem inválida: " + ordem);
        }
    }

    protected String gerarPathArquivoSaidaOperacao(String ordem, Operacao operacao) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_" + operacao.name().toLowerCase() + ".csv";
    }

    protected String gerarPathArquivoSaidaWorkload(String ordem, CasoMisto caso) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_workload_" + caso.getNome() + ".csv";
    }

    protected BufferedWriter inicializarArquivoDeSaida(String resultFilePath) throws IOException {
        File file = new File(resultFilePath);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("TamanhoEntrada,Operacao,TempoMedio(ns),MemoriaUso(bytes)\n");
        return writer;
    }

    protected void gravarLinhaResultado(
            BufferedWriter writer,
            int tamanhoEntrada,
            String operacao,
            long tempoMedio,
            long memoriaUso
    ) throws IOException {
        writer.write(
                tamanhoEntrada + "," +
                operacao + "," +
                tempoMedio + "," +
                memoriaUso + "\n"
        );
    }

    protected void lerDados() throws IOException {
        if (dadosCarregados) {
            return;
        }

        random = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv");
        crescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv");
        decrescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv");

        dadosCarregados = true;
    }

    protected List<Integer> carregarInteiros(String path) throws IOException {
        try (Stream<String> linhas = Files.lines(Paths.get(path))) {
            return linhas
                    .map(String::trim)
                    .filter(linha -> !linha.isEmpty())
                    .filter(linha -> !linha.matches(".*[a-zA-Z].*"))
                    .map(linha -> linha.split(",")[0].trim())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Retorna a quantidade de heap usada pela JVM em bytes.
     */
    protected long getHeapUsedBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /**
     * Faz uma pausa curta para reduzir ruído após solicitação de GC.
     */
    protected void pausar(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Mede a memória ocupada pela estrutura com n elementos.
     *
     * <p>O GC é solicitado antes da construção para reduzir ruído da heap já
     * existente. Após montar a estrutura, a leitura é feita imediatamente,
     * mantendo referência viva ao objeto medido.</p>
     */
    protected long medirMemoriaEstrutura(List<Integer> dados, int n) {
        System.gc();
        pausar(50);

        long antes = getHeapUsedBytes();

        Estrutura estrutura = criarEstrutura();
        for (int i = 0; i < n; i++) {
            estrutura.add(dados.get(i));
        }

        memoriaSink = estrutura;
        long depois = getHeapUsedBytes();
        long memoria = Math.max(0, depois - antes);
        memoriaSink = null;

        return memoria;
    }

    /**
     * Mede a memória do estado final de um workload.
     *
     * <p>A medição replica a mesma lógica do workload, mas captura a heap
     * utilizada pela estrutura ao final da sequência de operações.</p>
     */
    protected long medirMemoriaWorkload(
            List<Integer> dados,
            int n,
            CasoMisto caso,
            String ordem,
            int rodada
    ) {
        System.gc();
        pausar(50);

        long antes = getHeapUsedBytes();

        Estrutura estrutura = criarEstrutura();
        java.util.ArrayList<Integer> ativos = new java.util.ArrayList<>();

        int addExecutados = 0;

        Random randomizador = new Random(
                31L * n
                        + 17L * rodada
                        + 13L * Math.abs(ordem.hashCode())
                        + 97L * caso.ordinal()
        );

        for (int passo = 0; passo < n; passo++) {
            Operacao operacao = descobrirOperacaoWorkload(caso, passo, n);

            switch (operacao) {
                case ADD:
                    int valorInsercao = dados.get(addExecutados);
                    estrutura.add(valorInsercao);
                    ativos.add(valorInsercao);
                    addExecutados++;
                    break;

                case SEARCH:
                    if (!ativos.isEmpty()) {
                        int indiceBusca = randomizador.nextInt(ativos.size());
                        int valorBusca = ativos.get(indiceBusca);
                        estrutura.search(valorBusca);
                    }
                    break;

                case REMOVE:
                    if (!ativos.isEmpty()) {
                        int indiceRemocao = randomizador.nextInt(ativos.size());
                        int valorRemocao = ativos.remove(indiceRemocao);
                        estrutura.remove(valorRemocao);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Operação inválida no workload: " + operacao);
            }
        }

        memoriaSink = estrutura;
        long depois = getHeapUsedBytes();
        long memoria = Math.max(0, depois - antes);
        memoriaSink = null;

        return memoria;
    }

    protected abstract String getNomeEstrutura();

    protected abstract Estrutura criarEstrutura();

    /**
     * Resultado de uma operação isolada.
     */
    protected static class ResultadoOperacao {
        long tempoMedio;
        long memoriaMedia;

        ResultadoOperacao(long tempoMedio, long memoriaMedia) {
            this.tempoMedio = tempoMedio;
            this.memoriaMedia = memoriaMedia;
        }
    }

    /**
     * Resultado de um workload.
     */
    protected static class ResultadoWorkload {
        long tempoAdd;
        long tempoSearch;
        long tempoRemove;

        int qtdAdd;
        int qtdSearch;
        int qtdRemove;
    }
}
