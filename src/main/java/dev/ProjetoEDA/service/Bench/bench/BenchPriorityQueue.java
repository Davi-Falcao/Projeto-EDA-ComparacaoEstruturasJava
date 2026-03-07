package dev.ProjetoEDA.bench;

import dev.ProjetoEDA.model.PriorityQueue;
import java.io.*;
import java.util.*;


/**
 * Implementação concreta da classe {@link Bench} responsável
 * por executar os experimentos de benchmark da estrutura de dados Priority Queue.
 *
 * Esta classe executa diferentes cenários de operações sobre a Priority Queue
 * utilizando conjuntos de dados previamente carregados.
 * Os resultados de tempo de execução e consumo de memória são registrados
 * em um arquivo CSV para posterior análise e geração de gráficos.
 *
 * Os cenários atualmente implementados são:
 *
 * 100I0R0S - 100% inserções
 * 50I50R0S - 50% inserções e 50% remoções
 * 75I25R0S - 75% inserções e 25% remoções
 *
 * Os dados utilizados nos testes podem estar em três ordens:
 *
 * random
 * crescente
 * decrescente
 * 
 * Cada experimento é executado múltiplas vezes para amenizar os ruídos e o Garbage Collector
 * e a mediana das medições é registrada no arquivo de resultados.
 */
public class BenchPriorityQueue extends Bench {

    /**
     * Executa o benchmark da estrutura Priority Queue.
     *
     * Este método realiza:
     *
     * Leitura dos dados de entrada
     * Abertura do arquivo de resultados
     * Execução dos testes definidos
     *
     * Os resultados são gravados no arquivo:
     * {@code repository/results/result.csv}.
     */
    @Override
    public void run() {
        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultPriorityQueue.csv";

        try {
            super.lerDados();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))) {

                if (new File(resultFilePath).length() == 0) {
                    writer.write("TamanhoEntrada,TipoEntrada,Caso,Estrutura,TempoExecucao(ns),MemoriaUso(bytes)\n");
                }

                test(writer);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    /**
     * Executa todos os cenários de teste definidos para a Priority Queue.
     *
     * Para cada tipo de entrada (random, crescente, decrescente) e para
     * cada tamanho de entrada definido no dataset, são executados os
     * cenários de benchmark.
     *
     * @param writer objeto responsável por escrever os resultados no CSV
     * @throws IOException caso ocorra erro na escrita do arquivo
     */
    @Override
    protected void test(BufferedWriter writer) throws IOException {
        String[] ordens = new String[]{"random", "crescente", "decrescente"};
        String[] casos = new String[]{"100I0R0S", "50I50R0S", "75I25R0S"};

        for (String ordem : ordens) {
            for (int n : super.getDados("entradas")) {
                for (String caso : casos) {

                    super.experimento(n, ordem, caso, "PriorityQueue", writer);
            
                }
            }
        }
    }

    /**
     * Executa o cenário com 100% operações de inserção.
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI100_R0_S0(List<Integer> dados, int n) {
        PiorityQueue pq = new PriorityQueue();

        for (int i = 0; i < n; i++) {
            pq.add(dados.get(i));
        }
    }

    /**
     * Executa o cenário com 50% inserções e 50% remoções.
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI50_R50_S0(List<Integer> dados, int n) {
        PiorityQueue pq = new PriorityQueue();
        int metade = n / 2;

        for (int i = 0; i < metade; i++) {
            pq.add(dados.get(i));
        }
        for (int i = 0; i < metade; i++) {
            pq.remove();
        }
    }

    /**
     * Executa o cenário com 75% inserções e 25% remoções.
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI75_R25_S0(List<Integer> dados, int n) {
        PiorityQueue pq = new PriorityQueue();

        int insercoes = (int) (n * 0.75);
        int remocoes = (int) (n * 0.25);

        for (int i = 0; i < insercoes; i++) {
            pq.add(dados.get(i));
        }
        for (int i = 0; i < remocoes; i++) {
            pq.remove();
        }
    }

    /**
     * Cenário com 50% inserções, 25% remoções e 25% buscas.
     *
     * Este cenário não é utilizado para Priority Queue.
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n) {
        return; 
    }

    /**
     * Cenário com 50% inserções, 0% remoções e 50% buscas.
     *
     * Este cenário não é utilizado para  Priority Queue.
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n) {
        return;
    }
}
