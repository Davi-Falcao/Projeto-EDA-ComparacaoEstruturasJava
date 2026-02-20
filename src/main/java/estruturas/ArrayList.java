package estruturas;

public class ArrayList {

    private int[] lista;
    public static final int CAPACIDADE_DEFAULT = 20;
    private int tamanho;

    public ArrayList() {
        this(CAPACIDADE_DEFAULT);
    }

    public ArrayList(int capacidade) {
        if (capacidade <= 0) capacidade = CAPACIDADE_DEFAULT;
        this.lista = new int[capacidade];
        this.tamanho = 0;
    }

    public boolean isEmpty() {
        return this.tamanho == 0;
    }

    public int size() {
        return this.tamanho;
    }

    public boolean add(int element) {
        assegureCapacidade(this.tamanho + 1);
        this.lista[tamanho++] = element;
        return true;
    }

    public void add(int index, int element) {
        if (index < 0 || index > this.tamanho)
            throw new IndexOutOfBoundsException();

        assegureCapacidade(this.tamanho + 1);
        shiftParaDireita(index);

        this.lista[index] = element;
        this.tamanho++;
    }

    public void set(int index, int element) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();
        this.lista[index] = element;
    }

    public int get(int index) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();
        return this.lista[index];
    }

    private void assegureCapacidade(int capacidadePretendida) {
        if (capacidadePretendida > this.lista.length)
            resize(Math.max(this.lista.length * 2, capacidadePretendida));
    }

    private void resize(int novaCapacidade) {
        int[] novaLista = new int[novaCapacidade];
        for (int i = 0; i < this.tamanho; i++)
            novaLista[i] = this.lista[i];
        this.lista = novaLista;
    }

    private void shiftParaDireita(int index) {
        for (int i = this.tamanho; i > index; i--) {
            this.lista[i] = this.lista[i - 1];
        }
    }

    private void shiftParaEsquerda(int index) {
        for (int i = index; i < this.tamanho - 1; i++) {
            this.lista[i] = this.lista[i + 1];
        }
    }

    public int indexOf(int element) {
        for (int i = 0; i < tamanho; i++)
            if (this.lista[i] == element)
                return i;
        return -1;
    }

    public boolean contains(int element) {
        return indexOf(element) != -1;
    }


    public int remove(int index) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();

        int element = this.lista[index];
        shiftParaEsquerda(index);
        this.tamanho--;
        return element;
    }


    public boolean remove(Integer element) {
        if (element == null) return false;

        for (int i = 0; i < tamanho; i++) {
            if (this.lista[i] == element.intValue()) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public String toString() {
        if (isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < this.tamanho; i++) {
            sb.append(this.lista[i]);
            if (i < this.tamanho - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
