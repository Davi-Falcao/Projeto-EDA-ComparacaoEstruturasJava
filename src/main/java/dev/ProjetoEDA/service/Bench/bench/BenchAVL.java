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

import dev.ProjetoEDA.model.AVL;

public class BenchAVL extends BenchAbstrato{

    public static void run(){

        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultAVL.csv";

        try{
            super.lerDados();

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))){
                if (new File(resultFilePath).length() == 0) {
                    writer.write("TamnhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
                }

                super.test(writer);
            }

        }catch(IOException io){
            io.printStackTrace();
        }

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    @Override
     protected void test(BufferedWriter aux) throws IOException{

        String[] ordens = new String[]{"random", "crescente", "decrescente"};

        for(String orden : ordens){

            for(int i : entradas){
                run100I0R(i, orden, aux);
                run50I50R(i, orden, aux);
                run75I25R(i,orden, aux);
            }
    }

    }   

    private void run100I0R(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada, "AVL", "100I0R", writer, tipo);
    }

    private  void run50I50R(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada,"AVL", "50I50R", writer, tipo);
    }

    private void run75I25R(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada,"AVL", "75I25R", writer, tipo);
    }

    private void run50I50R0S(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada,"AVL", "50I50R0S", writer, tipo);
    }

    private void run50I25R25S(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada,"AVL", "50I25R25S", writer, tipo);
    }

    private void run50I0R50S(int entrada, String tipo, BufferedWriter writer){
        experimento(entrada,"AVL", "50I0R50S", writer, tipo);
    }


}
