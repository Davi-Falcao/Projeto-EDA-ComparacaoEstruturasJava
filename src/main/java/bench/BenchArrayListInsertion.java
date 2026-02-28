import java.io.*;
import java.util.*;
import estruturas.arraylist.ArrayList;

public class BenchArrayListInsertion {
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

            int[] tempos = new int[5];
            int[] memorias = new int[5];

            for (int i = 0; i < 10; i++) {
                long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoAntes = System.nanoTime();

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

            int tempoMediana = calcularMediana(tempos);
            int memoriaMediana = calcularMediana(memorias);

            writer.write(operacao + "," + lista.size() + "," + tempoMediana + "," + memoriaMediana + "\n");
        }

        reader.close();
        writer.close();

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

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