package dev.ProjetoEDA.service.bench;
import dev.ProjetoEDA.model.PV;
import dev.ProjetoEDA.model.Estrutura;
/*
 * Benchmark concreto da estrutura PV.
 */
public class BenchPV extends Bench{

    @Override
    protected Estrutura criarEstrutura() {return new PV();}

    @Override
    protected String getNomeEstrutura() {return "PV";}
}
