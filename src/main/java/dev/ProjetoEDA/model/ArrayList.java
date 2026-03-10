package dev.ProjetoEDA.model;

/**
 * Implementação de uma estrutura de dados ArrayList utilizando um array dinâmico de inteiros.
 *
 * A estrutura suporta operações básicas como inserção, remoção, acesso a elementos e redimensionamento
 * dinâmico da capacidade do array conforme necessário.
 */
public class ArrayList implements Estrutura {

    private int[] lista;
    public static final int CAPACIDADE_DEFAULT = 3000;
    private int tamanho;

    /**
     * Construtor padrão que cria um ArrayList com capacidade inicial padrão.
     */
    public ArrayList() {
        this(CAPACIDADE_DEFAULT);
    }

    /**
     * Construtor que cria um ArrayList com a capacidade inicial especificada.
     *
     * @param capacidade a capacidade inicial do ArrayList
     */
    public ArrayList(int capacidade) {
        if (capacidade <= 0) {
            capacidade = CAPACIDADE_DEFAULT;
        }
        this.lista = new int[capacidade];
        this.tamanho = 0;
    }

    /**
     * Verifica se o ArrayList está vazio.
     *
     * @return true se o ArrayList não contiver elementos, false caso contrário
     */
    public boolean isEmpty() {
        return this.tamanho == 0;
    }

    /**
     * Retorna o número de elementos presentes no ArrayList.
     *
     * @return o número de elementos no ArrayList
     */
    public int size() {
        return this.tamanho;
    }

    /**
     * Adiciona um elemento no final do ArrayList.
     *
     * Este método implementa a operação exigida pela interface Estrutura.
     *
     * @param element o elemento a ser adicionado
     * @return true, sempre que o elemento for adicionado com sucesso
     */
    @Override
    public boolean add(int element) {
        assegureCapacidade(this.tamanho + 1);
        this.lista[this.tamanho++] = element;
        return true;
    }

    /**
     * Adiciona um elemento na posição especificada do ArrayList.
     *
     * @param index a posição onde o elemento será inserido
     * @param element o elemento a ser inserido
     * @throws IndexOutOfBoundsException se o índice fornecido for inválido
     */
    public void add(int index, int element) {
        if (index < 0 || index > this.tamanho) {
            throw new IndexOutOfBoundsException();
        }

        assegureCapacidade(this.tamanho + 1);
        shiftParaDireita(index);

        this.lista[index] = element;
        this.tamanho++;
    }

    /**
     * Substitui o elemento na posição especificada pelo novo valor.
     *
     * @param index a posição onde o elemento será substituído
     * @param element o novo valor a ser colocado no índice especificado
     * @throws IndexOutOfBoundsException se o índice fornecido for inválido
     */
    public void set(int index, int element) {
        if (index < 0 || index >= this.tamanho) {
            throw new IndexOutOfBoundsException();
        }
        this.lista[index] = element;
    }

    /**
     * Retorna o elemento presente na posição especificada.
     *
     * @param index a posição do elemento a ser acessado
     * @return o elemento presente na posição especificada
     * @throws IndexOutOfBoundsException se o índice fornecido for inválido
     */
    public int get(int index) {
        if (index < 0 || index >= this.tamanho) {
            throw new IndexOutOfBoundsException();
        }
        return this.lista[index];
    }

    /**
     * Assegura que o ArrayList tenha capacidade suficiente para armazenar
     * a quantidade desejada de elementos.
     *
     * @param capacidadePretendida a capacidade desejada
     */
    private void assegureCapacidade(int capacidadePretendida) {
        if (capacidadePretendida <= this.lista.length) {
            return;
        }

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
     * Redimensiona o array interno para a nova capacidade fornecida.
     *
     * @param novaCapacidade a nova capacidade do ArrayList
     */
    private void resize(int novaCapacidade) {
        int[] novaLista = new int[novaCapacidade];
        for (int i = 0; i < this.tamanho; i++) {
            novaLista[i] = this.lista[i];
        }
        this.lista = novaLista;
    }

    /**
     * Desloca todos os elementos a partir do índice especificado para a direita.
     *
     * @param index o índice onde o novo elemento será inserido
     */
    private void shiftParaDireita(int index) {
        for (int i = this.tamanho; i > index; i--) {
            this.lista[i] = this.lista[i - 1];
        }
    }

    /**
     * Desloca todos os elementos a partir do índice especificado para a esquerda.
     *
     * @param index o índice do elemento removido
     */
    private void shiftParaEsquerda(int index) {
        for (int i = index; i < this.tamanho - 1; i++) {
            this.lista[i] = this.lista[i + 1];
        }
    }

    /**
     * Retorna o índice da primeira ocorrência do elemento no ArrayList.
     *
     * Esta operação faz uma varredura linear e possui custo O(n).
     *
     * @param element o elemento a ser procurado
     * @return o índice da primeira ocorrência do elemento, ou -1 se não for encontrado
     */
    public int indexOf(int element) {
        for (int i = 0; i < this.tamanho; i++) {
            if (this.lista[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Verifica se o ArrayList contém o elemento especificado.
     *
     * Esta operação possui custo O(n), pois percorre o array sequencialmente.
     *
     * @param element o elemento a ser procurado
     * @return true se o elemento for encontrado, false caso contrário
     */
    public boolean contains(int element) {
        return indexOf(element) != -1;
    }

    /**
     * Busca um elemento na estrutura.
     *
     * Esta implementação atende à interface Estrutura e realiza
     * busca linear no array, com custo O(n).
     *
     * @param element o elemento a ser buscado
     * @return true se o elemento estiver presente, false caso contrário
     */
    @Override
    public boolean search(int element) {
        return contains(element);
    }

    /**
     * Remove o elemento na posição especificada e retorna o valor removido.
     *
     * Este método representa remoção por índice.
     *
     * @param index a posição do elemento a ser removido
     * @return o valor do elemento removido
     * @throws IndexOutOfBoundsException se o índice fornecido for inválido
     */
    public int removeAt(int index) {
        if (index < 0 || index >= this.tamanho) {
            throw new IndexOutOfBoundsException();
        }

        int element = this.lista[index];
        shiftParaEsquerda(index);
        this.tamanho--;
        return element;
    }

    /**
     * Remove a primeira ocorrência do elemento especificado.
     *
     * Este método implementa a operação exigida pela interface Estrutura.
     * A busca do elemento é linear, portanto o custo é O(n).
     *
     * @param element o elemento a ser removido
     * @return true se o elemento for encontrado e removido, false caso contrário
     */
    @Override
    public boolean remove(int element) {
        int index = indexOf(element);

        if (index == -1) {
            return false;
        }

        removeAt(index);
        return true;
    }

    /**
     * Retorna uma representação textual do ArrayList.
     *
     * @return string representando os elementos do ArrayList
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < this.tamanho; i++) {
            sb.append(this.lista[i]);
            if (i < this.tamanho - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}