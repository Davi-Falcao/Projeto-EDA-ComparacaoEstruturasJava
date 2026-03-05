package EstruturasBstAvlPv;

public class PV implements Estrutura {

    private NodePv root;
    private final NodePv nil;
    private int size;

    public PV() {
        nil = new NodePv(0);
        nil.color = 0;
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;

        root = nil;
        size = 0;
    }

    private class NodePv {
        int value;
        NodePv parent;
        NodePv left;
        NodePv right;
        int color; // 0 = preto, 1 = vermelho

        public NodePv(int value) {
            this.value = value;
            this.parent = nil;
            this.left = nil;
            this.right = nil;
            this.color = 1; // novo nó começa vermelho
        }
    }

    private void leftRotate(NodePv x) {
        NodePv y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(NodePv x) {
        NodePv y = x.left;
        x.left = y.right;
        if (y.right != nil) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    private void fixInsert(NodePv k) {
        NodePv u;
        while (k.parent.color == 1) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) break;
        }
        root.color = 0;
    }

    // ======== Seu insert original (mantido) ========
    public void insert(int key) {
        NodePv node = new NodePv(key);
        NodePv y = nil;
        NodePv x = root;

        while (x != nil) {
            y = x;
            if (node.value < x.value) x = x.left;
            else x = x.right;
        }

        node.parent = y;
        if (y == nil) root = node;
        else if (node.value < y.value) y.left = node;
        else y.right = node;

        if (node.parent == nil) {
            node.color = 0;
            return;
        }

        if (node.parent.parent == nil) return;

        fixInsert(node);
    }

    public NodePv searchTree(int key) {
        return searchTreeHelper(this.root, key);
    }

    private NodePv searchTreeHelper(NodePv node, int key) {
        if (node == nil || key == node.value) return node;
        if (key < node.value) return searchTreeHelper(node.left, key);
        return searchTreeHelper(node.right, key);
    }

    // ======== Implementação da interface Estrutura ========

    @Override
    public void add(int x) {
        insert(x);
        size++;
    }

    @Override
    public void remove(int x) {
        // Ainda não implementado na sua PV (delete + fixDelete).
        // Para benchmark de 100% insertion, isso já serve.
        // Quando você implementar delete, substitui aqui.
    }

    @Override
    public boolean search(int x) {
        return searchTree(x) != nil;
    }

    @Override
    public int size() {
        return size;
    }
}