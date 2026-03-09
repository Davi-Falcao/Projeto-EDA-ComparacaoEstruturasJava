import java.util.*;

public class Heap {

    private int[] heap;
    private int tail;

    public Heap(int capacidade){
        this.heap = new int[capacidae];
        this.tail = -1;
    }

    public boolean isEmpty(){
        return this.tail == -1.
    }

    public int left(int i){
        return 2 * i + 1;
    }

    public int right(int i){
        return 2 * i + 2;
    }

    public int parent(int i){
        return Math.minDiv(i-1, 2);
    }

    public void add(int element) {
        // sua implementação
        if(isEmpty()){
            
        }
        
    }

    private void buildHeap(){
        //TODO
    }

    public int remove() {
        // sua implementação
    }

    private void heapify(int index) {
        // sua implementação
    }

    private int maxIndex(int index, int left, int right){
        //TODO
    }

    private boolean isValidIndex(int index){
        //TODO
    }

    private boolean isLeaf(int index){
        //TODO
    }

    private void swap(int a, int b){
        int temp = this.heap[a];
        this.heap[a] = this.heap[b];
        this.heap[b] = temp;
    }

    private void resize(){
        //TODO
    }

    public int size(){
        return this.tail + 1;
    }

    public String toString(){
        return Arrays.toString(this.heap);
    }
}




