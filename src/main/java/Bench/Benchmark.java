package Bench;

import java.util.ArrayList;

import EstruturasBstAvlPv.AVL;
import EstruturasBstAvlPv.BST;
import EstruturasBstAvlPv.PV;
import EstruturasBstAvlPv.Estrutura;

public class Benchmark {

    public static void main(String[] args) {
        Benchmark bench = new Benchmark();
        bench.executar();
    }

    public void executar() {
        ArrayList<Estrutura> estruturas = new ArrayList<>();

        estruturas.add(new BST());
        estruturas.add(new AVL());
        estruturas.add(new PV());

        for (Estrutura e : estruturas) {
            executarBenchmark(e);
        }
    }

    private void executarBenchmark(Estrutura e) {
        System.out.println("Rodando: " + e.getClass().getSimpleName());
    }
}