package dev.ProjetoEDA.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class AVL {

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
        if (node == null) return true;
        int blc = Math.abs(balance(node));
        if (blc >= 2) return false;
        return isAVL(node.left) && isAVL(node.right);
    }

    public int height() {
        return height(this.root);
    }

    private int height(Node node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private int balance(Node node) {
        return height(node.left) - height(node.right);
    }

    private void rotate(Node des) {

        if (balance(des) >= 2) {

            if (balance(des.left) >= 0) {
                rotacaoDireita(des);
            } else {
                rotacaoEsquerda(des.left);
                rotacaoDireita(des);
            }

        } else if (balance(des) <= -2) {

            if (balance(des.right) <= 0) {
                rotacaoEsquerda(des);
            } else {
                rotacaoDireita(des.right);
                rotacaoEsquerda(des);
            }
        }
    }

    private void rotacaoDireita(Node n) {

        Node x = n;
        Node y = x.left;

        x.left = y.right;

        if (y.right != null)
            y.right.parent = x;

        y.parent = x.parent;

        if (x.parent == null)
            this.root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;

        y.right = x;
        x.parent = y;
    }

    private void rotacaoEsquerda(Node n) {

        Node x = n;
        Node y = x.right;

        x.right = y.left;

        if (y.left != null)
            y.left.parent = x;

        y.parent = x.parent;

        if (x.parent == null)
            this.root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;

        y.left = x;
        x.parent = y;
    }

    // ======= SEARCH para interface (boolean) =======

    public boolean search(int element) {
        return searchNode(element) != null;
    }

    // seu search antigo (Node) renomeado
    public Node searchNode(int element) {

        Node aux = this.root;

        while (aux != null) {

            if (element == aux.value)
                return aux;

            if (element < aux.value)
                aux = aux.left;
            else
                aux = aux.right;
        }

        return null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void add(int element) {

        if (isEmpty()) {
            this.root = new Node(element);
            size++;
            return;
        }

        Node aux = this.root;

        while (true) {

            if (element < aux.value) {

                if (aux.left == null) {

                    Node newNode = new Node(element);
                    aux.left = newNode;
                    newNode.parent = aux;

                    size++;

                    rebalanceUp(newNode);

                    return;
                }

                aux = aux.left;

            } else {

                if (aux.right == null) {

                    Node newNode = new Node(element);
                    aux.right = newNode;
                    newNode.parent = aux;

                    size++;

                    rebalanceUp(newNode);

                    return;
                }

                aux = aux.right;
            }
        }
    }

    private void rebalanceUp(Node node) {

        Node current = node;

        while (current != null) {

            rotate(current);

            current = current.parent;
        }
    }

    public void remove(int value) {

        Node toRemove = searchNode(value);

        if (toRemove == null)
            return;

        Node parent = toRemove.parent;

        removeNode(toRemove);

        size--;

        rebalanceUp(parent);
    }

    private void removeNode(Node node) {

        if (node.isLeaf()) {

            if (node == root)
                root = null;
            else if (node == node.parent.left)
                node.parent.left = null;
            else
                node.parent.right = null;

        } else if (node.hasOnlyLeftChild()) {

            Node child = node.left;

            if (node == root) {
                root = child;
                child.parent = null;
            } else {

                if (node == node.parent.left)
                    node.parent.left = child;
                else
                    node.parent.right = child;

                child.parent = node.parent;
            }

        } else if (node.hasOnlyRightChild()) {

            Node child = node.right;

            if (node == root) {
                root = child;
                child.parent = null;
            } else {

                if (node == node.parent.left)
                    node.parent.left = child;
                else
                    node.parent.right = child;

                child.parent = node.parent;
            }

        } else {

            Node sucessor = sucessor(node);

            node.value = sucessor.value;

            removeNode(sucessor);
        }
    }

    public Node min() {
        Node node = root;
        while (node.left != null)
            node = node.left;
        return node;
    }

    public Node max() {
        Node node = root;
        while (node.right != null)
            node = node.right;
        return node;
    }

    public Node predecessor(Node node) {

        if (node.left != null)
            return max(node.left);

        Node parent = node.parent;

        while (parent != null && node == parent.left) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    public Node sucessor(Node node) {

        if (node.right != null)
            return min(node.right);

        Node parent = node.parent;

        while (parent != null && node == parent.right) {
            node = parent;
            parent = parent.parent;
        }

        return parent;
    }

    public ArrayList<Integer> bfs() {

        ArrayList<Integer> list = new ArrayList<>();

        if (root == null)
            return list;

        Deque<Node> queue = new LinkedList<>();

        queue.add(root);

        while (!queue.isEmpty()) {

            Node current = queue.remove();

            list.add(current.value);

            if (current.left != null)
                queue.add(current.left);

            if (current.right != null)
                queue.add(current.right);
        }

        return list;
    }

    private Node min(Node node) {
        if (node == null) return null;
        while (node.left != null) node = node.left;
        return node;
    }

    private Node max(Node node) {
        if (node == null) return null;
        while (node.right != null) node = node.right;
        return node;
    }

    public int size() {
        return size;
    }

    class Node {

        int value;
        Node left;
        Node right;
        Node parent;

        Node(int value) {
            this.value = value;
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
