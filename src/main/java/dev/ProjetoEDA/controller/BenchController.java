package dev.ProjetoEDA.controller;

import dev.ProjetoEDA.service.Bench;
import dev.ProjetoEDA.service.BenchAVL;
import dev.ProjetoEDA.service.BenchArrayList;
import dev.ProjetoEDA.service.BenchBST;
import dev.ProjetoEDA.service.BenchHeap;
import dev.ProjetoEDA.service.BenchLinkedList;
import dev.ProjetoEDA.service.BenchPV;
import dev.ProjetoEDA.service.BenchPriorityQueue;

/**
 * Controlador responsável por selecionar e iniciar o benchmark
 * correspondente à estrutura informada.
 *
 * A classe centraliza o mapeamento entre o identificador recebido
 * pela aplicação e a implementação concreta de {@link Bench}.
 */
public class BenchController {

    /**
     * Executa o benchmark da estrutura solicitada.
     *
     * Cria a implementação concreta correspondente, define o maior
     * tamanho de entrada do experimento e inicia o protocolo de benchmark.
     *
     * @param estrutura nome da estrutura a ser avaliada
     * @param tamanhoEntrada limite superior das escalas de teste
     */
    public void executar(String estrutura, int tamanhoEntrada) {
        Bench bench = criarBench(estrutura);
        bench.definirEntrada(tamanhoEntrada);
        bench.run();
    }

    /**
     * Cria a implementação de benchmark associada à estrutura informada.
     *
     * @param estrutura nome da estrutura solicitada
     * @return instância do benchmark correspondente
     * @throws IllegalArgumentException se a estrutura não for suportada
     */
    private Bench criarBench(String estrutura) {
        switch (estrutura.toLowerCase()) {
            case "arraylist":
                return new BenchArrayList();
            case "linkedlist":
                return new BenchLinkedList();
            case "heap":
                return new BenchHeap();
            case "priorityqueue":
                return new BenchPriorityQueue();
            case "avl":
                return new BenchAVL();
            case "bst":
                return new BenchBST();
            case "pv":
                return new BenchPV();
            default:
                throw new IllegalArgumentException("Benchmark desconhecido: " + estrutura);
        }
    }
}