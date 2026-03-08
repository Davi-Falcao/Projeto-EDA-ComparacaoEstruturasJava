package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.AVL;
import dev.ProjetoEDA.model.Estrutura;

/*
 * Benchmark concreto da estrutura AVL.
 */
public class BenchAVL extends Bench {

    @Override
    protected Estrutura criarEstrutura() {
        return new AVL();
    }

    @Override
    protected String getNomeEstrutura() {
        return "AVL";
    }
}