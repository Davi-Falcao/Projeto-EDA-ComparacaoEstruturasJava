package dev.ProjetoEDA.controller;
import dev.ProjetoEDA.service.bench.Bench;
import dev.ProjetoEDA.service.bench.BenchAVL;
import dev.ProjetoEDA.service.bench.BenchArrayList;
import dev.ProjetoEDA.service.bench.BenchBST;
import dev.ProjetoEDA.service.bench.BenchHeap;
import dev.ProjetoEDA.service.bench.BenchLinkedList;
import dev.ProjetoEDA.service.bench.BenchPV;
import dev.ProjetoEDA.service.bench.BenchPriorityQueue;
;

public class BenchController {
    
    /**
     * Método principal para escolher e executar o benchmark com base no caso fornecido,
     * e chamar o método específico para cada estrutura de dados.
     * 
     * @param args Argumentos passados pela linha de comando, nesse caso é o nome do benchmark.
     */
   
    public static void escolherOCaso(String caso) {
        switch (caso) {
            case "arraylist":
                Bench benchArrayList = new BenchArrayList();
                benchArrayList.run();
                break;
            case "linkedlist":
                 Bench benchLinkedList = new BenchLinkedList();
                 benchLinkedList.run();
                 break;
             case "heap":
                 Bench benchHeap = new BenchHeap();
                 benchHeap.run();
                 break;
             case "filaPrioridade":
                 Bench benchFilaPrioridade = new BenchPriorityQueue();
                 benchFilaPrioridade.run();
                 break;
             case "avl":
                 Bench benchAVL = new BenchAVL();
                 benchAVL.run();
                 break;
             case "bst":
                 Bench benchBST = new BenchBST();
                 benchBST.run();
                 break;
             case "pv":
                 Bench benchPV = new BenchPV();
                 benchPV.run();
                 break;
        default:
            System.err.println("Benchmark desconhecido: " + caso);
        }
    }
}