package dev.ProjetoEDA;

import dev.ProjetoEDA.controller.BenchController;

/**
 * Classe de entrada da aplicação.
 *
 * Recebe os argumentos da linha de comando, valida os parâmetros
 * básicos do experimento e delega a execução ao controlador
 * responsável pelo benchmark.
 */
public class App {

    /**
     * Ponto de entrada da aplicação.
     *
     * Espera dois argumentos: o nome da estrutura e o maior tamanho
     * de entrada do experimento. Em caso de parâmetros inválidos,
     * a execução é interrompida com mensagem de erro.
     *
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: <estrutura> <tamanhoEntrada>");
            System.err.println("Exemplo: arraylist 100000");
            return;
        }

        String estrutura = args[0].trim().toLowerCase();
        int tamanhoEntrada;

        try {
            tamanhoEntrada = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("O tamanho da entrada deve ser um número inteiro válido.");
            return;
        }

        executarBenchmark(estrutura, tamanhoEntrada);
    }

    /**
     * Encaminha a execução do benchmark ao controlador.
     *
     * @param estrutura nome da estrutura a ser avaliada
     * @param tamanhoEntrada limite superior das escalas de teste
     */
    private static void executarBenchmark(String estrutura, int tamanhoEntrada) {
        try {
            BenchController controller = new BenchController();
            controller.executar(estrutura, tamanhoEntrada);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}