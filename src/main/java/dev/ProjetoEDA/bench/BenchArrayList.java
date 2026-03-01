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
        boolean ordemBusca = false;
        JSONObject config = extrairConfigOpcional(args);

        if (config != null) {
            ordemBusca = config.optBoolean("OrdemBusca", false);
            
            ordemAdicao = config.optBoolean("OrdemAdicao", false);
        }

        String filePath = "data/entradas/ArrayList/OrdemDeBusca.csv";  
        String resultFilePath = "data/results/ArrayList/resultOrdemDeBusca.csv"; 

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true));

        // Verifica se o arquivo está vazio para adicionar o cabeçalho
        if (new File(resultFilePath).length() == 0) {
            writer.write("Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        ArrayList lista = new ArrayList(10000);


        // Processa cada linha do arquivo de entrada
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String operacao = parts[0].trim();
            String indice = parts[1].trim();
            String valor = parts[2].trim();

            int[] tempos = new int[30];
            int[] memorias = new int[30];

            // Executa a operação 5 vezes para calcular a mediana
            for (int i = 0; i < 30; i++) {
                long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoAntes = System.nanoTime();

                switch (operacao) {
                    case "I": 
                        int indexInsert = Integer.parseInt(indice);
                        int valueInsert = Integer.parseInt(valor);
                        if (ordemAdicao || ordemBusca) { 
                            lista.add(valueInsert);  
                        } else {
                            lista.add(indexInsert, valueInsert);  
                        }
                        break;
                    case "R": 
                        int indexRemove = Integer.parseInt(indice);
                        lista.remove(indexRemove); 
                        break;
                    case "S": 
                        int valueSearch = Integer.parseInt(valor);
                        int index = lista.indexOf(valueSearch);  
                        break;
                }

                long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoDepois = System.nanoTime();

                long tempoExecucao = tempoDepois - tempoAntes;
                long memoriaUso = memoriaDepois - memoriaAntes;

                tempos[i] = (int) tempoExecucao;
                memorias[i] = (int) memoriaUso;
            }

            int tempoMediana = calcularMediana(tempos);
            int memoriaMediana = calcularMediana(memorias);

            writer.write(operacao + "," + lista.size() + "," + tempoMediana + "," + memoriaMediana + "\n");
        }

        reader.close();
        writer.close();

        System.out.println("Resultados gravados em: " + resultFilePath);
    }
    
    /**
     * Extrai a configuração opcional fornecida como argumento de linha de comando no formato JSON.
     * 
     * Este método verifica se há argumentos passados para o programa. Se não houver nenhum argumento, 
     * ele retorna {@code null}. Se houver mais de um argumento, ele imprime uma mensagem de erro e também retorna {@code null}.
     * Caso o argumento seja um único JSON válido, ele tenta parseá-lo e retorná-lo como um objeto {@link JSONObject}.
     * Se ocorrer algum erro ao processar o JSON, uma mensagem de erro é exibida e {@code null} é retornado.
     * 
     * @param args O array de argumentos de linha de comando, que deve conter no máximo um argumento JSON.
     * @return O objeto {@link JSONObject} correspondente ao argumento JSON fornecido, ou {@code null} em caso de erro ou ausência de argumento válido.
     */
    private static JSONObject extrairConfigOpcional(String[] args) {
        // Verifica se não há argumentos fornecidos
        if (args.length == 0) {
            return null;
        }
    

        // Verifica se mais de um argumento foi fornecido e exibe um erro
        if (args.length > 1) {
            System.err.println("Apenas um argumento JSON e permitido. Configuracao sera ignorada.");
            return null;
        }

        // Tenta converter o primeiro argumento para um objeto JSON
        try {

            return new JSONObject(args[0]);
        } catch (Exception e) {

            // Em caso de erro ao processar o JSON, exibe uma mensagem de erro
            System.err.println("Erro ao processar o JSON fornecido. Configuracao sera ignorada.");
            return null;
        }
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


