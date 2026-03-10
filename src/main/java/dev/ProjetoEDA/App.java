package dev.ProjetoEDA;

import dev.ProjetoEDA.controller.BenchController;

/**
 * Classe principal da aplicação responsável por iniciar a execução dos benchmarks
 * a partir dos argumentos recebidos pela linha de comando.
 *
 * Esta classe funciona como ponto de entrada do programa. Seu papel é receber
 * os parâmetros informados pelo usuário, validar esses parâmetros, converter o
 * tamanho da entrada para inteiro e encaminhar a execução para o controlador
 * responsável pelos benchmarks.
 *
 * O fluxo geral desta classe é:
 *
 * 1. verificar se a quantidade mínima de argumentos foi informada;
 * 2. ler o nome da estrutura que será testada;
 * 3. converter o tamanho máximo da entrada;
 * 4. delegar ao BenchController a execução do benchmark correspondente.
 *
 * A classe não executa benchmarks diretamente. Ela existe para organizar a
 * inicialização da aplicação e separar a responsabilidade de entrada da
 * responsabilidade de controle da execução.
 */
public class App {

    /**
     * Método principal da aplicação.
     *
     * Esse método é o ponto de entrada executado pela JVM. Ele interpreta os
     * argumentos passados na linha de comando e garante que a aplicação só
     * continue quando os parâmetros essenciais estiverem válidos.
     *
     * O primeiro argumento representa o nome da estrutura a ser benchmarkada.
     * O segundo argumento representa o maior tamanho de entrada que será usado
     * na geração das escalas do experimento.
     *
     * Se os argumentos forem insuficientes, o método informa a forma correta
     * de uso e encerra a execução. Se o tamanho da entrada não puder ser
     * convertido para inteiro, o método informa o erro e também encerra.
     *
     * Quando os argumentos são válidos, a execução é repassada para o método
     * executarBenchmark.
     *
     * @param args argumentos recebidos pela linha de comando
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
     * Executa o benchmark da estrutura escolhida.
     *
     * Esse método centraliza a delegação da execução para o BenchController,
     * que é o componente responsável por localizar a estrutura pedida e iniciar
     * o fluxo completo de benchmark.
     *
     * A separação desse comportamento em um método próprio melhora a organização
     * da classe principal e deixa claro que a função da App é apenas iniciar e
     * encaminhar a execução, sem conter a lógica de benchmark em si.
     *
     * O tratamento de exceções distingue dois tipos principais de falha:
     *
     * 1. erro de argumento inválido, como nome de estrutura não suportado;
     * 2. erro inesperado durante a execução, que é impresso para diagnóstico.
     *
     * @param estrutura nome normalizado da estrutura a ser executada
     * @param tamanhoEntrada maior tamanho de entrada do experimento
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