package dev.ProjetoEDA.controller;

import dev.ProjetoEDA.service.bench.Bench;
import dev.ProjetoEDA.service.bench.BenchAVL;
import dev.ProjetoEDA.service.bench.BenchArrayList;
import dev.ProjetoEDA.service.bench.BenchBST;
import dev.ProjetoEDA.service.bench.BenchHeap;
import dev.ProjetoEDA.service.bench.BenchLinkedList;
import dev.ProjetoEDA.service.bench.BenchPV;
import dev.ProjetoEDA.service.bench.BenchPriorityQueue;

public class BenchController {

    /**
     * Método principal para escolher e executar o benchmark com base no caso fornecido.
     *
     * @param caso Nome da estrutura a ser executada.
     * @param tamanhoEntrada Tamanho da entrada usado no benchmark.
     */
    public void escolherOCaso(String caso, int tamanhoEntrada) {
        Bench bench = null;

        switch (caso.toLowerCase()) {
            case "arraylist":
                bench = new BenchArrayList();
                break;
            case "linkedlist":
                bench = new BenchLinkedList();
                break;
            case "heap":
                bench = new BenchHeap();
                break;
            case "priorityqueue":
                bench = new BenchPriorityQueue();
                break;
            case "avl":
                bench = new BenchAVL();
                break;
            case "bst":
                bench = new BenchBST();
                break;
            case "pv":
                bench = new BenchPV();
                break;
            default:
                System.err.println("Benchmark desconhecido: " + caso);
                return;
        }

        bench.definirEntrada(tamanhoEntrada);
        bench.run();
    }
}