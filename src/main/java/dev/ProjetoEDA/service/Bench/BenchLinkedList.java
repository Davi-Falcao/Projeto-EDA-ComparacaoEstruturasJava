package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.LinkedList;
import dev.ProjetoEDA.model.Estrutura;

/*
* Benchmark concreto da estrutura de LinkedList.
 */
public class BenchLinkedList extends Bench {

    @Override
    protected Estrutura criarEstrutura() { return new LinkedList(); }

    @Override
    protected String getNomeEstrutura() {return "LinkedList";}
}
