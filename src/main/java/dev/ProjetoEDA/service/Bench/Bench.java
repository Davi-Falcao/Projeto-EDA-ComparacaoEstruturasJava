package dev.ProjetoEDA.service.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * <p>Esta classe executa automaticamente:</p>
 *
 * <ul>
 *   <li>Benchmarks de operação isolada: ADD, SEARCH e REMOVE</li>
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
 * <p>Os benchmarks de operação isolada servem como referência
 * para análise assintótica. Os benchmarks mistos servem para
 * representar workloads reais.</p>
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

        /**
         * Retorna o nome textual do caso.
         *
         * @return nome do caso
         */
        public String getNome() {
            return nome;
        }
    }

    /**
     * Ordens de entrada suportadas.
     */
    protected static final String[] ORDENS = { "random", "crescente", "decrescente" };

    /**
     * Operações isoladas executadas automaticamente.
     */
    protected static final Operacao[] OPERACOES_ISOLADAS = {
        Operacao.ADD,
        Operacao.SEARCH,
        Operacao.REMOVE
    };

    /**
     * Casos mistos executados automaticamente.
     */
    protected static final CasoMisto[] CASOS_MISTOS = {
        CasoMisto.C100I0R0S,
        CasoMisto.C75I25R0S,
        CasoMisto.C50I25R25S,
        CasoMisto.C50I0R50S
    };

    /**
     * Dados de entrada carregados por ordem.
     */
    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decrescente;

    /**
     * Tamanho máximo da entrada do experimento.
     */
    protected static int entrada;

    /**
     * Indica se os dados já foram carregados.
     */
    private static boolean dadosCarregados = false;

    /**
     * Quantidade de rodadas de warmup.
     */
    protected static final int RODADAS_WARMUP = 3;

    /**
     * Quantidade de rodadas reais de medição.
     */
    protected static final int RODADAS_MEDICAO = 9;

    /**
     * Número de repetições por amostra para operação isolada.
     */
    protected static final int REPETICOES_POR_AMOSTRA = 2000;

    /**
     * Define o tamanho máximo da entrada.
     *
     * @param tamanhoEntrada tamanho desejado
     */
    public void definirEntrada(int tamanhoEntrada) {
        if (tamanhoEntrada <= 0) {
            throw new IllegalArgumentException("O tamanho da entrada deve ser maior que zero.");
        }

        entrada = tamanhoEntrada;
    }

    /**
     * Executa todos os benchmarks automaticamente:
     * operações isoladas e workloads mistos.
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

    /**
     * Executa automaticamente todos os benchmarks de operação isolada.
     *
     * @throws IOException se houver erro de escrita
     */
    protected void executarOperacoesIsoladas() throws IOException {
        for (Operacao operacao : OPERACOES_ISOLADAS) {
            for (String ordem : ORDENS) {
                executarExperimentoOperacao(ordem, operacao);
            }
        }
    }

    /**
     * Executa automaticamente todos os benchmarks de workload misto.
     *
     * @throws IOException se houver erro de escrita
     */
    protected void executarCasosMistos() throws IOException {
        for (CasoMisto caso : CASOS_MISTOS) {
            for (String ordem : ORDENS) {
                executarExperimentoWorkload(ordem, caso);
            }
        }
    }

    /**
     * Executa experimento de operação isolada para uma ordem específica.
     *
     * @param ordem ordem da entrada
     * @param operacao operação alvo
     * @throws IOException se houver erro de escrita
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
                    Estrutura estrutura = criarEstrutura();
                    prepararEstruturaParaOperacao(estrutura, dados, n, operacao);

                    long memoriaAntes = getHeapUsedBytes();
                    long inicio = System.nanoTime();
                    int qtdExecutada = executarBlocoOperacao(estrutura, dados, n, operacao, REPETICOES_POR_AMOSTRA, rodada);
                    long fim = System.nanoTime();
                    long memoriaDepois = getHeapUsedBytes();

                    tempos[rodada] = (fim - inicio) / Math.max(1, qtdExecutada);
                    memorias[rodada] = Math.max(0, memoriaDepois - memoriaAntes);
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
     * Executa experimento de workload misto para uma ordem específica.
     *
     * @param ordem ordem da entrada
     * @param caso caso misto
     * @throws IOException se houver erro de escrita
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

                long[] memAdd = new long[RODADAS_MEDICAO];
                long[] memSearch = new long[RODADAS_MEDICAO];
                long[] memRemove = new long[RODADAS_MEDICAO];

                boolean temAdd = false;
                boolean temSearch = false;
                boolean temRemove = false;

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoWorkload resultado = executarRoundWorkload(dados, n, caso, ordem, rodada);

                    if (resultado.qtdAdd > 0) {
                        temposAdd[rodada] = resultado.tempoAdd / resultado.qtdAdd;
                        memAdd[rodada] = resultado.memAdd / resultado.qtdAdd;
                        temAdd = true;
                    }

                    if (resultado.qtdSearch > 0) {
                        temposSearch[rodada] = resultado.tempoSearch / resultado.qtdSearch;
                        memSearch[rodada] = resultado.memSearch / resultado.qtdSearch;
                        temSearch = true;
                    }

                    if (resultado.qtdRemove > 0) {
                        temposRemove[rodada] = resultado.tempoRemove / resultado.qtdRemove;
                        memRemove[rodada] = resultado.memRemove / resultado.qtdRemove;
                        temRemove = true;
                    }
                }

                if (temAdd) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.ADD.name(),
                            calcularMediana(temposAdd),
                            calcularMediana(memAdd)
                    );
                }

                if (temSearch) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.SEARCH.name(),
                            calcularMediana(temposSearch),
                            calcularMediana(memSearch)
                    );
                }

                if (temRemove) {
                    gravarLinhaResultado(
                            writer,
                            n,
                            Operacao.REMOVE.name(),
                            calcularMediana(temposRemove),
                            calcularMediana(memRemove)
                    );
                }
            }
        }
    }

    /**
     * Executa warmup para benchmark de operação isolada.
     *
     * @param dados dados de entrada
     * @param n tamanho atual
     * @param operacao operação alvo
     */
    protected void executarWarmupOperacao(List<Integer> dados, int n, Operacao operacao) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            Estrutura estrutura = criarEstrutura();
            prepararEstruturaParaOperacao(estrutura, dados, n, operacao);
            executarBlocoOperacao(
                    estrutura,
                    dados,
                    n,
                    operacao,
                    Math.min(REPETICOES_POR_AMOSTRA, Math.max(10, n)),
                    i
            );
        }
    }

    /**
     * Executa warmup para benchmark de workload.
     *
     * @param dados dados de entrada
     * @param n tamanho atual
     * @param caso caso misto
     * @param ordem ordem da entrada
     */
    protected void executarWarmupWorkload(List<Integer> dados, int n, CasoMisto caso, String ordem) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            executarRoundWorkload(dados, n, caso, ordem, i);
        }
    }

    /**
     * Prepara a estrutura para operação isolada.
     *
     * @param estrutura estrutura alvo
     * @param dados dados de entrada
     * @param n tamanho atual
     * @param operacao operação alvo
     */
    protected void prepararEstruturaParaOperacao(Estrutura estrutura, List<Integer> dados, int n, Operacao operacao) {
        if (operacao == Operacao.ADD) {
            return;
        }

        for (int i = 0; i < n; i++) {
            estrutura.add(dados.get(i));
        }
    }

    /**
     * Executa bloco de operação isolada.
     *
     * @param estrutura estrutura alvo
     * @param dados dados de entrada
     * @param n tamanho atual
     * @param operacao operação alvo
     * @param repeticoes quantidade de repetições
     * @param semente índice da rodada
     * @return quantidade real de operações executadas
     */
    protected int executarBlocoOperacao(
            Estrutura estrutura,
            List<Integer> dados,
            int n,
            Operacao operacao,
            int repeticoes,
            int semente
    ) {
        switch (operacao) {
            case ADD:
                for (int i = 0; i < repeticoes; i++) {
                    int indice = (i + semente) % dados.size();
                    estrutura.add(dados.get(indice));
                }
                return repeticoes;

            case SEARCH:
                for (int i = 0; i < repeticoes; i++) {
                    int indice = distribuirIndice(i, repeticoes, n);
                    estrutura.search(dados.get(indice));
                }
                return repeticoes;

            case REMOVE:
                java.util.ArrayList<Integer> ativos = new java.util.ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    ativos.add(dados.get(i));
                }

                Random randomizador = new Random(97L * n + semente);
                int limite = Math.min(repeticoes, n);

                for (int i = 0; i < limite; i++) {
                    int indice = randomizador.nextInt(ativos.size());
                    int valor = ativos.remove(indice);
                    estrutura.remove(valor);
                }
                return limite;

            default:
                throw new IllegalArgumentException("Operação inválida: " + operacao);
        }
    }

    /**
     * Executa uma rodada completa de workload misto.
     *
     * @param dados dados de entrada
     * @param n quantidade total de operações do workload
     * @param caso caso misto
     * @param ordem ordem da entrada
     * @param rodada índice da rodada
     * @return resultado agregado da rodada
     */
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

            long memoriaAntes = getHeapUsedBytes();
            long inicio = System.nanoTime();

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

            long fim = System.nanoTime();
            long memoriaDepois = getHeapUsedBytes();

            long tempo = fim - inicio;
            long memoria = Math.max(0, memoriaDepois - memoriaAntes);

            switch (operacao) {
                case ADD:
                    resultado.tempoAdd += tempo;
                    resultado.memAdd += memoria;
                    resultado.qtdAdd++;
                    break;

                case SEARCH:
                    if (!ativos.isEmpty()) {
                        resultado.tempoSearch += tempo;
                        resultado.memSearch += memoria;
                        resultado.qtdSearch++;
                    }
                    break;

                case REMOVE:
                    if (resultado.qtdAdd > resultado.qtdRemove) {
                        resultado.tempoRemove += tempo;
                        resultado.memRemove += memoria;
                        resultado.qtdRemove++;
                    }
                    break;

                default:
                    break;
            }
        }

        return resultado;
    }

    /**
     * Descobre qual operação deve ser executada em um passo
     * do caso misto.
     *
     * @param caso caso misto
     * @param passo passo atual
     * @param n total de operações
     * @return operação correspondente
     */
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

    /**
     * Calcula quantidade de inserções para o caso.
     *
     * @param caso caso misto
     * @param n total de operações
     * @return quantidade de inserções
     */
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

    /**
     * Calcula quantidade de buscas para o caso.
     *
     * @param caso caso misto
     * @param n total de operações
     * @return quantidade de buscas
     */
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
     * Distribui índices por toda a estrutura para evitar viés
     * concentrado no início.
     *
     * @param iteracao iteração atual
     * @param total total de iterações
     * @param tamanho tamanho da estrutura
     * @return índice distribuído
     */
    protected int distribuirIndice(int iteracao, int total, int tamanho) {
        if (tamanho <= 1) {
            return 0;
        }

        int indice = (int) (((long) iteracao * tamanho) / Math.max(1, total));
        return Math.min(indice, tamanho - 1);
    }

    /**
     * Gera no máximo 10 escalas distribuídas logaritmicamente
     * entre 100 e o valor máximo de entrada.
     *
     * Essa abordagem gera pontos bem distribuídos para análise
     * de complexidade assintótica sem poluir os gráficos.
     *
     * @return vetor de tamanhos de entrada
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
     * Calcula mediana de um vetor.
     *
     * @param valores valores de entrada
     * @return mediana
     */
    protected long calcularMediana(long[] valores) {
        long[] copia = Arrays.copyOf(valores, valores.length);
        Arrays.sort(copia);

        int meio = copia.length / 2;

        if (copia.length % 2 == 0) {
            return (copia[meio - 1] + copia[meio]) / 2;
        }

        return copia[meio];
    }

    /**
     * Obtém os dados de acordo com a ordem escolhida.
     *
     * @param ordem ordem da entrada
     * @return lista de dados correspondente
     */
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

    /**
     * Gera o caminho do arquivo CSV para benchmark de operação isolada.
     *
     * @param ordem ordem da entrada
     * @param operacao operação alvo
     * @return caminho do arquivo
     */
    protected String gerarPathArquivoSaidaOperacao(String ordem, Operacao operacao) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_" + operacao.name().toLowerCase() + ".csv";
    }

    /**
     * Gera o caminho do arquivo CSV para benchmark de workload misto.
     *
     * @param ordem ordem da entrada
     * @param caso caso misto
     * @return caminho do arquivo
     */
    protected String gerarPathArquivoSaidaWorkload(String ordem, CasoMisto caso) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_workload_" + caso.getNome() + ".csv";
    }

    /**
     * Inicializa um arquivo CSV de saída.
     *
     * @param resultFilePath caminho do arquivo
     * @return writer pronto para uso
     * @throws IOException se houver erro de I/O
     */
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

    /**
     * Grava uma linha de resultado no CSV.
     *
     * @param writer writer do arquivo
     * @param tamanhoEntrada tamanho da entrada
     * @param operacao nome da operação
     * @param tempoMedio tempo médio em nanosegundos
     * @param memoriaUso memória usada em bytes
     * @throws IOException se houver erro de escrita
     */
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

    /**
     * Carrega os arquivos de entrada uma única vez.
     *
     * @throws IOException se houver erro de leitura
     */
    protected void lerDados() throws IOException {
        if (dadosCarregados) {
            return;
        }

        random = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv");
        crescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv");
        decrescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv");

        dadosCarregados = true;
    }

    /**
     * Carrega lista de inteiros de um arquivo.
     *
     * @param path caminho do arquivo
     * @return lista de inteiros
     * @throws IOException se houver erro de leitura
     */
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
     * Retorna a quantidade de heap usada no instante da chamada.
     *
     * @return bytes usados no heap
     */
    protected long getHeapUsedBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Retorna o nome da estrutura.
     *
     * @return nome da estrutura
     */
    protected abstract String getNomeEstrutura();

    /**
     * Cria uma nova instância da estrutura.
     *
     * @return nova estrutura
     */
    protected abstract Estrutura criarEstrutura();

    /**
     * Estrutura auxiliar para acumular resultados de workload.
     */
    protected static class ResultadoWorkload {
        long tempoAdd;
        long tempoSearch;
        long tempoRemove;

        long memAdd;
        long memSearch;
        long memRemove;

        int qtdAdd;
        int qtdSearch;
        int qtdRemove;
    }
}