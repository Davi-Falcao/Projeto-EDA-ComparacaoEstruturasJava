package dev.ProjetoEDA.service.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ProjetoEDA.model.Estrutura;

/**
 * Classe abstrata responsável por definir a estrutura base
 * para execução de benchmarks das estruturas de dados.
 *
 * Esta classe controla:
 * - carregamento das entradas
 * - execução dos experimentos
 * - medição de tempo e memória
 * - gravação dos resultados em arquivos CSV
 */
public abstract class Bench {

    /** Lista de entrada em ordem aleatória. */
    protected static List<Integer> random;

    /** Lista de entrada em ordem crescente. */
    protected static List<Integer> crescente;

    /** Lista de entrada em ordem decrescente. */
    protected static List<Integer> decrescente;

    /** Tamanho total da entrada utilizada no experimento. */
    protected static int entrada;

    /** Número de repetições de cada medição. */
    protected static final int REPETICOES = 10;

    /** Indica se os dados já foram carregados. */
    private static boolean dadosCarregados = false;

    /** Método que inicia a execução do benchmark. */
    public abstract void run();

    public void definirEntrada(int tamanhoEntrada) {
        if (tamanhoEntrada <= 0) {
            throw new IllegalArgumentException("O tamanho da entrada deve ser maior que zero.");
        }

        entrada = tamanhoEntrada;
    }

    protected void experimento(int tamanhoEntrada, String ordem, String casoTest) {

        List<Integer> dados = getDadosPorOrdem(ordem);
        String resultFilePath = gerarPathArquivoSaida(ordem, casoTest);

        Estrutura estrutura = criarEstrutura();

        for (int passo = 0; passo < tamanhoEntrada; passo++) {

            long[] tempos = new long[REPETICOES];
            long[] memorias = new long[REPETICOES];

            char operacao = descobrirOperacao(casoTest, passo, tamanhoEntrada);
            int indiceOperacao = descobrirIndiceOperacao(casoTest, passo, tamanhoEntrada);

            for (int repeticao = 0; repeticao < REPETICOES; repeticao++) {

                long memoriaAntes = getHeapUsedBytes();
                long tempoAntes = System.nanoTime();

                executarPasso(estrutura, dados, passo, tamanhoEntrada, casoTest);

                long tempoDepois = System.nanoTime();
                long memoriaDepois = getHeapUsedBytes();

                tempos[repeticao] = tempoDepois - tempoAntes;
                memorias[repeticao] = memoriaDepois - memoriaAntes;
            }

            Arrays.sort(tempos);
            Arrays.sort(memorias);

            gravarDadosArquivoSaida(
                resultFilePath,
                indiceOperacao,
                operacao,
                calcularMediana(tempos),
                calcularMediana(memorias)
            );
        }
    }
    /**
     * Executa todos os casos de teste para uma determinada ordem de dados.
     */
    protected void executarPorOrdem(String ordem, String[] casos) {
        for (String caso : casos) {
            experimento(entrada, ordem, caso);
        }
    }

    /**
     * Executa todos os passos anteriores ao passo atual do experimento.
     */
    protected void executarPassosAte(
            Estrutura estrutura,
            List<Integer> dados,
            int quantidadePassos,
            int tamanhoEntrada,
            String casoTest
    ) {
        for (int passo = 0; passo < quantidadePassos; passo++) {
            executarPasso(estrutura, dados, passo, tamanhoEntrada, casoTest);
        }
    }

    /**
     * Executa uma única operação do experimento (insert, remove ou search).
     */
    protected void executarPasso(
            Estrutura estrutura,
            List<Integer> dados,
            int passo,
            int tamanhoEntrada,
            String casoTest
    ) {
        switch (casoTest) {

            case "100I0R0S":
                estrutura.add(dados.get(passo));
                break;

            case "50I50R0S":
                int metade = tamanhoEntrada / 2;
                if (passo < metade) {
                    int indiceInsercao = passo;
                    estrutura.add(dados.get(indiceInsercao));
                } else {
                    int indiceRemocao = passo - metade;
                    estrutura.remove(dados.get(indiceRemocao));
                }
                break;

            case "75I25R0S":
                int limiteInsercao75 = (int) (tamanhoEntrada * 0.75);
                if (passo < limiteInsercao75) {
                    int indiceInsercao = passo;
                    estrutura.add(dados.get(indiceInsercao));
                } else {
                    int indiceRemocao = passo - limiteInsercao75;
                    estrutura.remove(dados.get(indiceRemocao));
                }
                break;

            case "50I25R25S":
                int limiteInsercao50 = (int) (tamanhoEntrada * 0.50);
                int limiteBusca25 = (int) (tamanhoEntrada * 0.25);

                if (passo < limiteInsercao50) {
                    int indiceInsercao = passo;
                    estrutura.add(dados.get(indiceInsercao));
                } else if (passo < limiteInsercao50 + limiteBusca25) {
                    int indiceBusca = passo - limiteInsercao50;
                    estrutura.search(dados.get(indiceBusca));
                } else {
                    int indiceRemocao = passo - limiteInsercao50 - limiteBusca25;
                    estrutura.remove(dados.get(indiceRemocao));
                }
                break;

            case "50I0R50S":
                int limiteInsercao = (int) (tamanhoEntrada * 0.50);

                if (passo < limiteInsercao) {
                    int indiceInsercao = passo;
                    estrutura.add(dados.get(indiceInsercao));
                } else {
                    int indiceBusca = passo - limiteInsercao;
                    estrutura.search(dados.get(indiceBusca));
                }
                break;

            default:
                throw new IllegalArgumentException("Caso de teste inválido: " + casoTest);
        }
    }

    /**
     * Identifica qual operação está sendo executada no passo atual.
     */
    protected char descobrirOperacao(String casoTest, int passo, int tamanhoEntrada) {
        switch (casoTest) {

            case "100I0R0S":
                return 'I';

            case "50I50R0S":
                return passo < tamanhoEntrada / 2 ? 'I' : 'R';

            case "75I25R0S":
                return passo < (int) (tamanhoEntrada * 0.75) ? 'I' : 'R';

            case "50I25R25S":
                int limiteInsercao50 = (int) (tamanhoEntrada * 0.50);
                int limiteBusca25 = (int) (tamanhoEntrada * 0.25);

                if (passo < limiteInsercao50) {
                    return 'I';
                } else if (passo < limiteInsercao50 + limiteBusca25) {
                    return 'S';
                } else {
                    return 'R';
                }

            case "50I0R50S":
                return passo < (int) (tamanhoEntrada * 0.50) ? 'I' : 'S';

            default:
                throw new IllegalArgumentException("Caso de teste inválido: " + casoTest);
        }
    }

    /**
     * Retorna o índice local da operação dentro da fase atual.
     */
    protected int descobrirIndiceOperacao(String casoTest, int passo, int tamanhoEntrada) {
        switch (casoTest) {

            case "100I0R0S":
                return passo;

            case "50I50R0S":
                int metade = tamanhoEntrada / 2;
                return passo < metade ? passo : passo - metade;

            case "75I25R0S":
                int limiteInsercao75 = (int) (tamanhoEntrada * 0.75);
                return passo < limiteInsercao75 ? passo : passo - limiteInsercao75;

            case "50I25R25S":
                int limiteInsercao50 = (int) (tamanhoEntrada * 0.50);
                int limiteBusca25 = (int) (tamanhoEntrada * 0.25);

                if (passo < limiteInsercao50) {
                    return passo;
                } else if (passo < limiteInsercao50 + limiteBusca25) {
                    return passo - limiteInsercao50;
                } else {
                    return passo - limiteInsercao50 - limiteBusca25;
                }

            case "50I0R50S":
                int limiteInsercao = (int) (tamanhoEntrada * 0.50);
                return passo < limiteInsercao ? passo : passo - limiteInsercao;

            default:
                throw new IllegalArgumentException("Caso de teste inválido: " + casoTest);
        }
    }

    /**
     * Retorna os dados de entrada de acordo com a ordem escolhida.
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
     * Gera o caminho do arquivo CSV onde os resultados serão gravados.
     */
    protected String gerarPathArquivoSaida(String ordem, String casoTest) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();

        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_" + casoTest + ".csv";
    }

    /**
     * Inicializa o arquivo de saída e escreve o cabeçalho caso esteja vazio.
     */
    protected static BufferedWriter inicializarArquivoDeSaida(String resultFilePath) throws IOException {
        File file = new File(resultFilePath);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            file.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        if (file.length() == 0) {
            writer.write("IndiceOperacao,Operacao,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }

        return writer;
    }

    /**
     * Grava uma linha de resultado no arquivo CSV.
     */
    protected void gravarDadosArquivoSaida(
            String resultFilePath,
            int indiceOperacao,
            char operacao,
            long tempoMediana,
            long memoriaMediana
    ) {
        try (BufferedWriter writer = inicializarArquivoDeSaida(resultFilePath)) {
            writer.write(
                    indiceOperacao + "," +
                    operacao + "," +
                    tempoMediana + "," +
                    memoriaMediana + "\n"
            );
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Carrega os arquivos de entrada utilizados nos testes.
     */
    protected void lerDados() throws IOException {
        if (dadosCarregados) return;

        random = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv");
        crescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv");
        decrescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv");

        if (entrada <= 0) {
            entrada = carregarInteiroUnico("src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntradaPadrao.csv");
        }

        dadosCarregados = true;
    }

    private Stream<String> streamLinhasNumericas(String path) throws IOException {
        return Files.lines(Paths.get(path))
                .map(String::trim)
                .filter(linha -> !linha.isEmpty())
                .filter(linha -> !linha.matches(".*[a-zA-Z].*"))
                .map(linha -> linha.split(",")[0].trim());
    }

    protected List<Integer> carregarInteiros(String path) throws IOException {
        try (Stream<String> linhas = streamLinhasNumericas(path)) {
            return linhas
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Lê um único valor inteiro de um arquivo.
     */
    protected int carregarInteiroUnico(String path) throws IOException {
        try (Stream<String> linhas = streamLinhasNumericas(path)) {
            return linhas
                    .mapToInt(Integer::parseInt)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Arquivo vazio ou sem inteiro válido: " + path
                    ));
        }
    }

    /**
     * Calcula a mediana de um conjunto de valores.
     */
    protected long calcularMediana(long[] valores) {
        int meio = valores.length / 2;

        if (valores.length % 2 == 0) {
            return (valores[meio - 1] + valores[meio]) / 2;
        }

        return valores[meio];
    }

    /**
     * Retorna a quantidade de memória utilizada pela JVM.
     */
    protected long getProcessRssBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    protected long getHeapUsedBytes() {
    Runtime runtime = Runtime.getRuntime();
    return runtime.totalMemory() - runtime.freeMemory();
    }   
    /** Retorna o nome da estrutura testada. */
    protected abstract String getNomeEstrutura();

    /** Cria uma nova instância da estrutura testada. */
    protected abstract Estrutura criarEstrutura();
}