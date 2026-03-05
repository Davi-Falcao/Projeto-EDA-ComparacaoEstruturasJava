package dev.ProjetoEDA.;

import dev.ProjetoEDA.model.Heap;

public class TesteHeap {
    public static void main(String[] args) {

        testarInsercaoERemocaoSimples();
        testarBuildHeap();
        testarRedimensionamento();
        testarHeapVazia();

        System.out.println("ok");
    }

    private static void testarInsercaoERemocaoSimples() {
        Heap h = new Heap(10);
        h.add(10);
        h.add(30);
        h.add(20);
        h.add(5);

        // Em uma MaxHeap, o maior (30) deve sair primeiro
        assert h.size() == 4;
        assert h.remove() == 30;
        assert h.remove() == 20;
        assert h.size() == 2;
    }

    private static void testarBuildHeap() {
        // Testando o construtor que recebe um array pronto
        int[] entrada = {5, 3, 17, 10, 84, 19, 6, 22, 9};
        Heap h = new Heap(entrada);

        // O buildHeap deve ter colocado o 84 no topo
        assert h.remove() == 84;
        assert h.remove() == 22;
    }

    private static void testarRedimensionamento() {
        // Criando heap com capacidade 2 para o resize()
        Heap h = new Heap(2);
        h.add(1);
        h.add(2);
        h.add(3); // Aqui deve ocorrer o resize
        h.add(4);

        assert h.size() == 4;
        assert h.remove() == 4;
    }

    private static void testarHeapVazia() {
        Heap h = new Heap(5);
        assert h.isEmpty();
        
        try {
            h.remove();
            assert false;

        } catch (RuntimeException e) {
            assert e.getMessage().equals("Empty");
        }
    }
}
