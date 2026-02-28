import FilaPrioridadeHeap.PriorityQueue;

public class TestePriorityQueue {

    public static void main(String[] args) {

        testarInsercaoERemocaoSimples();
        testarConstrutorComArrayOrdena();
        testarRedimensionamento();
        testarFilaVazia();
        testarDuplicadosENegativos();

        System.out.println("ok");
    }

    private static void testarInsercaoERemocaoSimples() {
        PriorityQueue q = new PriorityQueue(10);
        q.add(10);
        q.add(30);
        q.add(20);
        q.add(5);

        // Sua fila é crescente, então o maior sai primeiro (tail)
        assert q.size() == 4;
        assert q.remove() == 30;
        assert q.remove() == 20;
        assert q.size() == 2;
        assert q.remove() == 10;
        assert q.remove() == 5;
        assert q.isEmpty();
    }

    private static void testarConstrutorComArrayOrdena() {
        int[] entrada = {5, 3, 17, 10, 84, 19, 6, 22, 9};
        PriorityQueue q = new PriorityQueue(entrada);

        // O maior deve sair primeiro
        assert q.remove() == 84;
        assert q.remove() == 22;
        assert q.remove() == 19;
    }

    private static void testarRedimensionamento() {
        PriorityQueue q = new PriorityQueue(2);
        q.add(1);
        q.add(2);
        q.add(3); // resize aqui
        q.add(4);

        assert q.size() == 4;
        assert q.remove() == 4;
        assert q.remove() == 3;
        assert q.remove() == 2;
        assert q.remove() == 1;
        assert q.isEmpty();
    }

    private static void testarFilaVazia() {
        PriorityQueue q = new PriorityQueue(5);
        assert q.isEmpty();

        try {
            q.remove();
            assert false : "Era para lançar exceção ao remover de fila vazia";
        } catch (RuntimeException e) {
            assert e.getMessage().equals("Empty");
        }
    }

    private static void testarDuplicadosENegativos() {
        PriorityQueue q = new PriorityQueue(3);
        q.add(-10);
        q.add(0);
        q.add(0);
        q.add(5); // resize deve acontecer

        assert q.remove() == 5;
        assert q.remove() == 0;
        assert q.remove() == 0;
        assert q.remove() == -10;
        assert q.isEmpty();
    }
}
