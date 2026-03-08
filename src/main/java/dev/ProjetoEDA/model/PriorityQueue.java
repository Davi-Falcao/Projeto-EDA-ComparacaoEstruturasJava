package dev.ProjetoEDA.model;

public class PriorityQueue implements Estrutura {

    private int[] fila;
    private int size;
    private int tail;

    public PriorityQueue(){
        this.fila = new int[20];
        this.size = 0;
        this.tail = -1;
    }

    public PriorityQueue(int capacity){
        this.fila = new int[capacity];
        this.size = 0;
        this.tail = -1;
    }

    public PriorityQueue(int[] fila){
        this.fila = fila;
        this.size = fila.length;
        this.tail = fila.length-1;
        mergeSort(fila, 0, this.tail);
    }

    public boolean isEmpty(){
        return this.tail == -1;
    }

    private boolean isFull(){
        return this.size == this.fila.length;
    }

    @Override
    public boolean add(int value){
        if(isFull()) resize(this.size *2);

        this.tail++;
        this.size ++;
        this.fila[this.tail] = value;

        int j = this.tail;

        for(int i = this.tail-1; i >= 0; i --) {
            if(this.fila[i] > value){
                swap(i, j);
                j = i;
            }else return true;
        }

        return true;
    }

    public int remove(){
        if(isEmpty()) throw new RuntimeException("Empty");

        int out = this.fila[this.tail];

        this.tail--;
        this.size--;

        return out;
    }

    @Override
    public boolean remove(int value){
        for (int i = 0; i <= this.tail; i++) {
            if (this.fila[i] == value) {
                for (int j = i; j < this.tail; j++) {
                    this.fila[j] = this.fila[j + 1];
                }
                this.tail--;
                this.size--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean search(int value) {
        for (int i = 0; i <= this.tail; i++) {
            if (this.fila[i] == value) {
                return true;
            }
        }
        return false;
    }

    private void resize(int newCapacipty){
        int[] newQuede = new int[newCapacipty];

        for(int i = 0; i < this.size; i++){
            newQuede[i] = this.fila[i];
        }

        this.fila = newQuede;
    }

    private void swap(int i, int j){
        int aux = this.fila[i];
        this.fila[i] = this.fila[j];
        this.fila[j] = aux;
    }

    public int size(){
        return this.size;
    }

    private void mergeSort(int[] v, int start, int end){
        if(start >= end) return;

        int middle = (start+end)/2;

        mergeSort(v, start, middle);
        mergeSort(v, middle+1, end);

        merge(v, start, end);
    }

    private void merge(int[] v, int start, int end) {
        int size = end - start;
        int[] aux = new int[size + 1];

        for (int i = 0; i <= size; i++) {
            aux[i] = v[start + i];
        }

        int middle = size / 2;

        int i = 0;
        int j = middle + 1;
        int k = start;

        while (j <= size && i <= middle) {
            if (aux[i] <= aux[j])  v[k] = aux[i++];
            else  v[k] = aux[j++];

            k++;
        }

        while (i <= middle) {
            v[k] = aux[i++];
            k++;
        }
    }
}