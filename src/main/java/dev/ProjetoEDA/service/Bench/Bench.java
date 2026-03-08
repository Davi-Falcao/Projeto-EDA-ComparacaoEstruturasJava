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

public abstract class Bench {

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decrescente;
    protected static int entrada;

    protected static final int REPETICOES = 10;

    private static boolean dadosCarregados = false;

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

        for (int passo = 1; passo <= tamanhoEntrada; passo++) {
            long[] tempos = new long[REPETICOES];
            long[] memorias = new long[REPETICOES];
            char operacao = descobrirOperacao(casoTest, passo - 1, tamanhoEntrada);

            for (int repeticao = 0; repeticao < REPETICOES; repeticao++) {
                Estrutura estrutura = criarEstrutura();

                executarPassosAte(estrutura, dados, passo - 1, tamanhoEntrada, casoTest);

                long memoriaAntes = getProcessRssBytes();
                long tempoAntes = System.nanoTime();

                executarPasso(estrutura, dados, passo - 1, tamanhoEntrada, casoTest);

                long tempoDepois = System.nanoTime();
                long memoriaDepois = getProcessRssBytes();

                tempos[repeticao] = tempoDepois - tempoAntes;
                memorias[repeticao] = memoriaDepois - memoriaAntes;
            }

            Arrays.sort(tempos);
            Arrays.sort(memorias);

            gravarDadosArquivoSaida(
                    resultFilePath,
                    passo,
                    operacao,
                    calcularMediana(tempos),
                    calcularMediana(memorias)
            );
        }
    }

    protected void executarPorOrdem(String ordem, String[] casos) {
        for (String caso : casos) {
            experimento(entrada, ordem, caso);
        }
    }

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
                if (passo < tamanhoEntrada / 2) {
                    estrutura.add(dados.get(passo));
                } else {
                    estrutura.remove(dados.get(passo - (tamanhoEntrada / 2)));
                }
                break;

            case "75I25R0S":
                int limiteInsercao75 = (int) (tamanhoEntrada * 0.75);
                if (passo < limiteInsercao75) {
                    estrutura.add(dados.get(passo));
                } else {
                    estrutura.remove(dados.get(passo - limiteInsercao75));
                }
                break;

            case "50I25R25S":
                int limiteInsercao50 = (int) (tamanhoEntrada * 0.50);
                int limiteBusca25 = (int) (tamanhoEntrada * 0.25);

                if (passo < limiteInsercao50) {
                    estrutura.add(dados.get(passo));
                } else if (passo < limiteInsercao50 + limiteBusca25) {
                    estrutura.search(dados.get(passo - limiteInsercao50));
                } else {
                    estrutura.remove(dados.get(passo - limiteInsercao50 - limiteBusca25));
                }
                break;

            case "50I0R50S":
                int limiteInsercao = (int) (tamanhoEntrada * 0.50);

                if (passo < limiteInsercao) {
                    estrutura.add(dados.get(passo));
                } else {
                    estrutura.search(dados.get(passo - limiteInsercao));
                }
                break;

            default:
                throw new IllegalArgumentException("Caso de teste inválido: " + casoTest);
        }
    }

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

    protected String gerarPathArquivoSaida(String ordem, String casoTest) {
        String nomeEstrutura = getNomeEstrutura();

        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_" + casoTest + ".csv";
    }

    protected static BufferedWriter inicializarArquivoDeSaida(String resultFilePath) throws IOException {
        File file = new File(resultFilePath);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

        if (file.length() == 0) {
            writer.write("TamanhoEntrada,Operacao,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }

        return writer;
    }

    protected void gravarDadosArquivoSaida(
            String resultFilePath,
            int tamanhoEntrada,
            char operacao,
            long tempoMediana,
            long memoriaMediana
    ) {
        try (BufferedWriter writer = inicializarArquivoDeSaida(resultFilePath)) {
            writer.write(
                    tamanhoEntrada + "," +
                    operacao + "," +
                    tempoMediana + "," +
                    memoriaMediana + "\n"
            );
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    protected void lerDados() throws IOException {
        if (dadosCarregados) {
            return;
        }

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

    protected long calcularMediana(long[] valores) {
        int meio = valores.length / 2;

        if (valores.length % 2 == 0) {
            return (valores[meio - 1] + valores[meio]) / 2;
        }

        return valores[meio];
    }

    protected long getProcessRssBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    protected abstract String getNomeEstrutura();

    protected abstract Estrutura criarEstrutura();
}