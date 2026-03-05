package dev.ProjetoEDA.bench;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.ProjetoEDA.model.Heap;

public class BenchHeap {

    private static List<Integer> random;
    private static List<Integer> crescente;
    private static List<Integer> decresente;
    private static  List<Integer> entradas;

    private static final int REPETICOES = 18;

    public static void main(String[] args){

        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/result.csv"; 

        try{
            lerDados();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))){
             // Verifica se o arquivo está vazio para adicionar o cabeçalho
            if (new File(resultFilePath).length() == 0) {
            writer.write("TamnhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
            }

            testsRandom(writer);
            //rodaCrescente();
            //rodaDecresente();
        }

        }catch(IOException io){
            io.printStackTrace();
        }

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
        List<Integer> dados = null;

        switch (tipo) {
            case "random":
                dados = random;
                break;
            case "crescente":
                dados = crescente;
                break;
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
                       for(int i = 0; i < entrada; i ++) hp.add(dados.get(i));
                        break;
                    case "50I50R":
                        for(int i = 0; i < (int)(entrada*0.5); i ++) hp.add(dados.get(i));
                        for(int i = 0; i < (int)(entrada*0.5); i++) hp.remove();
                        break;
                    case "75I25R":
                        for(int i = 0; i < (int)(entrada*0.75); i ++) hp.add(dados.get(i));
                        for(int i = 0; i < (int)(entrada*0.25); i++) hp.remove();
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
            try{
                writer.write(
                    entrada + "," +          // TamanhoEntrada
                    test + "," +             // Caso
                    "Heap" + "," +           // Estrutura
                    tipo + "," +             // TipoEntrada
                    tempoMediana + "," +     // TempoExecucao(ns)
                    memoriaMediana + "\n"    // MemoriaUso(bytes)
                );
                
            }catch(IOException io){
            }

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
        String caminhoRandom = "src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv";
        String caminhoCrescente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv";
        String caminhoDecresente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv";
        String caminhoentradas = "src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntrada.csv";

        random = Files.lines(Paths.get(caminhoRandom)).map(Integer::valueOf).collect(Collectors.toList());
        crescente = Files.lines(Paths.get(caminhoCrescente)).map(Integer::valueOf).collect(Collectors.toList());
        decresente = Files.lines(Paths.get(caminhoDecresente)).map(Integer::valueOf).collect(Collectors.toList());
        entradas = Files.lines(Paths.get(caminhoentradas)).map(Integer::valueOf).collect(Collectors.toList());
    }

    
}
