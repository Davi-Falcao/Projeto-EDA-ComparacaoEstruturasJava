package dev.ProjetoEDA.controller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import dev.ProjetoEDA.model.ArrayList;


public class BenchController {
    
    /**
     * Método principal que executa o benchmark das operações em um ArrayList.
     * Ele lê um arquivo CSV contendo uma sequência de operações (inserção, remoção e busca),
     * executa cada operação no ArrayList, registra os tempos de execução e uso de memória,
     * e grava os resultados em um arquivo CSV de saída.
     * 
     * @param args Argumentos passados pela linha de comando, que incluem o nome do arquivo de entrada e saída.
     * @throws IOException Caso ocorra algum erro na leitura ou escrita de arquivos.
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