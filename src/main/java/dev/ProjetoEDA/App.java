package dev.ProjetoEDA;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import dev.ProjetoEDA.bench.BenchArrayList;

/**
 * Classe principal responsável por selecionar e executar benchmarks.
 * A execução é realizada via argumentos passados pelo Maven.
 */
public class App {

    private static final int ITERACOES = 30;
    private static final String TEMP_DIR = "data/results/temp/";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            exibirMenu();
            return;
        }

        String benchmark = args[0].trim().toLowerCase();
        String arquivoEntradaPath = args[1].trim();

        executarBenchmark(benchmark, arquivoEntradaPath);
    }

    private static void exibirMenu() {
        System.out.println("==============================================================");
        System.out.println("        SELETOR DE BENCHMARKS - EDA COMPARAÇÃO JAVA          ");
        System.out.println("==============================================================");
        System.out.println("Uso:");
        System.out.println("  mvn exec:java -Papp -Dexec.args=\"arraylist nomeArquivo.csv\"");
        System.out.println("Exemplo:");
        System.out.println("  mvn exec:java -Papp -Dexec.args=\"arraylist crescente_n100000_I50_R0_S50.csv\"");
        System.out.println("PowerShell:");
        System.out.println("  mvn exec:java -Papp \"-Dexec.args=arraylist crescente_n100000_I50_R0_S50.csv\"");
        System.out.println("==============================================================");
    }

    private static void executarBenchmark(String benchmark, String arquivoEntradaPath) throws Exception {
        switch (benchmark) {
            case "arraylist":
            case "1":

                List<String> arquivosGerados = new ArrayList<>();

                for (int i = 0; i < ITERACOES; i++) {
                    System.out.println("Executando iteração " + (i + 1));
                    String arquivoSaidaPath = TEMP_DIR + "arquivo_" + (i + 1) + ".csv";
                    arquivosGerados.add(arquivoSaidaPath);

                    BenchArrayList.main(new String[]{arquivoEntradaPath, arquivoSaidaPath});
                }
                String pathSaida = "ArrayList/result_" + arquivoEntradaPath;
                calcularMedianaDosArquivos(arquivosGerados, pathSaida);
                break;

            default:
                System.err.println("Benchmark não encontrado.");
        }
    }

    private static void calcularMedianaDosArquivos(List<String> arquivos, String pathSaida) throws IOException {
        if (arquivos == null || arquivos.isEmpty()) return;

        int medianaIndex = arquivos.size() / 2;
        String mediana = arquivos.get(medianaIndex);

        String destino = "data/results/" + pathSaida;

        Files.copy(new File(mediana).toPath(), new File(destino).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Resultados gravados em: " + destino);


        for (String arquivo : arquivos) {
            new File(arquivo).delete();
        }
    }
}