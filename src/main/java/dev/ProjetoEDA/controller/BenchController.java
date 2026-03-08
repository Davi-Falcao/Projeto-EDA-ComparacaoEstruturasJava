package dev.ProjetoEDA.controller;

import dev.ProjetoEDA.service.bench.Bench;
import dev.ProjetoEDA.service.bench.BenchArrayList;
// import dev.ProjetoEDA.service.bench.BenchBST;
// import dev.ProjetoEDA.service.bench.BenchHeap;
// import dev.ProjetoEDA.service.bench.BenchLinkedList;
// import dev.ProjetoEDA.service.bench.BenchPV;
// import dev.ProjetoEDA.service.bench.BenchPriorityQueue;

/**
 * Controller responsável por selecionar a estrutura
 * e executar todos os benchmarks associados.
 */
public class BenchController {

    /**
     * Executa todos os benchmarks da estrutura informada.
     *
     * @param estrutura nome da estrutura
     * @param tamanhoEntrada tamanho máximo da entrada
     */
    public void executar(String estrutura, int tamanhoEntrada) {
        Bench bench = criarBench(estrutura);
        bench.definirEntrada(tamanhoEntrada);
        bench.run();
    }

    /**
     * Cria a instância de benchmark da estrutura escolhida.
     *
     * @param estrutura nome da estrutura
     * @return benchmark correspondente
     */
    private Bench criarBench(String estrutura) {
        switch (estrutura.toLowerCase()) {
            case "arraylist":
                return new BenchArrayList();
            // case "linkedlist":
            //     return new BenchLinkedList();
            // case "heap":
            //     return new BenchHeap();
            // case "priorityqueue":
            //     return new BenchPriorityQueue();
            // case "avl":
            //     return new BenchAVL();
            // case "bst":
            //     return new BenchBST();
            // case "pv":
            //     return new BenchPV();
            default:
                throw new IllegalArgumentException("Benchmark desconhecido: " + estrutura);
        }
    }
}