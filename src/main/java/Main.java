package LinkedList;
import LinkedList.estruturas.LinkedList;

import java.io.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePathInput = ""; //colocar caminho
        String filePathOutput = ""; // colocar caminho;

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathOutput));
        writer.write("Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");

        BufferedReader reader = new BufferedReader(new FileReader(filePathInput));
        String linha;
        LinkedList lista = new LinkedList();

        while ((linha = reader.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split(", ");
            String comando = partes[0].trim();
            int index = Integer.parseInt(partes[1].trim());
            int valor = Integer.parseInt(partes[2].trim());

            long[] tempo = new long[10];
            long[] memoria = new long[10];

            for(int i = 0; i < 10; i ++){
                System.gc();

                long memoriaAnterior = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoAntes = System.nanoTime();

                switch (comando){
                    case "I":
                        lista.addLast(valor);
                        break;
                    case "R":
                        if(!lista.isEmpty() && index < lista.size()) {
                            lista.removeLast();
                        }
                        break;
                    case "S":
                        lista.contains(valor);
                        break;
                }
                long tempoDepois = System.nanoTime();
                long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                tempo[i] = tempoDepois - tempoAntes;
                memoria[i] = Math.max(0, memoriaDepois - memoriaAnterior);
            }

            long tempoMediana = calcularMediana(tempo);
            long memoriaMediana = calcularMediana(memoria);

            writer.write(comando + "," + lista.size() + "," + tempoMediana + "," + memoriaMediana + "\n");
        }
        reader.close();
        writer.close();
        System.out.println("Resultados gravados em: " + filePathOutput);
    }

    public static long calcularMediana(long[] valores) {
        Arrays.sort(valores);
        int n = valores.length;
        if(n % 2 == 1){
            return valores[n / 2];
        } else {
            return (valores[n / 2 - 1] + valores[n / 2]) / 2;
        }
    }
}

