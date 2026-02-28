package FilaPrioridadeHeap;

/**
 * Implementação de uma Fila de Prioridade utilizando um Array.
 * A fila mantém os elementos ordenados de forma crescente para garantir 
 * que a remoção do elemento de maior prioridade seja feita em tempo constante O(1).
 *  
 */
public class PriorityQueue {

    private int[] fila;
    private int size;
    private int tail;

    /**
     * Cria uma fila de prioridade com a capacidade padrão de 20 elementos.
     */
    public PriorityQueue(){
        this.fila = new int[20];
        this.size = 0;
        this.tail = -1;
    }

    /**
     * Cria uma fila de prioridade com uma capacidade específica.
     * * @param capacity A capacidade inicial do array.
     */
    public PriorityQueue(int capacity){
        this.fila = new int[capacity];
        this.size = 0;
        this.tail = -1;
    }

    /**
     * Cria uma fila de prioridade a partir de um array recebido, usando mergeSort para ordenar.
     * * @param fila array com os elementos.
     */
    public PriorityQueue(int[] fila){
        this.fila = fila;
        this.size = fila.length;
        this.tail = fila.length-1;

        mergeSort(fila, 0, this.tail);

    }

    /**
     * Verifica se a fila está vazia.
     * * @return true se a fila não contiver elementos, false caso contrário.
     */
    private boolean isEmpyt(){
        return this.tail == -1;
    }

    /**
     * Verifica se a fila atingiu sua capacidade máxima atual.
     * * @return true se a fila estiver cheia, false caso contrário.
     */
    private boolean isFull(){
        return this.size == this.fila.length;
    }

    /**
     * Adiciona um novo valor à fila. O valor é inserido de forma ordenada
     * utilizando a lógica de Insertion Sort, movendo elementos menores para a esquerda.
     * * @param value o inteiro a ser adicionado à fila.
     */
    public void add(int value){
        if(isFull()) resize(this.size *2);

        this.tail++;
        this.size ++;
        this.fila[this.tail] = value;

        int j = this.tail;

        for(int i = this.tail-1; i >= 0; i --) {
            if(this.fila[i] > value){
                swap(i, j);
                j = i;

            }else return;
        }

    }

    /**
     * Remove e retorna o elemento de maior prioridade da fila.
     * Como a fila é mantida ordenada, este elemento reside no final do array.
     * * @return O valor do elemento removido.
     * @throws RuntimeException Se a fila estiver vazia.
     */
    public int remove(){
        if(isEmpyt()) throw new RuntimeException();

        int out = this.fila[this.tail];

        this.tail--;
        this.size--;

        return out;


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
