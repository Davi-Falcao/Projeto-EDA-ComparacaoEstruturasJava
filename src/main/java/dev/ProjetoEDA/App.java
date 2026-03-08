package dev.ProjetoEDA;

import dev.ProjetoEDA.controller.BenchController;

/**
 * Classe principal responsável por selecionar e executar benchmarks.
 * A execução é realizada via argumentos passados pelo Maven.
 */
public class App {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: <benchmark> <tamanhoEntrada>");
            return;
        }

        String benchmark = args[0].trim().toLowerCase();
        int tamanhoEntrada;

        try {
            tamanhoEntrada = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("O tamanho da entrada deve ser um número inteiro válido.");
            return;
        }

        executarBenchmark(benchmark, tamanhoEntrada);
    }

    /**
     * Executa o benchmark selecionado para a estrutura especificada.
     *
     * @param benchmark Nome do benchmark a ser executado.
     * @param tamanhoEntrada Tamanho da entrada a ser usado no experimento.
     */
    private static void executarBenchmark(String benchmark, int tamanhoEntrada) {
        try {
            BenchController bController = new BenchController();
            bController.escolherOCaso(benchmark, tamanhoEntrada);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}