package EstruturasBstAvlPv;

public class Asserts {

    public static void main(String[] args) {
        testarAVL();
        testarBST();
        testarPV();
        System.out.println("OK - todos os asserts passaram.");
    }

    private static void testarAVL() {
        AVL avl = new AVL();

        assert avl.isEmpty();
        assert avl.size() == 0;

        avl.add(10);
        avl.add(5);
        avl.add(15);
        avl.add(3);

        assert !avl.isEmpty();
        assert avl.size() == 4;
        assert avl.isAVL();

        // search agora é boolean (pela interface Estrutura)
        assert avl.search(10);
        assert avl.search(5);
        assert avl.search(15);
        assert !avl.search(999);

        avl.remove(3);

        assert avl.size() == 3;
        assert !avl.search(3);
        assert avl.isAVL();
    }

    private static void testarBST() {
        BST bst = new BST();

        assert bst.isEmpty();
        assert bst.size() == 0;

        bst.add(10);
        bst.add(5);
        bst.add(15);

        assert !bst.isEmpty();
        assert bst.size() == 3;

        // search agora é boolean
        assert bst.search(10);
        assert !bst.search(999);

        bst.remove(5);

        assert bst.size() == 2;
        assert !bst.search(5);
    }

    private static void testarPV() {
        PV pv = new PV();

        // Se você adaptou PV para Estrutura, use add()
        // Se ainda está no insert(), troque pv.add(...) por pv.insert(...)
        pv.add(10);
        pv.add(5);
        pv.add(15);
        pv.add(20);

        assert pv.search(10);
        assert pv.search(5);
        assert pv.search(15);

        // busca valor inexistente: tem que ser FALSE
        assert !pv.search(999);
    }
}