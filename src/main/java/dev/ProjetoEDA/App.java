package dev.ProjetoEDA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.ProjetoEDA.bench.BenchArrayList;

/**
 * Classe principal que funciona como um seletor de benchmarks.
 * Permite escolher e executar diferentes classes de benchmark com argumentos JSON.
 */ 
public class App {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            exibirMenu();
            return;
        }

        String fullArg = args[0];
        String benchmark;
        String jsonConfig = "";

        if (fullArg.contains(",")) {
            int commaIndex = fullArg.indexOf(",");
            benchmark = fullArg.substring(0, commaIndex).toLowerCase();
            jsonConfig = fullArg.substring(commaIndex + 1).trim();
        } else {
            benchmark = fullArg.toLowerCase();
        }

        executarBenchmark(benchmark, jsonConfig);
    }

    private static void exibirMenu() {
        System.out.println("========================================================");
        System.out.println("      SELETOR DE BENCHMARKS - EDA COMPARAÇÃO JAVA       ");
        System.out.println("========================================================");
        System.out.println("Benchmarks disponíveis:");
        System.out.println("  1. arraylist-insertion  - Benchmark de inserção em ArrayList");
        System.out.println();
        System.out.println("Uso:");
        System.out.println("  mvn exec:java -Papp");
        System.out.println("  mvn exec:java -Papp '-Dexec.args=arraylist'");
        System.out.println("  mvn exec:java -Papp '-Dexec.args=arraylist,{\"OrdemAdicao\":true}'");
        System.out.println();
    }


    private static void executarBenchmark(String benchmark, String jsonConfig) throws Exception {
        switch (benchmark) {
            case "arraylist":
            case "1":
                System.out.println("Executando: Benchmark de Inserção em ArrayList");
                List<String> arquivosGerados = new ArrayList<>();

                for (int i = 0; i < 30; i++) {
                    System.out.println("Executando iteração " + (i + 1));
                    String arquivoPath = "data/results/ArrayList/temp/arquivo_" + (i + 1) + ".csv";
                    arquivosGerados.add(arquivoPath);
                    if (!jsonConfig.isEmpty()) {
                        BenchArrayList.main(new String[]{jsonConfig, arquivoPath});
                    } else {
                        BenchArrayList.main(new String[]{arquivoPath});
                    }
                }

                calcularMedianaDosArquivos(arquivosGerados, "ArrayList/resultCrescente_n100000_I50_R0_S50.csv");
                break;

            default:
                System.err.println("Erro: Benchmark '" + benchmark + "' não encontrado!");
                System.err.println("Benchmarks disponíveis: arraylist");
                System.exit(1);
        }
    }

    /**
     * Calcula a mediana a partir dos 5 arquivos gerados pelo benchmark.
     * 
     * @param arquivos Lista dos caminhos dos arquivos gerados.
     * @throws IOException Caso ocorra algum erro ao ler os arquivos.
     */
    private static void calcularMedianaDosArquivos(List<String> arquivos, String PathSaida) throws IOException {
        String mediana = arquivos.get(15); // A mediana é o arquivo no índice 3 (quarto arquivo gerado)
        String destino = "data/results/" + PathSaida;
    
        File origem = new File(mediana);
        File destinoFile = new File(destino);
        java.nio.file.Files.copy(origem.toPath(), destinoFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        for (String arquivo : arquivos) {
            new File(arquivo).delete();
        }
    }
}