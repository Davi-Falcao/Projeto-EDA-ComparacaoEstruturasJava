package dev.ProjetoEDA.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class AVL implements Estrutura {

    private Node root;
    private int size;

    public AVL() {
        this.root = null;
        this.size = 0;
    }

    public boolean isAVL() {
        return isAVL(this.root);
    }

    private boolean isAVL(Node node) {
        if (node == null) {
            return true;
        }

        int fator = Math.abs(balance(node));
        if (fator >= 2) {
            return false;
        }

        return isAVL(node.left) && isAVL(node.right);
    }

    public int height() {
        return height(this.root);
    }

    private int height(Node node) {
        return node == null ? -1 : node.height;
    }

    private void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private int balance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private void rebalance(Node node) {
        updateHeight(node);

        int balance = balance(node);

        if (balance >= 2) {
            if (balance(node.left) < 0) {
                rotacaoEsquerda(node.left);
            }
            rotacaoDireita(node);
        } else if (balance <= -2) {
            if (balance(node.right) > 0) {
                rotacaoDireita(node.right);
            }
            rotacaoEsquerda(node);
        }
    }

    private void rebalanceUp(Node node) {
        Node current = node;

        while (current != null) {
            rebalance(current);
            current = current.parent;
        }
    }

    private void rotacaoDireita(Node n) {
        Node x = n;
        Node y = x.left;

        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.right = x;
        x.parent = y;

        updateHeight(x);
        updateHeight(y);
    }

    private void rotacaoEsquerda(Node n) {
        Node x = n;
        Node y = x.right;

        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;

        updateHeight(x);
        updateHeight(y);
    }

    @Override
    public boolean search(int element) {
        return searchNode(element) != null;
    }

    public Node searchNode(int element) {
        Node aux = this.root;

        while (aux != null) {
            if (element == aux.value) {
                return aux;
            }

            if (element < aux.value) {
                aux = aux.left;
            } else {
                aux = aux.right;
            }
        }

        return null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public boolean add(int element) {
        if (isEmpty()) {
            this.root = new Node(element);
            size++;
            return true;
        }

        Node aux = this.root;

        while (true) {
            if (element < aux.value) {
                if (aux.left == null) {
                    Node newNode = new Node(element);
                    aux.left = newNode;
                    newNode.parent = aux;
                    size++;
                    rebalanceUp(aux);
                    return true;
                }

                aux = aux.left;
            } else {
                if (aux.right == null) {
                    Node newNode = new Node(element);
                    aux.right = newNode;
                    newNode.parent = aux;
                    size++;
                    rebalanceUp(aux);
                    return true;
                }

                aux = aux.right;
            }
        }
    }

    @Override
    public boolean remove(int value) {
        Node toRemove = searchNode(value);

        if (toRemove == null) {
            return false;
        }

        removeNode(toRemove);
        size--;
        return true;
    }

    private void removeNode(Node node) {
        if (node.left != null && node.right != null) {
            Node sucessor = sucessor(node);
            node.value = sucessor.value;
            removeNode(sucessor);
            return;
        }

        Node child = (node.left != null) ? node.left : node.right;
        Node parent = node.parent;

        if (child != null) {
            child.parent = parent;
        }

        if (parent == null) {
            root = child;
        } else if (node == parent.left) {
            parent.left = child;
        } else {
            parent.right = child;
        }

        if (parent != null) {
            rebalanceUp(parent);
        } else if (root != null) {
            updateHeight(root);
        }
    }

    public Node min() {
        return min(root);
    }

    public Node max() {
        return max(root);
    }

    public Node predecessor(Node node) {
        if (node == null) {
            return null;
        }

        if (node.left != null) {
            return max(node.left);
        }

        Node parent = node.parent;

        while (parent != null && node == parent.left) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    public Node sucessor(Node node) {
        if (node == null) {
            return null;
        }

        if (node.right != null) {
            return min(node.right);
        }

        Node parent = node.parent;

        while (parent != null && node == parent.right) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    public ArrayList<Integer> bfs() {
        ArrayList<Integer> list = new ArrayList<>();

        if (root == null) {
            return list;
        }

        Deque<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.remove();
            list.add(current.value);

            if (current.left != null) {
                queue.add(current.left);
            }

            if (current.right != null) {
                queue.add(current.right);
            }
        }

        return list;
    }

    private Node min(Node node) {
        if (node == null) {
            return null;
        }

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    private Node max(Node node) {
        if (node == null) {
            return null;
        }

        while (node.right != null) {
            node = node.right;
        }

        return node;
    }

    public int size() {
        return size;
    }

    class Node {
        int value;
        int height;
        Node left;
        Node right;
        Node parent;

        Node(int value) {
            this.value = value;
            this.height = 0;
        }

        boolean hasOnlyLeftChild() {
            return left != null && right == null;
        }

        boolean hasOnlyRightChild() {
            return left == null && right != null;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }
}