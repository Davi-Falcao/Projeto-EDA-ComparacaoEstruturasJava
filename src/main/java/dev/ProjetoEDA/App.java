package dev.ProjetoEDA;

import dev.ProjetoEDA.controller.BenchController;

/**
 * Classe principal responsável por disparar todos os benchmarks
 * de uma estrutura a partir da linha de comando.
 *
 * <p>Uso:
 * <pre>
 * mvn exec:java "-Dexec.args=arraylist 100000"
 * </pre>
 * </p>
 */
public class App {

    /**
     * Método principal da aplicação.
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
     * Executa todos os benchmarks da estrutura escolhida.
     *
     * @param estrutura nome da estrutura
     * @param tamanhoEntrada tamanho máximo da entrada
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