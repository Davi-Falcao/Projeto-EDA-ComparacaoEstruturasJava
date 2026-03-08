package dev.ProjetoEDA.service;
import dev.ProjetoEDA.model.Estrutura;
import dev.ProjetoEDA.model.PV;
/*
 * Benchmark concreto da estrutura PV.
 */
public class BenchPV extends Bench{

    @Override
    protected Estrutura criarEstrutura() {return new PV();}

    @Override
    protected String getNomeEstrutura() {return "PV";}
}
