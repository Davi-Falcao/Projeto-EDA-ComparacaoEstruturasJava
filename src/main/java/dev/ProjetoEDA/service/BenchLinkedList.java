package dev.ProjetoEDA.service;

import dev.ProjetoEDA.model.Estrutura;
import dev.ProjetoEDA.model.LinkedList;

/*
* Benchmark concreto da estrutura de LinkedList.
 */
public class BenchLinkedList extends Bench {

    @Override
    protected Estrutura criarEstrutura() { return new LinkedList(); }

    @Override
    protected String getNomeEstrutura() {return "LinkedList";}
}
