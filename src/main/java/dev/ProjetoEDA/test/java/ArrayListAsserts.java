package dev.ProjetoEDA.test;

import dev.ProjetoEDA.model.ArrayList;

public class ArrayListAsserts {

    public static void main(String[] args) {
        testInicializacao();
        testAddESizeEEmpty();
        testResizeAoAdicionar();
        testAddPorIndice();
        testGetSet();
        testIndexOfContains();
        testRemovePorIndice();
        testRemovePorElemento();
        testToString();
        testExcecoesIndexOutOfBounds();
        System.out.println("OK");
    }

    private static void assertEqualsInt(int expected, int actual) {
        assert expected == actual;
    }

    private static void assertEqualsBool(boolean expected, boolean actual) {
        assert expected == actual;
    }

    private static void assertEqualsStr(String expected, String actual) {
        assert expected.equals(actual);
    }

    private static void assertThrows(Runnable r) {
        boolean thrown = false;
        try {
            r.run();
        } catch (IndexOutOfBoundsException e) {
            thrown = true;
        }
        assert thrown;
    }

    private static void assertListContent(ArrayList list, int[] expected) {
        assertEqualsInt(expected.length, list.size());
        for (int i = 0; i < expected.length; i++) {
            assertEqualsInt(expected[i], list.get(i));
        }
    }

    private static void testInicializacao() {
        ArrayList list = new ArrayList();
        assertEqualsBool(true, list.isEmpty());
        assertEqualsInt(0, list.size());
        assertEqualsStr("[]", list.toString());
    }

    private static void testAddESizeEEmpty() {
        ArrayList list = new ArrayList();
        assertEqualsBool(true, list.add(10));
        assertEqualsBool(false, list.isEmpty());
        assertEqualsInt(1, list.size());
        assertEqualsInt(10, list.get(0));
    }

    private static void testResizeAoAdicionar() {
        ArrayList list = new ArrayList(2);
        list.add(1);
        list.add(2);
        list.add(3);
        assertListContent(list, new int[]{1,2,3});
    }

    private static void testAddPorIndice() {
        ArrayList list = new ArrayList();
        list.add(10);
        list.add(30);
        list.add(1,20);
        assertListContent(list, new int[]{10,20,30});
        list.add(0,5);
        assertListContent(list, new int[]{5,10,20,30});
        list.add(list.size(),40);
        assertListContent(list, new int[]{5,10,20,30,40});
    }

    private static void testGetSet() {
        ArrayList list = new ArrayList();
        list.add(10);
        list.add(20);
        list.add(30);
        assertEqualsInt(20, list.get(1));
        list.set(1,99);
        assertEqualsInt(99, list.get(1));
        assertListContent(list, new int[]{10,99,30});
    }

    private static void testIndexOfContains() {
        ArrayList list = new ArrayList();
        list.add(7);
        list.add(8);
        list.add(9);
        assertEqualsInt(0, list.indexOf(7));
        assertEqualsInt(1, list.indexOf(8));
        assertEqualsInt(2, list.indexOf(9));
        assertEqualsInt(-1, list.indexOf(100));
        assertEqualsBool(true, list.contains(8));
        assertEqualsBool(false, list.contains(100));
    }

    private static void testRemovePorIndice() {
        ArrayList list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        int removed = list.remove(1);
        assertEqualsInt(2, removed);
        assertListContent(list, new int[]{1,3,4});
        removed = list.remove(0);
        assertEqualsInt(1, removed);
        assertListContent(list, new int[]{3,4});
        removed = list.remove(list.size()-1);
        assertEqualsInt(4, removed);
        assertListContent(list, new int[]{3});
    }

    private static void testRemovePorElemento() {
        ArrayList list = new ArrayList();
        list.add(10);
        list.add(20);
        list.add(30);
        assertEqualsBool(true, list.remove(Integer.valueOf(20)));
        assertListContent(list, new int[]{10,30});
        assertEqualsBool(false, list.remove(Integer.valueOf(999)));
        assertEqualsBool(false, list.remove((Integer)null));
    }

    private static void testToString() {
        ArrayList list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEqualsStr("[1, 2, 3]", list.toString());
    }

    private static void testExcecoesIndexOutOfBounds() {
        ArrayList list = new ArrayList();
        assertThrows(() -> list.get(0));
        assertThrows(() -> list.set(0,1));
        assertThrows(() -> list.remove(0));
        list.add(10);
        assertThrows(() -> list.get(-1));
        assertThrows(() -> list.get(1));
        assertThrows(() -> list.set(1,5));
        assertThrows(() -> list.remove(1));
        assertThrows(() -> list.add(2,50));
    }
}

