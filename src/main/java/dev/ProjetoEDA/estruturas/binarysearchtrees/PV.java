package dev.ProjetoEDA.estruturas.binarysearchtrees;


public class PV {

    private NodePv root;
    private NodePv nil;

    public PV() {
        nil = new NodePv(0);
        nil.color = 0; 
        nil.left = nil;
        nil.right = nil;
        root = nil;
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
            if (k == root) {
                break;
            }
        }
        root.color = 0;
    }

    public void insert(int key) {
        NodePv node = new NodePv(key);
        NodePv y = nil;
        NodePv x = root;

        while (x != nil) {
            y = x;
            if (node.value < x.value) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y == nil) {
            root = node;
        } else if (node.value < y.value) {
            y.left = node;
        } else {
            y.right = node;
        }

        if (node.parent == nil) {
            node.color = 0;
            return;
        }

        if (node.parent.parent == nil) {
            return;
        }

        fixInsert(node);
    }

    public NodePv searchTree(int key) {
        return searchTreeHelper(this.root, key);
    }

    private NodePv searchTreeHelper(NodePv node, int key) {
        if (node == nil || key == node.value) {
            return node;
        }

        if (key < node.value) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }
}