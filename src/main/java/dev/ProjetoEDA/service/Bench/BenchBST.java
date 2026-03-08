package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.BST;
import dev.ProjetoEDA.model.Estrutura;
/*
 * Benchmark concreto da estrutura BST.
 */
public class BenchBST extends Bench {

    @Override
    protected Estrutura criarEstrutura() {return new BST();}

    @Override
    protected String getNomeEstrutura() {return "BST";}
}
