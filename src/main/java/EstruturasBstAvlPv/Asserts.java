package EstruturasBstAvlPv;

public class Asserts{
public static void main(String[] args){

    testarAVL();
    testarBST();
    testarPV();

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

        assert avl.search(10) != null;
        assert avl.search(5) != null;
        assert avl.search(15) != null;
        assert avl.search(999) == null;

        avl.remove(3);

        assert avl.size() == 3;
        assert avl.search(3) == null;
        assert avl.isAVL();
    }

    private static void testarBST() {

        BST bst = new BST();

        assert bst.isEmpty();

        bst.add(10);
        bst.add(5);
        bst.add(15);

        assert !bst.isEmpty();
        assert bst.size() == 3;

        assert bst.search(10) != null;
        assert bst.search(999) == null;

        bst.remove(5);

        assert bst.size() == 2;
        assert bst.search(5) == null;
    }

    private static void testarPV() {

        PV pv = new PV();

        pv.insert(10);
        pv.insert(5);
        pv.insert(15);
        pv.insert(20);

        assert pv.searchTree(10) != null;
        assert pv.searchTree(5) != null;
        assert pv.searchTree(15) != null;

        // busca valor inexistente
        assert pv.searchTree(999) != null;
        }
    }
