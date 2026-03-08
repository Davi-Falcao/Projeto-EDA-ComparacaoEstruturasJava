package dev.ProjetoEDA.service;

import dev.ProjetoEDA.model.Estrutura;
import dev.ProjetoEDA.model.PriorityQueue;
/*
 * Benchmark concreto da estrutura PriorityQueue.
 */
public class BenchPriorityQueue extends Bench {
    @Override
    protected Estrutura criarEstrutura() {return new PriorityQueue();}

    @Override
    protected String getNomeEstrutura() {return "PriorityQueue";}
}
