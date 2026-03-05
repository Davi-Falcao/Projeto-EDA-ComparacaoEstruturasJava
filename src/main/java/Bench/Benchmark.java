package Bench;

import java.util.ArrayList;

import EstruturasBstAvlPv.AVL;
import EstruturasBstAvlPv.BST;
import EstruturasBstAvlPv.PV;
import EstruturasBstAvlPv.Estrutura;

public class Benchmark {

    private ArrayList<Estrutura> resetaEstruturas() {

    ArrayList<Estrutura> estruturas = new ArrayList<>();

    estruturas.add(new BST());
    estruturas.add(new AVL());
    estruturas.add(new PV());

    return estruturas;
    
    }
    
    
    private void executarBenchmark(Estrutura e) {
        System.out.println("Rodando: " + e.getClass().getSimpleName());
    }

}