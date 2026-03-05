package dev.ProjetoEDA.bench;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ModeloBench {

    private static List<Integer> random;
    private static List<Integer> crescente;
    private static List<Integer> decresente;
    private static  List<Integer> entradas;

    private static final int REPETICOES = 18;

    public static void main(String[] args){

        String resultFilePath = "repository/results/..../result....csv"; 

        try{

        lerDados();

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true));

        // Verifica se o arquivo está vazio para adicionar o cabeçalho
        if (new File(resultFilePath).length() == 0) {
            writer.write("TamnhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }

        testsRandom(writer);
        //rodaCrescente();
        //rodaDecresente();

        writer.close();
        }catch(IOException io){}

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    private static void testsRandom(BufferedWriter aux) throws IOException{

        for(int i : entradas){
            run100I0R(i, "random", aux);
            run50I50R(i, "random", aux);
            run75I25R(i, "random", aux);
        }

    }

    private static void run100I0R(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada, "100I0R", writer, tipo);
    }

     private static void run50I50R(int entrada, String tipo, BufferedWriter writer){
       experimento(entrada, "50I50R", writer, tipo);
    }

      private static void run75I25R(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada, "75I25R", writer, tipo);
    }

    private static void experimento(int entrada, String test, BufferedWriter writer, String tipo){
        List<> dados = null;

        switch (tipo) {
            case "random":
                dados = random;
                break;
            case "crescente":
                dados = crescente;
            default:
                dados = decresente;
                break;
        }
        long[] tempos = new long[REPETICOES];
        long[] memorias = new long[REPETICOES];

            for (int s = 0; s < REPETICOES; s++) {
                long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoAntes = System.nanoTime();

                Heap hp = new Heap();

                switch (test) {
                    case "100I0R":
                        //hp.add();
                        //a implementar..
                        break;
                    case "50I50R":
                        //a implementar..
                        break;
                    case "75I25R":
                        // a implementar..
                        break;
                    default:
                        //a implementar
                        break;
                }

                long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long tempoDepois = System.nanoTime();

                long tempoExecucao = tempoDepois - tempoAntes;
                long memoriaUso = memoriaDepois - memoriaAntes;

                tempos[s] =  tempoExecucao;
                memorias[s] =  memoriaUso;
            }

            long tempoMediana = calcularMediana(tempos);
            long memoriaMediana = calcularMediana(memorias);

            writer.write(test + "," + entrada  + "," + tipo + "," + tempoMediana + "," + memoriaMediana + "\n");

        }

    private static long calcularMediana(long[] valores) {
        Arrays.sort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2]; 
        } else {
            long mediana = (valores[n / 2 - 1] + valores[n / 2]) / 2;  
            return mediana;
        }
    }

    private static void lerDados() throws IOException{
        String caminhoRandom = "data/entradas/dados/random.csv";
        String caminhoCrescente = "data/entradas/dados/crescente.csv";
        String caminhoDecresente = "data/entradas/dados/decresente.csv";
        String caminhoentradas = "data/entradas/tamanho/entradas.csv";

        random = Files.lines(Paths.get(caminhoRandom)).map(Integer::valueOf).collect(Collectors.toList());
        crescente = Files.lines(Paths.get(caminhoCrescente)).map(Integer::valueOf).collect(Collectors.toList());
        decresente = Files.lines(Paths.get(caminhoDecresente)).map(Integer::valueOf).collect(Collectors.toList());
        entradas = Files.lines(Paths.get(caminhoentradas)).map(Integer::valueOf).collect(Collectors.toList());
    }

}
