package dev.ProjetoEDA.service.Bench.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import dev.ProjetoEDA.model.ArrayList;
import dev.ProjetoEDA.service.Bench.Bench;

/**
 * Implementação concreta da classe {@link Bench} responsável
 * por executar os experimentos de benchmark da estrutura de dados ArrayList.
 *
 * Esta classe executa diferentes cenários de operações sobre a ArrayList
 * utilizando conjuntos de dados previamente carregados.
 * Os resultados de tempo de execução e consumo de memória são registrados
 * em um arquivo CSV para posterior análise e geração de gráficos.
 *
 * Os cenários atualmente implementados são:
 *
 * 100I0R0S - 100% inserções
 * 50I50R0S - 50% inserções e 50% remoções
 * 75I25R0S - 75% inserções e 25% remoções
 * 50I25R25S - 50% inserções, 25% remoções e 25% de busca por elemento
 * 50I0R50S - 50% inserções e 50% de busca por elemento
 *
 * Os dados utilizados nos testes podem estar em três ordens:
 *
 * random
 * crescente
 * decrescente
 *
 * Cada experimento é executado múltiplas vezes para amenizar os ruídos e a atuação do Garbage Collector
 * e a mediana das medições é registrada no arquivo de resultados.
 */
public class BenchArrayList extends Bench {

    /**
     * Executa o benchmark da estrutura ArrayList.
     *
     * Este método realiza:
     *
     * Leitura dos dados de entrada
     * Abertura do arquivo de resultados
     * Execução dos testes definidos
     *
     * Os resultados são gravados no arquivo:
     * {@code repository/results/resultArrayList.csv}.
     */
    @Override
    public void run() {
        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultArrayList.csv";

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
     * Executa todos os cenários de teste definidos para a ArrayList.
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
        String[] casos = new String[]{"100I0R0S", "50I50R0S", "75I25R0S", "50I25R25S", "50I0R50S" };

        for (String ordem : ordens) {
            for (int n : super.getDados("entradas")) {
                for (String caso : casos) {
                    super.experimento(n, ordem, caso, "ArrayList", writer);
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
        ArrayList list = new ArrayList();

        for (int i = 0; i < n; i++) {
            list.add(dados.get(i));
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
        ArrayList list = new ArrayList();
        int metade = n / 2;

        for (int i = 0; i < metade; i++) {
            list.add(dados.get(i));
        }

        for (int i = 0; i < metade; i++) {
            list.remove(0);
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
       ArrayList list = new ArrayList();

        int insercoes = (int) (n * 0.75);
        int remocoes = (int) (n * 0.25);

        for (int i = 0; i < insercoes; i++) {
            list.add(dados.get(i));
        }

        for (int i = 0; i < remocoes; i++) {
            list.remove(0);
        }
    }

    /**
     * Cenário com 50% inserções, 25% remoções e 25% buscas.
     *
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n) {
       ArrayList list = new ArrayList();

        int insercoes = (int) (n * 0.50);
        int remocoes = (int) (n * 0.25);
        int procura = (int) (n * 0.25);

        // inserções
        for (int i = 0; i < insercoes; i++) {
            list.add(dados.get(i));
        }

        // buscas
        for (int i = 0; i < procura; i++){
            list.search(dados.get(i));
        }

        // remoções
        for (int i = 0; i < remocoes; i++) {
            list.remove(0);
        } 
    }

    /**
     * Cenário com 50% inserções, 0% remoções e 50% buscas.
     *
     *
     * @param dados conjunto de dados de entrada
     * @param n tamanho da entrada
     */
    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n) {
        ArrayList list = new ArrayList();

        int insercoes = (int) (n * 0.50);
        int procura = (int) (n * 0.50);

        // inserções
        for (int i = 0; i < insercoes; i++) {
            list.add(dados.get(i));
        }

        // buscas
        for (int i = 0; i < procura; i++){
            list.contains(dados.get(i));
        }
    }
}