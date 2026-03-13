package dev.ProjetoEDA.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ProjetoEDA.model.Estrutura;

/**
 * Classe abstrata responsável por definir o protocolo experimental dos benchmarks.
 *
 * A classe centraliza o fluxo de avaliação das estruturas de dados, incluindo:
 * carregamento dos conjuntos de entrada, geração das escalas de teste,
 * execução de operações isoladas, execução de workloads mistos,
 * medição de tempo e memória e gravação dos resultados em arquivos CSV.
 *
 * Todas as estruturas concretas são avaliadas sob o mesmo procedimento
 * experimental, variando apenas a implementação retornada por
 * {@link #criarEstrutura()} e o nome textual retornado por
 * {@link #getNomeEstrutura()}.
 */
public abstract class Bench {

    public enum Operacao {
        ADD,
        SEARCH,
        REMOVE
    }

    public enum CasoMisto {
        C100I0R0S("100I0R0S"),
        C75I25R0S("75I25R0S"),
        C50I25R25S("50I25R25S"),
        C50I0R50S("50I0R50S"),
        C50I50R0S("50I50R0S");

        private final String nome;

        CasoMisto(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }

    protected static final String[] ORDENS = { "random", "crescente", "decrescente" };

    protected static final Operacao[] OPERACOES_ISOLADAS = {Operacao.ADD, Operacao.SEARCH, Operacao.REMOVE};

    protected static final CasoMisto[] CASOS_MISTOS = { CasoMisto.C100I0R0S, CasoMisto.C75I25R0S, CasoMisto.C50I25R25S, CasoMisto.C50I0R50S,CasoMisto.C50I50R0S};

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decrescente;

    protected static int entrada;

    private static boolean dadosCarregados = false;

    protected static final int RODADAS_WARMUP = 3;
    protected static final int RODADAS_MEDICAO = 9;
    protected static final int REPETICOES_POR_AMOSTRA = 2000;
    protected static final int REPETICOES_REMOVE = 200;

    private static volatile Object referenciaMemoria;

    /**
     * Define o maior tamanho de entrada considerado no experimento.
     *
     * @param tamanhoEntrada limite superior das escalas de teste
     * @throws IllegalArgumentException se o valor informado for menor ou igual a zero
     */
    public void definirEntrada(int tamanhoEntrada) {
        if (tamanhoEntrada <= 0) {
            throw new IllegalArgumentException("O tamanho da entrada deve ser maior que zero.");
        }

        entrada = tamanhoEntrada;
    }

    /**
     * Executa o protocolo completo do benchmark.
     *
     * O fluxo experimental é composto por três etapas:
     * carregamento dos dados de entrada, execução dos benchmarks de operações
     * isoladas e execução dos benchmarks de workloads mistos.
     *
     * Esse método funciona como ponto central de orquestração do experimento
     * para qualquer estrutura concreta.
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
     * Executa os benchmarks de operações isoladas para todas as ordens de entrada.
     *
     * Para cada operação fundamental da estrutura, o experimento é repetido
     * sobre os conjuntos random, crescente e decrescente, garantindo
     * comparabilidade entre estruturas e entre distribuições de entrada.
     *
     * @throws IOException se ocorrer erro na escrita dos resultados
     */
    protected void executarOperacoesIsoladas() throws IOException {
        for (Operacao operacao : OPERACOES_ISOLADAS) {
            for (String ordem : ORDENS) {
                executarExperimentoOperacao(ordem, operacao);
            }
        }
    }

    /**
     * Executa os benchmarks de workloads mistos para todas as ordens de entrada.
     *
     * Cada cenário misto representa uma distribuição fixa entre inserções,
     * buscas e remoções. O experimento é executado para todas as ordens
     * de entrada, permitindo observar o comportamento da estrutura em
     * padrões de uso compostos.
     *
     * @throws IOException se ocorrer erro na escrita dos resultados
     */
    protected void executarCasosMistos() throws IOException {
        for (CasoMisto caso : CASOS_MISTOS) {
            for (String ordem : ORDENS) {
                executarExperimentoWorkload(ordem, caso);
            }
        }
    }

    /**
     * Executa as rodadas de aquecimento de uma operação isolada.
     *
     * As execuções de warmup não são gravadas. Seu objetivo é reduzir
     * efeitos transitórios da JVM antes da coleta das rodadas válidas.
     *
     * @param dados conjunto de dados usado na rodada
     * @param n tamanho da estrutura
     * @param operacao operação a ser aquecida
     */
    protected void executarWarmupOperacao(List<Integer> dados, int n, Operacao operacao) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            medirOperacao(dados, n, operacao, i);
        }
    }

    /**
     * Executa o experimento de uma operação isolada para uma ordem específica.
     *
     * Para cada escala de tamanho:
     * realiza warmup, executa as rodadas de medição, calcula a mediana
     * do tempo da operação, mede a memória da estrutura com n elementos
     * e grava o resultado em CSV.
     *
     * A medição de memória é realizada separadamente da medição de tempo,
     * mas ambos os valores são registrados na mesma linha de saída.
     *
     * @param ordem ordem dos dados de entrada
     * @param operacao operação avaliada
     * @throws IOException se ocorrer erro na criação ou escrita do CSV
     */
    protected void executarExperimentoOperacao(String ordem, Operacao operacao) throws IOException {
        List<Integer> dados = getDadosPorOrdem(ordem);
        String pathSaida = gerarPathArquivoSaidaOperacao(ordem, operacao);

        try (BufferedWriter writer = inicializarArquivoDeSaida(pathSaida)) {
            for (int n : gerarEscalas()) {
                executarWarmupOperacao(dados, n, operacao);

                long[] tempos = new long[RODADAS_MEDICAO];

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoOperacao resultado = medirOperacao(dados, n, operacao, rodada);
                    tempos[rodada] = resultado.tempoMedio;
                }

                long memoriaEstrutura = medirMemoriaEstrutura(dados, n);

                gravarLinhaResultado(writer, n, operacao.name(), calcularMediana(tempos), memoriaEstrutura);
            }
        }
    }

    /**
     * Direciona a medição para o procedimento correspondente à operação avaliada.
     *
     * @param dados conjunto de dados usado no experimento
     * @param n tamanho da estrutura
     * @param operacao operação medida
     * @param rodada índice da rodada
     * @return resultado agregado da operação
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
     * Mede o tempo médio da operação de inserção.
     *
     * Em cada repetição interna, a estrutura é recriada, preenchida com n elementos
     * e uma inserção adicional é medida isoladamente.
     *
     * @param dados dados usados na construção da estrutura
     * @param n tamanho inicial da estrutura
     * @param rodada índice da rodada
     * @return resultado agregado da medição
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
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede o tempo médio da operação de busca.
     *
     * Em cada repetição interna, a estrutura é recriada, preenchida com n elementos
     * e a busca de um elemento presente é medida isoladamente.
     *
     * @param dados dados usados na construção e na busca
     * @param n tamanho da estrutura
     * @param rodada índice da rodada
     * @return resultado agregado da medição
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
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede o tempo médio da operação de remoção.
     *
     * Em cada repetição interna, a estrutura é recriada, preenchida com n elementos
     * e uma remoção unitária é medida.
     *
     * @param dados dados usados na construção e remoção
     * @param n tamanho da estrutura
     * @param rodada índice da rodada
     * @return resultado agregado da medição
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
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede a memória ocupada pela estrutura após a inserção de n elementos.
     *
     * O procedimento executa múltiplas rodadas, estabiliza a heap antes e depois
     * da construção da estrutura e retorna a mediana das diferenças observadas.
     *
     * @param dados dados usados para popular a estrutura
     * @param n tamanho da estrutura
     * @return memória ocupada em bytes
     */
    protected long medirMemoriaEstrutura(List<Integer> dados, int n) {
        long[] memorias = new long[RODADAS_MEDICAO];

        for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
            estabilizarHeap();

            long memoriaAntes = getHeapUsedBytes();

            Estrutura estrutura = criarEstrutura();
            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            referenciaMemoria = estrutura;
            estabilizarHeap();

            long memoriaDepois = getHeapUsedBytes();
            memorias[rodada] = Math.max(0L, memoriaDepois - memoriaAntes);

            referenciaMemoria = null;
            estabilizarHeap();
        }

        return calcularMediana(memorias);
    }

    /**
     * Executa o experimento de workload misto para uma ordem e um cenário específicos.
     *
     * Para cada escala:
     * realiza warmup, executa as rodadas do workload, calcula o tempo médio
     * por tipo de operação dentro de cada rodada e grava no CSV a mediana
     * desses tempos médios, juntamente com a memória da estrutura.
     *
     * @param ordem ordem dos dados de entrada
     * @param caso cenário de workload avaliado
     * @throws IOException se ocorrer erro na criação ou escrita do CSV
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
                }

                long memoriaEstrutura = medirMemoriaEstrutura(dados, n);

                if (temAdd) {
                    gravarLinhaResultado(writer, n, Operacao.ADD.name(), calcularMediana(temposAdd), memoriaEstrutura);
                }

                if (temSearch) {
                    gravarLinhaResultado(writer, n, Operacao.SEARCH.name(), calcularMediana(temposSearch), memoriaEstrutura);
                }

                if (temRemove) {
                    gravarLinhaResultado(writer, n, Operacao.REMOVE.name(), calcularMediana(temposRemove), memoriaEstrutura);
                }
            }
        }
    }

    /**
     * Executa as rodadas de aquecimento de um workload misto.
     *
     * @param dados conjunto de dados usado no experimento
     * @param n número total de passos do workload
     * @param caso cenário avaliado
     * @param ordem ordem dos dados de entrada
     */
    protected void executarWarmupWorkload(List<Integer> dados, int n, CasoMisto caso, String ordem) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            executarRoundWorkload(dados, n, caso, ordem, i);
        }
    }

    /**
     * Executa uma rodada completa de workload misto.
     *
     * A rodada mantém uma estrutura ativa e aplica a sequência de operações
     * determinada pelo cenário. Os tempos e quantidades de cada operação
     * são acumulados no objeto de resultado.
     *
     * @param dados dados usados no workload
     * @param n número total de passos da rodada
     * @param caso cenário executado
     * @param ordem ordem dos dados de entrada
     * @param rodada índice da rodada
     * @return tempos acumulados e quantidades executadas por operação
     */
    protected ResultadoWorkload executarRoundWorkload(List<Integer> dados, int n, CasoMisto caso, String ordem, int rodada) {
        ResultadoWorkload resultado = new ResultadoWorkload();

        Estrutura estrutura = criarEstrutura();
        java.util.ArrayList<Integer> ativos = new java.util.ArrayList<>();

        int addExecutados = 0;

        Random randomizador = new Random(31L * n + 17L * rodada + 13L * Math.abs(ordem.hashCode()) + 97L * caso.ordinal());

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

    /**
     * Determina a operação correspondente a um passo do workload.
     *
     * @param caso cenário de workload
     * @param passo posição atual na rodada
     * @param n número total de passos
     * @return operação correspondente ao passo
     */
    protected Operacao descobrirOperacaoWorkload(CasoMisto caso, int passo, int n) {
        int qtdAdd = quantidadeAdd(caso, n);
        int qtdSearch = quantidadeSearch(caso, n);

        switch (caso) {
            case C100I0R0S:
                return Operacao.ADD;
            case C75I25R0S:
            case C50I50R0S:
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
     * Calcula a quantidade de inserções prevista para um cenário misto.
     *
     * @param caso cenário avaliado
     * @param n tamanho total da rodada
     * @return número de operações ADD
     */
    protected int quantidadeAdd(CasoMisto caso, int n) {
        switch (caso) {
            case C100I0R0S:
                return n;
            case C75I25R0S:
                return (int) Math.round(n * 0.75);
            case C50I25R25S:
            case C50I0R50S:
            case C50I50R0S:
                return n / 2;
            default:
                return 0;
        }
    }

    /**
     * Calcula a quantidade de buscas prevista para um cenário misto.
     *
     * @param caso cenário avaliado
     * @param n tamanho total da rodada
     * @return número de operações SEARCH
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
     * Gera as escalas de tamanho avaliadas no experimento.
     *
     * As escalas são distribuídas em progressão logarítmica entre o valor mínimo
     * e o maior tamanho de entrada definido para o benchmark.
     *
     * @return vetor com os tamanhos de entrada
     */
    protected int[] gerarEscalas() {
        int minimo = 100;
        int maximo = entrada;

        if (maximo <= minimo) {
            return new int[] { maximo };
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
     * Distribui um índice ao longo de um intervalo discreto.
     *
     * @param iteracao posição atual
     * @param total número total de iterações
     * @param tamanho tamanho do intervalo
     * @return índice válido no intervalo
     */
    protected int distribuirIndice(int iteracao, int total, int tamanho) {
        if (tamanho <= 1) {
            return 0;
        }

        int indice = (int) (((long) iteracao * tamanho) / Math.max(1, total));
        return Math.min(indice, tamanho - 1);
    }

    /**
     * Calcula a mediana de um vetor de valores long.
     *
     * @param valores vetor de entrada
     * @return valor mediano
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
     * Retorna o conjunto de dados correspondente à ordem informada.
     *
     * @param ordem identificador da ordem
     * @return lista de dados correspondente
     * @throws IllegalArgumentException se a ordem for inválida
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
     * Gera o caminho do arquivo CSV de uma operação isolada.
     *
     * @param ordem ordem dos dados de entrada
     * @param operacao operação avaliada
     * @return caminho completo do arquivo
     */
    protected String gerarPathArquivoSaidaOperacao(String ordem, Operacao operacao) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        String pathSaida = nomeEstrutura + "/result_" + nomeEstrutura + "_" + ordem + "_" + operacao.name().toLowerCase() + ".csv";
        return "src/main/java/dev/ProjetoEDA/repository/results/" + pathSaida;
    }

    /**
     * Gera o caminho do arquivo CSV de um workload misto.
     *
     * @param ordem ordem dos dados de entrada
     * @param caso cenário avaliado
     * @return caminho completo do arquivo
     */
    protected String gerarPathArquivoSaidaWorkload(String ordem, CasoMisto caso) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        String pathSaida = nomeEstrutura + "/result_" + nomeEstrutura + "_" + ordem + "_workload_" + caso.getNome() + ".csv";
        return "src/main/java/dev/ProjetoEDA/repository/results/" + pathSaida;
    }

    /**
     * Cria o arquivo CSV de saída e escreve o cabeçalho do experimento.
     *
     * @param resultFilePath caminho do arquivo
     * @return writer inicializado
     * @throws IOException se ocorrer erro na criação ou abertura
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
     * Grava uma linha de resultado no arquivo CSV.
     *
     * @param writer writer do arquivo
     * @param tamanhoEntrada tamanho avaliado
     * @param operacao operação registrada
     * @param tempoMedio valor temporal registrado
     * @param memoriaUso memória registrada
     * @throws IOException se ocorrer erro na escrita
     */
    protected void gravarLinhaResultado(BufferedWriter writer, int tamanhoEntrada, String operacao, long tempoMedio, long memoriaUso) throws IOException {
        writer.write(tamanhoEntrada + "," + operacao + "," + tempoMedio + "," + memoriaUso + "\n");
    }

    /**
     * Carrega os arquivos de entrada em memória.
     *
     * O carregamento é realizado apenas uma vez por execução, reutilizando
     * os dados entre os diferentes experimentos.
     *
     * @throws IOException se ocorrer erro na leitura dos arquivos
     */
    protected void lerDados() throws IOException {
        if (dadosCarregados) {
            return;
        }
         
        String pathBase = "src/main/java/dev/ProjetoEDA/repository/entry/";

        random = carregarInteiros(pathBase + "entradaRandomUnica.csv");
        crescente = carregarInteiros(pathBase + "entradaCrescenteUnica.csv");
        decrescente = carregarInteiros(pathBase + "entradaDecrescenteUnica.csv");

        dadosCarregados = true;
    }

    /**
     * Lê um arquivo CSV e extrai os inteiros da primeira coluna.
     *
     * @param path caminho do arquivo
     * @return lista de inteiros carregada
     * @throws IOException se ocorrer erro na leitura
     */
    protected List<Integer> carregarInteiros(String path) throws IOException {
        try (Stream<String> linhas = Files.lines(Paths.get(path))) {
            return linhas.map(String::trim)
                    .filter(linha -> !linha.isEmpty())
                    .filter(linha -> !linha.matches(".*[a-zA-Z].*"))
                    .map(linha -> linha.split(",")[0].trim())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Retorna a quantidade de heap atualmente utilizada pela JVM.
     *
     * @return heap usada em bytes
     */
    protected long getHeapUsedBytes() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Executa rotinas para reduzir ruído antes de leituras de memória.
     */
    protected void estabilizarHeap() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retorna o nome textual da estrutura em benchmark.
     *
     * @return nome da estrutura
     */
    protected abstract String getNomeEstrutura();

    /**
     * Cria uma nova instância vazia da estrutura avaliada.
     *
     * @return estrutura concreta vazia
     */
    protected abstract Estrutura criarEstrutura();

    protected static class ResultadoOperacao {
        long tempoMedio;
        long memoriaMedia;

        ResultadoOperacao(long tempoMedio, long memoriaMedia) {
            this.tempoMedio = tempoMedio;
            this.memoriaMedia = memoriaMedia;
        }
    }

    protected static class ResultadoWorkload {
        long tempoAdd;
        long tempoSearch;
        long tempoRemove;
        int qtdAdd;
        int qtdSearch;
        int qtdRemove;
    }
}