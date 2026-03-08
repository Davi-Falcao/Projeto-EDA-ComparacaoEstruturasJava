package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.Estrutura;
import dev.ProjetoEDA.model.PriorityQueue;


public class BenchPriorityQueue extends Bench {

    @Override
    public void run() {
        try {
            lerDados();

            String[] casos = {
                    "100I0R0S",
                    "50I50R0S",
                    "75I25R0S",
                    "50I25R25S",
                    "50I0R50S"
            };

            executarPorOrdem("random", casos);
            executarPorOrdem("crescente", casos);
            executarPorOrdem("decrescente", casos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Estrutura criarEstrutura() {
        Estrutura estrutura = new PriorityQueue();
        return estrutura;
    }

    @Override
    protected String getNomeEstrutura() {
        return "PriorityQueue";
    }
}