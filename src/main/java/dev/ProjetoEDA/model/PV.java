package dev.ProjetoEDA.model;

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
        int color;

        public NodePv(int value) {
            this.value = value;
            this.parent = nil;
            this.left = nil;
            this.right = nil;
            this.color = 1;
        }
    }

    private void leftRotate(NodePv x) {
        NodePv y = x.right;
        x.right = y.left;

        if (y.left != nil)
            y.left.parent = x;

        y.parent = x.parent;

        if (x.parent == nil)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;

        y.left = x;
        x.parent = y;
    }

    private void rightRotate(NodePv x) {
        NodePv y = x.left;
        x.left = y.right;

        if (y.right != nil)
            y.right.parent = x;

        y.parent = x.parent;

        if (x.parent == nil)
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;

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

            if (k == root)
                break;
        }

        root.color = 0;
    }

    public void insert(int key) {
        NodePv node = new NodePv(key);

        NodePv y = nil;
        NodePv x = root;

        while (x != nil) {
            y = x;

            if (node.value < x.value)
                x = x.left;
            else
                x = x.right;
        }

        node.parent = y;

        if (y == nil)
            root = node;
        else if (node.value < y.value)
            y.left = node;
        else
            y.right = node;

        if (node.parent == nil) {
            node.color = 0;
            size++;
            return;
        }

        if (node.parent.parent == nil) {
            size++;
            return;
        }

        fixInsert(node);
        size++;
    }

    public NodePv searchTree(int key) {
        return searchTreeHelper(root, key);
    }

    private NodePv searchTreeHelper(NodePv node, int key) {
        if (node == nil || key == node.value)
            return node;

        if (key < node.value)
            return searchTreeHelper(node.left, key);

        return searchTreeHelper(node.right, key);
    }

    @Override
    public boolean search(int key) {
        return searchTree(key) != nil;
    }

    @Override
    public boolean remove(int key) {
        NodePv z = searchTree(key);

        if (z == nil)
            return false;

        NodePv y = z;
        NodePv x;

        int yOriginalColor = y.color;

        if (z.left == nil) {
            x = z.right;
            transplant(z, z.right);

        } else if (z.right == nil) {
            x = z.left;
            transplant(z, z.left);

        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == z)
                x.parent = y;
            else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        if (yOriginalColor == 0)
            fixDelete(x);

        size--;
        return true;
    }

    private void fixDelete(NodePv x) {
        NodePv s;

        while (x != root && x.color == 0) {
            if (x == x.parent.left) {
                s = x.parent.right;

                if (s.color == 1) {
                    s.color = 0;
                    x.parent.color = 1;
                    leftRotate(x.parent);
                    s = x.parent.right;
                }

                if (s.left.color == 0 && s.right.color == 0) {
                    s.color = 1;
                    x = x.parent;

                } else {
                    if (s.right.color == 0) {
                        s.left.color = 0;
                        s.color = 1;
                        rightRotate(s);
                        s = x.parent.right;
                    }

                    s.color = x.parent.color;
                    x.parent.color = 0;
                    s.right.color = 0;
                    leftRotate(x.parent);
                    x = root;
                }

            } else {
                s = x.parent.left;

                if (s.color == 1) {
                    s.color = 0;
                    x.parent.color = 1;
                    rightRotate(x.parent);
                    s = x.parent.left;
                }

                if (s.left.color == 0 && s.right.color == 0) {
                    s.color = 1;
                    x = x.parent;

                } else {
                    if (s.left.color == 0) {
                        s.right.color = 0;
                        s.color = 1;
                        leftRotate(s);
                        s = x.parent.left;
                    }

                    s.color = x.parent.color;
                    x.parent.color = 0;
                    s.left.color = 0;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }

        x.color = 0;
    }

    private void transplant(NodePv u, NodePv v) {
        if (u.parent == nil)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;

        v.parent = u.parent;
    }

    private NodePv minimum(NodePv node) {
        while (node.left != nil)
            node = node.left;

        return node;
    }

    @Override
    public boolean add(int key) {
        insert(key);
        return true;
    }

    public int size() {
        return size;
    }
}