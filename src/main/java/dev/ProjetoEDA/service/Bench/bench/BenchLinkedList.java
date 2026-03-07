package dev.ProjetoEDA.service.Bench.bench;

import LinkedList.dev.ProjetoEDA.model.LinkedList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
//Documentar

public class BenchLinkedList extends Bench {
    @Override
    public void run(){
        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultLinkedList.csv";
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

        System.out.println("Resultados de LinkedList gravados em: " + resultFilePath);
    }
    @Override
    protected void test(BufferedWriter writer) throws IOException{
        String[] ordens = new String[] {"random", "crescente", "decrescente"};
        //Inclusão de cenários de busca(s) que fazem sentidos no âmbito de listas
        String[] casos = new String[]{"100I0R0S", "50I50R0S", "75I25R0S", "50I0R50S"};

        for (String ordem : ordens){
            for(int n : super.getDados("Entradas")) {
                for(String caso : casos) {
                    super.experimento(n, ordem, caso, "LinkedList", writer);
                }
            }
        }
    }
    @Override
    protected void executarI100_R0_S0(List<Integer> dados, int n) {
        LinkedList list = new LinkedList();
        for(int i = 0; i < n; i++){
            list.addLast(dados.get(i));
        }
    }
    @Override
    protected void executarI50_R50_S0(List<Integer> dados, int n) {
        LinkedList list = new LinkedList();

        for(int i = 0; i < n / 2; i++) {
            list.addLast(dados.get(i));
        }
        for(int i = 0; i < n /2; i++) {
            list.removeFirst();
        }
    }

    @Override
    protected void executarI75_R25_S0(List<Integer> dados, int n) {
        LinkedList list = new LinkedList();

        int insercoes = (int) (n * 0.75);
        int remocoes = (int) (n * 0.25);

        for(int i = 0; i < insercoes; i++) {
            list.addLast(dados.get(i));
        }
        for(int i = 0; i < remocoes; i++) {
            list.removeFirst();
        }
    }
    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n) {
        LinkedList list = new LinkedList();

        for(int i = 0; i < n /2; i ++) {
            list.addLast(dados.get(i));
        }
        for(int i = 0; i < n /2; i++) {
            list.contains(dados.get(i));
        }
    }
    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n) {
        LinkedList list = new LinkedList();

        int insercao = n /2;
        int remocao = n/4;
        int busca = n/4;

        for(int i = 0; i < insercao; i++) list.addLast(dados.get(i));
        for(int i = 0; i < remocao; i++) list.removeFirst();
        for(int i = 0; i < busca; i++) list.contains(dados.get(i));
    }
}
