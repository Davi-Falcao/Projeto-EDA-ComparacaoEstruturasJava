package dev.ProjetoEDA.service.bench;

import dev.ProjetoEDA.model.ArrayList;
import dev.ProjetoEDA.model.Estrutura;

/**
 * Benchmark concreto da estrutura ArrayList.
 */
public class BenchArrayList extends Bench {

    @Override
    protected Estrutura criarEstrutura() {
        return new ArrayList();
    }

    @Override
    protected String getNomeEstrutura() {
        return "ArrayList";
    }
}