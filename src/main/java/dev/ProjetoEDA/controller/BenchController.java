package dev.ProjetoEDA.controller;

import dev.ProjetoEDA.service.Bench;
import dev.ProjetoEDA.service.BenchAVL;
import dev.ProjetoEDA.service.BenchArrayList;
import dev.ProjetoEDA.service.BenchBST;
import dev.ProjetoEDA.service.BenchHeap;
import dev.ProjetoEDA.service.BenchLinkedList;
import dev.ProjetoEDA.service.BenchPV;
import dev.ProjetoEDA.service.BenchPriorityQueue;

/**
 * Classe responsável por selecionar qual implementação de benchmark deve ser
 * executada com base no nome da estrutura informado pela aplicação.
 *
 * Esta classe atua como ponto de roteamento entre a entrada recebida pela
 * camada principal da aplicação e a classe concreta de benchmark que será
 * realmente executada.
 *
 * O fluxo seguido por esta classe é:
 *
 * 1. receber o nome da estrutura e o tamanho máximo da entrada;
 * 2. identificar qual classe concreta de Bench corresponde à estrutura pedida;
 * 3. criar a instância correta de benchmark;
 * 4. configurar o tamanho máximo da entrada;
 * 5. iniciar a execução do benchmark.
 *
 * A existência desse controlador evita que a classe principal da aplicação
 * precise conhecer diretamente todas as subclasses de Bench. Com isso, a
 * lógica de seleção da estrutura fica centralizada em um único lugar, o que
 * melhora a organização do código e facilita manutenção e expansão futura.
 */
public class BenchController {

    /**
     * Executa o benchmark correspondente à estrutura informada.
     *
     * Este método coordena o fluxo principal do controlador. Primeiro, ele
     * solicita a criação da instância concreta de benchmark apropriada para
     * a estrutura recebida. Em seguida, configura o tamanho máximo de entrada
     * que será usado para gerar as escalas do experimento. Por fim, dispara
     * a execução completa do benchmark.
     *
     * O método não contém a lógica dos experimentos em si. Sua responsabilidade
     * é apenas preparar a instância correta e iniciar o processo.
     *
     * @param estrutura nome da estrutura que será benchmarkada
     * @param tamanhoEntrada maior tamanho de entrada que será usado no experimento
     */
    public void executar(String estrutura, int tamanhoEntrada) {
        Bench bench = criarBench(estrutura);
        bench.definirEntrada(tamanhoEntrada);
        bench.run();
    }

    /**
     * Cria a instância concreta de benchmark correspondente ao nome da
     * estrutura informado.
     *
     * Esse método funciona como fábrica interna do controlador. A escolha da
     * subclasse correta é feita por meio de um switch sobre o nome da estrutura.
     *
     * Essa centralização é importante porque garante que a decisão de qual
     * benchmark instanciar fique encapsulada no controlador, em vez de ser
     * espalhada por outros pontos do sistema.
     *
     * O nome é normalizado para minúsculas antes da comparação, reduzindo a
     * chance de erro por diferença de caixa na entrada recebida.
     *
     * Se o nome informado não corresponder a nenhuma estrutura suportada,
     * o método lança IllegalArgumentException para sinalizar que a aplicação
     * recebeu um identificador inválido.
     *
     * @param estrutura nome da estrutura solicitada
     * @return instância da classe de benchmark apropriada
     * @throws IllegalArgumentException quando o nome da estrutura não é reconhecido
     */
    private Bench criarBench(String estrutura) {
        switch (estrutura.toLowerCase()) {
            case "arraylist":
                return new BenchArrayList();
            case "linkedlist":
                return new BenchLinkedList();
            case "heap":
                return new BenchHeap();
            case "priorityqueue":
                return new BenchPriorityQueue();
            case "avl":
                return new BenchAVL();
            case "bst":
                return new BenchBST();
            case "pv":
                return new BenchPV();
            default:
                throw new IllegalArgumentException("Benchmark desconhecido: " + estrutura);
        }
    }
}