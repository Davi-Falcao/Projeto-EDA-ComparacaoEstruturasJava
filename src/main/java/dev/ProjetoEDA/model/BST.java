package dev.ProjetoEDA.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class BST implements Estrutura {

    private Node root;
    private int size;

    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public boolean add(int element) {
        this.size += 1;

        if (isEmpty()) {
            this.root = new Node(element);
            return true;
        }

        Node aux = this.root;

        while (aux != null) {
            if (element < aux.value) {
                if (aux.left == null) {
                    Node newNode = new Node(element);
                    aux.left = newNode;
                    newNode.parent = aux;
                    return true;
                }
                aux = aux.left;
            } else {
                if (aux.right == null) {
                    Node newNode = new Node(element);
                    aux.right = newNode;
                    newNode.parent = aux;
                    return true;
                }
                aux = aux.right;
            }
        }

        return true;
    }

    public Node min() {
        if (isEmpty()) return null;
        return min(this.root);
    }

    private Node min(Node node) {
        if (node.left == null) return node;
        else return min(node.left);
    }

    public Node max() {
        if (isEmpty()) return null;

        Node node = this.root;
        while (node.right != null)
            node = node.right;

        return node;
    }

    private Node max(Node node) {
        if (node.right == null) return node;
        else return max(node.right);
    }

    public Node predecessor(Node node) {
        if (node == null) return null;

        if (node.left != null)
            return max(node.left);
        else {
            Node aux = node.parent;

            while (aux != null && aux.value > node.value)
                aux = aux.parent;

            return aux;
        }
    }

    public Node sucessor(Node node) {
        if (node == null) return null;

        if (node.right != null)
            return min(node.right);
        else {
            Node aux = node.parent;

            while (aux != null && aux.value < node.value)
                aux = aux.parent;

            return aux;
        }
    }

    public void recursiveAdd(int element) {
        if (isEmpty())
            this.root = new Node(element);
        else
            recursiveAdd(this.root, element);

        this.size += 1;
    }

    private void recursiveAdd(Node node, int element) {
        if (element < node.value) {
            if (node.left == null) {
                Node newNode = new Node(element);
                node.left = newNode;
                newNode.parent = node;
                return;
            }
            recursiveAdd(node.left, element);
        } else {
            if (node.right == null) {
                Node newNode = new Node(element);
                node.right = newNode;
                newNode.parent = node;
                return;
            }
            recursiveAdd(node.right, element);
        }
    }

    @Override
    public boolean remove(int value) {
        Node toRemove = searchNode(value);
        if (toRemove != null) {
            removeNode(toRemove);
            this.size -= 1;
            return true;
        }
        return false;
    }

    private void removeNode(Node toRemove) {
        if (toRemove.isLeaf()) {
            if (toRemove == this.root)
                this.root = null;
            else {
                if (toRemove.value < toRemove.parent.value)
                    toRemove.parent.left = null;
                else
                    toRemove.parent.right = null;
            }

        } else if (toRemove.hasOnlyLeftChild()) {
            if (toRemove == this.root) {
                this.root = toRemove.left;
                this.root.parent = null;
            } else {
                toRemove.left.parent = toRemove.parent;
                if (toRemove.value < toRemove.parent.value)
                    toRemove.parent.left = toRemove.left;
                else
                    toRemove.parent.right = toRemove.left;
            }

        } else if (toRemove.hasOnlyRightChild()) {
            if (toRemove == this.root) {
                this.root = toRemove.right;
                this.root.parent = null;
            } else {
                toRemove.right.parent = toRemove.parent;
                if (toRemove.value < toRemove.parent.value)
                    toRemove.parent.left = toRemove.right;
                else
                    toRemove.parent.right = toRemove.right;
            }

        } else {
            Node sucessor = sucessor(toRemove);
            toRemove.value = sucessor.value;
            removeNode(sucessor);
        }
    }

    @Override
    public boolean search(int element) {
        return searchNode(element) != null;
    }

    public Node searchNode(int element) {
        Node aux = this.root;

        while (aux != null) {
            if (element == aux.value) return aux;
            aux = (element < aux.value) ? aux.left : aux.right;
        }

        return null;
    }

    public Node recursiveSearch(int element) {
        return recursiveSearch(this.root, element);
    }

    private Node recursiveSearch(Node node, int element) {
        if (node == null) return null;
        if (element == node.value) return node;
        if (element < node.value) return recursiveSearch(node.left, element);
        else return recursiveSearch(node.right, element);
    }

    public int height() {
        return height(this.root);
    }

    private int height(Node node) {
        if (node == null) return -1;
        else return 1 + Math.max(height(node.left), height(node.right));
    }

    public void preOrder() {
        preOrder(this.root);
    }

    private void preOrder(Node node) {
        if (node != null) {
            System.out.println(node.value);
            preOrder(node.left);
            preOrder(node.right);
        }
    }

    public void inOrder() {
        inOrder(this.root);
    }

    private void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println(node.value);
            inOrder(node.right);
        }
    }

    public void posOrder() {
        posOrder(this.root);
    }

    private void posOrder(Node node) {
        if (node != null) {
            posOrder(node.left);
            posOrder(node.right);
            System.out.println(node.value);
        }
    }

    public ArrayList<Integer> bfs() {
        ArrayList<Integer> list = new ArrayList<>();
        Deque<Node> queue = new LinkedList<>();

        if (!isEmpty()) {
            queue.addLast(this.root);
            while (!queue.isEmpty()) {
                Node current = queue.removeFirst();
                list.add(current.value);

                if (current.left != null)
                    queue.addLast(current.left);
                if (current.right != null)
                    queue.addLast(current.right);
            }
        }
        return list;
    }

    public int size() {
        return this.size;
    }

    static class Node {
        int value;
        Node left;
        Node right;
        Node parent;

        Node(int v) {
            this.value = v;
        }

        public boolean hasOnlyLeftChild() {
            return (this.left != null && this.right == null);
        }

        public boolean hasOnlyRightChild() {
            return (this.left == null && this.right != null);
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }
    }
}