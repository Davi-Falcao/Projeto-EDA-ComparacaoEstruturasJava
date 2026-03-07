package dev.ProjetoEDA.service.Bench.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import dev.ProjetoEDA.model.BST;
import dev.ProjetoEDA.service.Bench.Bench;

/**
 * Classe responsável por executar os benchmarks da estrutura BST.
 * 
 * Implementa os cenários de teste definidos na classe abstrata Bench,
 * executando inserções, remoções e buscas sobre uma Binary Search Tree.
 */
public class BenchBST extends Bench {

    /**
     * Método principal que inicia o benchmark da BST.
     * 
     * Realiza:
     * - leitura dos dados de entrada
     * - criação/abertura do arquivo CSV de resultados
     * - execução dos testes definidos
     */
    @Override
    public void run() {
        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultBST.csv";

        try {
            // lê os dados de entrada utilizados nos experimentos
            super.lerDados();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))) {

                // escreve cabeçalho caso o arquivo esteja vazio
                if (new File(resultFilePath).length() == 0) {
                    writer.write("TamanhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
                }

                // executa todos os testes definidos
                test(writer);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    /**
     * Executa todos os cenários de benchmark para a BST.
     * 
     * Para cada tipo de entrada (random, crescente, decrescente),
     * cada tamanho de entrada e cada cenário de operações,
     * é executado um experimento.
     *
     * @param aux writer responsável por registrar os resultados no CSV
     */
    @Override
    protected void test(BufferedWriter aux) throws IOException {

        // tipos de ordenação das entradas
        String[] ordens = new String[]{"random", "crescente", "decrescente"};

        // cenários de operações executados no benchmark
        String[] casos = new String[]{"100I0R0S", "50I50R0S", "75I25R0S","50I25R25S", "50I0R50S"};

        for (String ordem : ordens) {
            for (int i : super.getDados("entradas")) {
                for (String caso : casos) {
                    testar(i, ordem, caso, aux);
                }
            }
        }
    }

    /**
     * Executa um experimento específico da BST.
     *
     * @param entrada tamanho da entrada
     * @param ordem tipo de ordenação dos dados
     * @param caso cenário de operações
     * @param writer writer utilizado para registrar o resultado
     */
    private void testar(int entrada, String ordem, String caso, BufferedWriter writer) {
        super.experimento(entrada, ordem, caso, "BST", writer);
    }

    /**
     * Cenário: 100% inserções.
     * 
     * Insere todos os elementos na BST.
     */
    @Override
    protected void executarI100_R0_S0(List<Integer> dados, int n) {
        BST bst = new BST();

        for (int i = 0; i < n; i++) {
            bst.add(dados.get(i));
        }
    }

    /**
     * Cenário: 50% inserções e 50% remoções.
     * 
     * Primeiro metade dos elementos é inserida,
     * depois os mesmos elementos são removidos.
     */
    @Override
    protected void executarI50_R50_S0(List<Integer> dados, int n) {
        BST bst = new BST();
        int metade = n / 2;

        for (int i = 0; i < metade; i++) {
            bst.add(dados.get(i));
        }

        for (int i = 0; i < metade; i++) {
            bst.remove(dados.get(i));
        }
    }

    /**
     * Cenário: 75% inserções e 25% remoções.
     */
    @Override
    protected void executarI75_R25_S0(List<Integer> dados, int n) {
        BST bst = new BST();

        int insercoes = (int) (n * 0.75);
        int remocoes = (int) (n * 0.25);

        for (int i = 0; i < insercoes; i++) {
            bst.add(dados.get(i));
        }

        for (int i = 0; i < remocoes; i++) {
            bst.remove(dados.get(i));
        }
    }

    /**
     * Cenário: 50% inserções, 25% buscas e 25% remoções.
     */
    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n) {
        BST bst = new BST();

        int insercoes = (int) (n * 0.50);
        int remocoes = (int) (n * 0.25);
        int procura = (int) (n * 0.25);

        // inserções
        for (int i = 0; i < insercoes; i++) {
            bst.add(dados.get(i));
        }

        // buscas
        for (int i = 0; i < procura; i++){
            bst.search(dados.get(i));
        }

        // remoções
        for (int i = 0; i < remocoes; i++) {
            bst.remove(dados.get(i));
        } 
    }

    /**
     * Cenário: 50% inserções e 50% buscas.
     */
    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n) {
        BST bst = new BST();

        int insercoes = (int) (n * 0.50);
        int procura = (int) (n * 0.50);

        // inserções
        for (int i = 0; i < insercoes; i++) {
            bst.add(dados.get(i));
        }

        // buscas
        for (int i = 0; i < procura; i++){
            bst.search(dados.get(i));
        }
    }
}
