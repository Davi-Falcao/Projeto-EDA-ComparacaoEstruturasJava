package dev.ProjetoEDA.controller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import dev.ProjetoEDA.model.LinkeList;


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
                Estruturas benchArrayList = new benchArrayList();
                benchArrayList.run();
                break;
            case "linkedlist":
                Estruturas benchLinkedList = new benchLinkedList();
                benchLinkedList.run();
                break;
            case "heap":
                Estruturas benchHeap = new benchHeap();
                benchHeap.run();
                break;
            case "filaPrioridade":
                Estruturas benchFilaPrioridade = new benchFilaPrioridade();
                benchFilaPrioridade.run();
                break;
            case "avl":
                Estruturas benchAVL = new benchAVL();
                benchAVL.run();
                break;
            case "bst":
                Estruturas benchBST = new benchBST();
                benchBST.run();
                break;
            case "pv":
                Estruturas benchPV = new benchPV();
                benchPV.run();
                break;
        default:
            System.err.println("Benchmark desconhecido: " + caso);
        }
    }
}