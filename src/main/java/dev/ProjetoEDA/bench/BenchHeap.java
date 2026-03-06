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

public class BenchHeap extends BenchAbstrato{

    @Override
    public void run(){

        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/result.csv"; 

        try{
            super.lerDados();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))){

             // Verifica se o arquivo está vazio para adicionar o cabeçalho
            if (new File(resultFilePath).length() == 0) {
            writer.write("TamnhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
            }

            testsRandom(writer);
            
        }

        }catch(IOException io){
            io.printStackTrace();
        }

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    @Override
    protected void tests(BufferedWriter aux) throws IOException{

       String[] ordens = new String[]{"random", "crescente", "decrescente"};
       String[] casos = new String[]{"100I0R", "50I50R", "75I25R"};

        for(String orden : ordens){
            for(int i : super.getDados("entradas")){
                for(String caso: casos ){
                    testar(i, orden, caso, aux);
                }
            }
        }
    }

    private void testar(int entrada, String ordem, String caso, BufferedWriter writer){
        super.experimento(entrada, "Heap", caso, writer, ordem);
    }

    @Override
    protected void executarI100_R0_S0(List<Integer> dados, int n){
        Heap hp = new Heap();

        for(int i = 0; i < n; i++){
            hp.add(dados.get(i));
        }
    }

    @Override
    protected void executarI50_R50_S0(List<Integer> dados, int n){
        Heap hp = new Heap();

        for(int i = 0; i < (n*0.5); i++){
            hp.add(dados.get(i));
        }
        for(int i = 0; i < (n*0.5); i++){
            hp.remove();
        }
    }

    @Override
    protected void executarI75_R25_S0(List<Integer> dados, int n){
         Heap hp = new Heap();

        for(int i = 0; i < (n*0.75); i++){
            hp.add(dados.get(i));
        }
        for(int i = 0; i < (n*0.25); i++){
            hp.remove();
        }
    }

    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n){
        return;
    }
    

    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n){
        return;
    }
    
}
