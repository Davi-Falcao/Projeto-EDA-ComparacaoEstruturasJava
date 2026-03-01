import java.io.*;
import java.util.*;
import estruturas.arraylist.ArrayList;

/**
 * Classe que realiza o benchmark de operações (inserção, remoção e busca) em uma estrutura de dados 
 * ArrayList personalizada. Para cada operação, o tempo de execução e o uso de memória são medidos
 * em 5 iterações, e os valores medianos são gravados em um arquivo CSV.
 */
public class BenchArrayListInsertion {

    /**
     * Método principal que executa o benchmark das operações em um ArrayList.
     * Ele lê um arquivo CSV contendo uma sequência de operações (inserção, remoção e busca),
     * executa cada operação no ArrayList 5 vezes para garantir a estabilidade dos resultados,
     * e grava os tempos de execução e uso de memória no arquivo de saída em formato CSV.
     * 
     * Para cada operação:
     * - O tempo de execução é medido com precisão de nanossegundos.
     * - O uso de memória é calculado com base na diferença entre a memória total antes e depois da operação.
     * 
     * O valor mediano do tempo e da memória de cada operação é calculado após 5 execuções e registrado no CSV.
     * 
     * @param args Argumentos passados pela linha de comando (não utilizados).
     * @throws IOException Caso ocorra algum erro na leitura ou escrita de arquivos.
     */
    public static void main(String[] args) throws IOException {
        String filePath = "data/entradas/ArrayList/OrdemDeAdicao.csv";  
        String resultFilePath = "data/results/ArrayList/resultOrdemDeAdicao.csv"; 

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true));

        writer.write("Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        ArrayList lista = new ArrayList(10000);

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String operacao = parts[0].trim();
            String indice = parts[1].trim();
            String valor = parts[2].trim();

            int[] tempos = new int[30];
            int[] memorias = new int[30];

            // Executa a operação 30 vezes para calcular a mediana
            for (int i = 0; i < 30; i++) {
                long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoAntes = System.nanoTime();

                // Executa a operação correspondente
                switch (operacao) {
                    case "I": 
                        int valueInsert = Integer.parseInt(valor);
                        lista.add(valueInsert);
                        break;
                    case "R": 
                        int indexRemove = Integer.parseInt(indice);
                        lista.remove(indexRemove);
                        break;
                    case "S": 
                        int valueSearch = Integer.parseInt(valor);
                        lista.indexOf(valueSearch);
                        break;
                }

                long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoDepois = System.nanoTime();

                long tempoExecucao = tempoDepois - tempoAntes;
                long memoriaUso = memoriaDepois - memoriaAntes;

                tempos[i] = (int) tempoExecucao;
                memorias[i] = (int) memoriaUso;
            }

            // Calcula a mediana dos tempos e memorias para essa operação
            int tempoMediana = calcularMediana(tempos);
            int memoriaMediana = calcularMediana(memorias);

            // Grava o resultado no arquivo CSV
            writer.write(operacao + "," + lista.size() + "," + tempoMediana + "," + memoriaMediana + "\n");
        }

        reader.close();
        writer.close();

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    /**
     * Calcula a mediana de um array de inteiros.
     * 
     * A mediana é o valor central de um conjunto de números ordenados. Caso o número de elementos
     * seja par, a mediana será a média dos dois elementos centrais.
     * 
     * Este método ordena o array de valores e retorna o valor mediano.
     * 
     * @param valores Array de inteiros contendo os valores para os quais a mediana será calculada.
     * @return O valor da mediana.
     */
    public static int calcularMediana(int[] valores) {
        Arrays.sort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2]; 
        } else {
            int mediana = (valores[n / 2 - 1] + valores[n / 2]) / 2;  
            return mediana;
        }
    }
}