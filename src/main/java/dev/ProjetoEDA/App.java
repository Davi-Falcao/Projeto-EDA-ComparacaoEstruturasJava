package dev.ProjetoEDA;

import dev.ProjetoEDA.bench.BenchArrayList;

/**
 * Classe principal que funciona como um seletor de benchmarks.
 * Permite escolher e executar diferentes classes de benchmark com argumentos JSON.
 */ 
public class App {

    public static void main(String[] args) throws Exception {
        // Se não houver argumentos, exibe menu de opções
        if (args.length == 0) {
            exibirMenu();
            return;
        }

        // Processa os argumentos: pode ser "benchmark" ou "benchmark,jsonConfig"
        String fullArg = args[0];
        String benchmark;
        String jsonConfig = "";

        if (fullArg.contains(",")) {
            // Separar benchmark e jsonConfig pela primeira vírgula
            int commaIndex = fullArg.indexOf(",");
            benchmark = fullArg.substring(0, commaIndex).toLowerCase();
            jsonConfig = fullArg.substring(commaIndex + 1).trim();
        } else {
            benchmark = fullArg.toLowerCase();
        }

        executarBenchmark(benchmark, jsonConfig);
    }

    /** 
     * Exibe o menu de benchmarks disponíveis
     */
    private static void exibirMenu() {
        System.out.println("========================================================");
        System.out.println("      SELETOR DE BENCHMARKS - EDA COMPARAÇÃO JAVA       ");
        System.out.println("========================================================");
        System.out.println();
        System.out.println("Benchmarks disponíveis:");
        System.out.println("  1. arraylist-insertion  - Benchmark de inserção em ArrayList");
        System.out.println();
        System.out.println("Uso:");
        System.out.println("  mvn exec:java -Papp");
        System.out.println("  mvn exec:java -Papp '-Dexec.args=arraylist-insertion'");
        System.out.println("  mvn exec:java -Papp '-Dexec.args=arraylist-insertion,{\"OrdemAdicao\":true}'");
        System.out.println();
        System.out.println("Exemplos:");
        System.out.println("  java dev.ProjetoEDA.App arraylist-insertion");
        System.out.println("  java dev.ProjetoEDA.App arraylist-insertion '{\"OrdemAdicao\":true}'");
        System.out.println();
    }

    /**
     * Executa o benchmark selecionado
     * 
     * @param benchmark Nome do benchmark a executar
     * @param jsonConfig Configuração em formato JSON (opcional)
     */
    private static void executarBenchmark(String benchmark, String jsonConfig) throws Exception {
        switch (benchmark) {
            case "arraylist-insertion":
            case "1":
                System.out.println("Executando: Benchmark de Inserção em ArrayList");
                if (!jsonConfig.isEmpty()) {
                    BenchArrayList.main(new String[]{jsonConfig});
                } else {
                    BenchArrayList.main(new String[]{});
                }
                break;

            default:
                System.err.println("Erro: Benchmark '" + benchmark + "' não encontrado!");
                System.err.println("Benchmarks disponíveis: arraylist-insertion");
                System.exit(1);
        }
    }
}
