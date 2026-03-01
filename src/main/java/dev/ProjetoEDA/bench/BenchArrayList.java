package dev.ProjetoEDA.bench;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;

import dev.ProjetoEDA.estruturas.arraylist.ArrayList;

/**
 * Classe que realiza o benchmark de operações (inserção, remoção e busca) em uma estrutura de dados 
 * ArrayList personalizada. Para cada operação, o tempo de execução e o uso de memória são medidos
 * em 5 iterações, e os valores medianos são gravados em um arquivo CSV.
 */
public class BenchArrayList {

    /**
     * Método principal que executa o benchmark das operações em um ArrayList.
     * Ele lê um arquivo CSV contendo uma sequência de operações (inserção, remoção e busca),
     * executa cada operação no ArrayList 30 vezes para garantir a estabilidade dos resultados,
     * e grava os tempos de execução e uso de memória no arquivo de saída em formato CSV.
     * 
     * @param args Argumentos passados pela linha de comando (que deve incluir o JSON de configuração).
     * @throws IOException Caso ocorra algum erro na leitura ou escrita de arquivos.
     */
    public static void main(String[] args) throws IOException {
        boolean ordemAdicao = false;

        // Lê a configuração do arquivo JSON, caso fornecido
        if (args.length > 0) {
            try {
                String jsonConfig = args[0];
                JSONObject config = new JSONObject(jsonConfig);
                ordemAdicao = config.getBoolean("OrdemAdicao");
            } catch (Exception e) {
                System.err.println("Erro ao processar o JSON fornecido. Usando valor padrão para OrdemAdicao.");
            }
        } 

        // Caminhos dos arquivos
        String filePath = "data/entradas/ArrayList/OrdemDeAdicao.csv";  
        String resultFilePath = "data/results/ArrayList/resultOrdemDeAdicao.csv"; 

        // Cria o BufferedWriter para gravar os resultados no arquivo
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true));

        // Verifica se o arquivo está vazio para adicionar o cabeçalho
        if (new File(resultFilePath).length() == 0) {
            writer.write("Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }

        // Lê o arquivo de entrada com as operações
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Cria a estrutura ArrayList personalizada com tamanho inicial
        ArrayList lista = new ArrayList(10000);

        // Processa cada linha do arquivo de entrada
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
                        int indexInsert = Integer.parseInt(indice);
                        int valueInsert = Integer.parseInt(valor);
                        if (ordemAdicao) { 
                            lista.add(valueInsert);  // Adiciona no final da lista
                        } else {
                            lista.add(indexInsert, valueInsert);  // Adiciona no índice específico
                        }
                        break;
                    case "R": 
                        int indexRemove = Integer.parseInt(indice);
                        lista.remove(indexRemove);  // Remove do índice específico
                        break;
                    case "S": 
                        int valueSearch = Integer.parseInt(valor);
                        lista.indexOf(valueSearch);  // Realiza a busca
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

        // Fecha os leitores e escritores
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