package dev.ProjetoEDA;

import dev.ProjetoEDA.controller.BenchController;

/**
 * Classe principal responsável por selecionar e executar benchmarks.
 * A execução é realizada via argumentos passados pelo Maven.
 */
public class App {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Nenhum benchmark especificado.");
            exibirMenu();
            return;
        }

        exibirMenu();
        String benchmark = args[0].trim().toLowerCase();
        executarBenchmark(benchmark);
    }

    private static void exibirMenu() {
        System.out.println("==============================================================");
        System.out.println("        SELETOR DE BENCHMARKS - EDA COMPARAÇÃO JAVA          ");
        System.out.println("==============================================================");
        System.out.println("Uso:");
        System.out.println("  mvn exec:java -Papp -Dexec.args=\"arraylist\"");
        System.out.println("Exemplo:");
        System.out.println("  mvn exec:java -Papp -Dexec.args=\"arraylist\"");
        System.out.println("PowerShell:");
        System.out.println("  mvn exec:java -Papp \"-Dexec.args=arraylist \"");
        System.out.println("==============================================================");
    }
    /**
     * Executa o benchmark selecionado para a estrutura especificada.
     *
     * @param benchmark Nome do benchmark a ser executado.
     * @throws Exception Caso ocorra erro durante a execução do benchmark ou no processamento dos arquivos temporários.
     */
    private static void executarBenchmark(String benchmark) {
        try {
            BenchController bController = new BenchController();
            bController.escolherOCaso(benchmark);    
        
        } catch (Exception e) {}
    }
}