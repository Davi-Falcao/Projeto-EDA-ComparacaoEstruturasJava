package dev.ProjetoEDA.service.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import dev.ProjetoEDA.model.PV;

public class BenchPV extends Bench {

    @Override
    public void run() {

        String resultFilePath = "src/main/java/dev/ProjetoEDA/repository/results/resultPV.csv";

        try {
            super.lerDados();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true))) {

                if (new File(resultFilePath).length() == 0) {
                    writer.write("TamanhoEntrada,Caso,Estrutura,TipoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
                }

                test(writer);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    @Override
    protected void test(BufferedWriter aux) throws IOException {

        String[] ordens = new String[]{"random", "crescente", "decrescente"};

        String[] casos = new String[]{
                "100I0R0S",
                "50I50R0S",
                "75I25R0S",
                "50I25R25S",
                "50I0R50S"
        };

        for (String ordem : ordens) {
            for (int i : super.getDados("entradas")) {
                for (String caso : casos) {
                    testar(i, ordem, caso, aux);
                }
            }
        }
    }

    private void testar(int entrada, String ordem, String caso, BufferedWriter writer) {
        super.experimento(entrada, ordem, caso, "PV", writer);
    }

    @Override
    protected void executarI100_R0_S0(List<Integer> dados, int n) {

        PV pv = new PV();

        for (int i = 0; i < n; i++) {
            pv.add(dados.get(i));
        }
    }

    @Override
    protected void executarI50_R50_S0(List<Integer> dados, int n) {

        PV pv = new PV();
        int metade = n / 2;

        for (int i = 0; i < metade; i++) {
            pv.add(dados.get(i));
        }

        for (int i = 0; i < metade; i++) {
            pv.remove(dados.get(i));
        }
    }

    @Override
    protected void executarI75_R25_S0(List<Integer> dados, int n) {

        PV pv = new PV();

        int insercoes = (int) (n * 0.75);
        int remocoes = (int) (n * 0.25);

        for (int i = 0; i < insercoes; i++) {
            pv.add(dados.get(i));
        }

        for (int i = 0; i < remocoes; i++) {
            pv.remove(dados.get(i));
        }
    }

    @Override
    protected void executarI50_R25_S25(List<Integer> dados, int n) {

        PV pv = new PV();

        int insercoes = (int) (n * 0.50);
        int remocoes = (int) (n * 0.25);
        int procura = (int) (n * 0.25);

        for (int i = 0; i < insercoes; i++) {
            pv.add(dados.get(i));
        }

        for (int i = 0; i < procura; i++) {
            pv.search(dados.get(i));
        }

        for (int i = 0; i < remocoes; i++) {
            pv.remove(dados.get(i));
        }
    }

    @Override
    protected void executarI50_R0_S50(List<Integer> dados, int n) {

        PV pv = new PV();

        int insercoes = (int) (n * 0.50);
        int procura = (int) (n * 0.50);

        for (int i = 0; i < insercoes; i++) {
            pv.add(dados.get(i));
        }

        for (int i = 0; i < procura; i++) {
            pv.search(dados.get(i));
        }
    }
}
