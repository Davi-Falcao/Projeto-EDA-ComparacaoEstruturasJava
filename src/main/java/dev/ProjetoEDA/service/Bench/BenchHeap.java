package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.Heap;
import dev.ProjetoEDA.model.Estrutura;
/*
 * Benchmark concreto da estrutura Heap.
 */
public class BenchHeap extends Bench{

    @Override
    protected Estrutura criarEstrutura() {return new Heap();}

    @Override
    protected String getNomeEstrutura() {return "Heap";}
}
