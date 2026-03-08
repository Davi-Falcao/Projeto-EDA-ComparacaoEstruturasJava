package dev.ProjetoEDA.service;

import dev.ProjetoEDA.model.Estrutura;
import dev.ProjetoEDA.model.Heap;
/*
 * Benchmark concreto da estrutura Heap.
 */
public class BenchHeap extends Bench{

    @Override
    protected Estrutura criarEstrutura() {return new Heap();}

    @Override
    protected String getNomeEstrutura() {return "Heap";}
}
