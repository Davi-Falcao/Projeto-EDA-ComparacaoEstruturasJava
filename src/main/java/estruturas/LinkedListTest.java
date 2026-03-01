package LinkedList.estruturas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

class LinkedListTest {

    private LinkedList lista;

    @BeforeEach
    void setup() {
        lista = new LinkedList();
    }

    @Test
    void testeInsercaoEBusca() {
        assertTrue(lista.isEmpty());

        lista.addFirst(10);
        lista.addLast(20);
        lista.add(1, 15); // Lista: 10, 15, 20

        assertEquals(3, lista.size());
        assertEquals(10, lista.getFirst());
        assertEquals(20, lista.getLast());
        assertEquals(15, lista.get(1));
    }

    @Test
    void testeRemocaoPorValor() {
        lista.addLast(10);
        lista.addLast(20);
        lista.addLast(30);

        assertTrue(lista.removeByvalue(20));
        assertFalse(lista.contains(20));
        assertEquals(2, lista.size());
        assertEquals("10, 30", lista.toString());
    }

    @Test
    void testeRemocaoPosicional() {
        lista.addLast(5);
        lista.addLast(10);

        int removido = lista.removeFirst();
        assertEquals(5, removido);
        assertEquals(1, lista.size());
        assertEquals(10, lista.getFirst());
    }

    @Test
    void testeIndices() {
        lista.addLast(10);
        lista.addLast(20);
        lista.addLast(10);

        assertEquals(0, lista.indexOf(10));
        assertEquals(2, lista.lastIndexOf(10));
        assertEquals(-1, lista.indexOf(99));
    }

    @Test
    void testeExcecoes() {
        // Testa se lança erro ao pegar algo de lista vazia
        assertThrows(NoSuchElementException.class, () -> {
            lista.getFirst();
        });

        // Testa erro de índice fora dos limites
        assertThrows(IndexOutOfBoundsException.class, () -> {
            lista.get(10);
        });
    }
}