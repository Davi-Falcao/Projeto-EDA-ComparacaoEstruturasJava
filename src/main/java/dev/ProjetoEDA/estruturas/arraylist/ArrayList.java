package dev.ProjetoEDA.estruturas.arraylist;

/**
 * Implementação de uma estrutura de dados ArrayList utilizando um array dinâmico de inteiros.
 * 
 * A estrutura suporta operações básicas como inserção, remoção, acesso a elementos e redimensionamento
 * dinâmico da capacidade do array conforme necessário.
 */
public class ArrayList {

    private int[] lista;
    public static final int CAPACIDADE_DEFAULT = 10000;
    private int tamanho;

    /**
     * Construtor padrão que cria um ArrayList com capacidade inicial padrão (1000).
     */
    public ArrayList() {
        this(CAPACIDADE_DEFAULT);
    }

    /**
     * Construtor que cria um ArrayList com a capacidade inicial especificada.
     * 
     * @param capacidade A capacidade inicial do ArrayList.
     */
    public ArrayList(int capacidade) {
        if (capacidade <= 0) capacidade = CAPACIDADE_DEFAULT;
        this.lista = new int[capacidade];
        this.tamanho = 0;
    }

    /**
     * Verifica se o ArrayList está vazio.
     * 
     * @return true se o ArrayList não contiver elementos, false caso contrário.
     */
    public boolean isEmpty() {
        return this.tamanho == 0;
    }

    /**
     * Retorna o número de elementos presentes no ArrayList.
     * 
     * @return O número de elementos no ArrayList.
     */
    public int size() {
        return this.tamanho;
    }

    /**
     * Adiciona um elemento no final do ArrayList.
     * 
     * @param element O elemento a ser adicionado.
     * @return true, sempre que o elemento for adicionado com sucesso.
     */
    public boolean add(int element) {
        assegureCapacidade(this.tamanho + 1);
        this.lista[tamanho++] = element;
        return true;
    }

    /**
     * Adiciona um elemento na posição especificada do ArrayList.
     * 
     * @param index A posição onde o elemento será inserido.
     * @param element O elemento a ser inserido.
     * @throws IndexOutOfBoundsException Se o índice fornecido for inválido.
     */
    public void add(int index, int element) {
        if (index < 0 || index > this.tamanho)
            throw new IndexOutOfBoundsException();

        assegureCapacidade(this.tamanho + 1);
        shiftParaDireita(index);

        this.lista[index] = element;
        this.tamanho++;
    }

    /**
     * Substitui o elemento na posição especificada pelo novo valor.
     * 
     * @param index A posição onde o elemento será substituído.
     * @param element O novo valor a ser colocado no índice especificado.
     * @throws IndexOutOfBoundsException Se o índice fornecido for inválido.
     */
    public void set(int index, int element) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();
        this.lista[index] = element;
    }

    /**
     * Retorna o elemento presente na posição especificada.
     * 
     * @param index A posição do elemento a ser acessado.
     * @return O elemento presente na posição especificada.
     * @throws IndexOutOfBoundsException Se o índice fornecido for inválido.
     */
    public int get(int index) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();
        return this.lista[index];
    }

    /**
     * Assegura que o ArrayList tenha capacidade suficiente para armazenar o número de elementos
     * solicitado. Caso a capacidade não seja suficiente, o ArrayList é redimensionado.
     * 
     * @param capacidadePretendida A capacidade desejada para o ArrayList.
     */
    private void assegureCapacidade(int capacidadePretendida) {
        if (capacidadePretendida <= this.lista.length) return;

        int novaCapacidade = Math.max(1, this.lista.length);
        while (novaCapacidade < capacidadePretendida) {
            int proximaCapacidade = novaCapacidade * 2;

            if (proximaCapacidade <= 0) {
                novaCapacidade = capacidadePretendida;
                break;
            }

            novaCapacidade = proximaCapacidade;
        }

        resize(novaCapacidade);
    }

    /**
     * Redimensiona o ArrayList para a nova capacidade fornecida.
     * 
     * @param novaCapacidade A nova capacidade do ArrayList.
     */
    private void resize(int novaCapacidade) {
        int[] novaLista = new int[novaCapacidade];
        for (int i = 0; i < this.tamanho; i++)
            novaLista[i] = this.lista[i];
        this.lista = novaLista;
    }

    /**
     * Desloca todos os elementos a partir do índice especificado para a direita, criando espaço
     * para um novo elemento na posição.
     * 
     * @param index O índice onde o novo elemento será inserido.
     */
    private void shiftParaDireita(int index) {
        for (int i = this.tamanho; i > index; i--) {
            this.lista[i] = this.lista[i - 1];
        }
    }

    /**
     * Desloca todos os elementos a partir do índice especificado para a esquerda, de modo a preencher
     * o espaço vazio deixado pela remoção de um elemento.
     * 
     * @param index O índice do elemento que foi removido.
     */
    private void shiftParaEsquerda(int index) {
        for (int i = index; i < this.tamanho - 1; i++) {
            this.lista[i] = this.lista[i + 1];
        }
    }

    /**
     * Retorna o índice da primeira ocorrência do elemento no ArrayList.
     * 
     * @param element O elemento a ser procurado.
     * @return O índice da primeira ocorrência do elemento, ou -1 se o elemento não for encontrado.
     */
    public int indexOf(int element) {
        for (int i = 0; i < tamanho; i++)
            if (this.lista[i] == element)
                return i;
        return -1;
    }

    /**
     * Verifica se o ArrayList contém o elemento especificado.
     * 
     * @param element O elemento a ser procurado.
     * @return true se o elemento for encontrado, false caso contrário.
     */
    public boolean contains(int element) {
        return indexOf(element) != -1;
    }

    /**
     * Remove o elemento na posição especificada e retorna o valor removido.
     * 
     * @param index A posição do elemento a ser removido.
     * @return O valor do elemento removido.
     * @throws IndexOutOfBoundsException Se o índice fornecido for inválido.
     */
    public int remove(int index) {
        if (index < 0 || index >= this.tamanho)
            throw new IndexOutOfBoundsException();

        int element = this.lista[index];
        shiftParaEsquerda(index);
        this.tamanho--;
        return element;
    }

    /**
     * Remove a primeira ocorrência do elemento especificado.
     * 
     * @param element O elemento a ser removido.
     * @return true se o elemento for encontrado e removido, false caso contrário.
     */
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

    /**
     * Retorna uma representação em string do ArrayList.
     * 
     * @return Uma string representando os elementos do ArrayList.
     */
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