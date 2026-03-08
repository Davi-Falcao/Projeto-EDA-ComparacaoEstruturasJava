package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.PriorityQueue;
import dev.ProjetoEDA.model.Estrutura;
/*
 * Benchmark concreto da estrutura PriorityQueue.
 */
public class BenchPriorityQueue extends Bench {
    @Override
    protected Estrutura criarEstrutura() {return new PriorityQueue();}

    @Override
    protected String getNomeEstrutura() {return "PriorityQueue";}
}
