package dev.ProjetoEDA.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ProjetoEDA.model.Estrutura;

/**
 * Classe abstrata base responsável por executar benchmarks sobre estruturas de dados.
 *
 * Esta classe organiza todo o fluxo de experimentação de forma padronizada para
 * qualquer implementação concreta de Estrutura. O processo completo segue três etapas:
 *
 * 1. carregar os dados de entrada a partir dos arquivos CSV;
 * 2. executar benchmarks de operações isoladas;
 * 3. executar benchmarks de workloads mistos.
 *
 * O objetivo dessa centralização é garantir que todas as estruturas sejam avaliadas
 * sob o mesmo protocolo experimental, com a mesma política de warmup, medição,
 * escalas de entrada e escrita de resultados.
 *
 * Os benchmarks de operações isoladas existem para medir o custo de uma única operação
 * em um contexto controlado. Já os workloads mistos existem para observar o comportamento
 * da estrutura em um padrão de uso mais próximo de uma execução real, em que inserções,
 * buscas e remoções aparecem combinadas.
 *
 * A coluna de memória do CSV representa a ocupação aproximada da estrutura com n elementos,
 * calculada separadamente do tempo da operação, mas gravada na mesma tabela de resultados.
 *
 * Antes das medições reais, a classe executa rodadas de warmup. Isso é feito para reduzir
 * o impacto de efeitos transitórios da JVM, como carregamento inicial de classes,
 * compilação JIT e oscilações das primeiras execuções.
 */
public abstract class Bench {

    /**
     * Representa as operações que podem ser medidas pelo benchmark.
     *
     * Essas operações são usadas tanto nos experimentos isolados quanto nos workloads.
     */
    public enum Operacao {
        ADD,
        SEARCH,
        REMOVE
    }

    /**
     * Representa os cenários de workload misto suportados pela classe.
     *
     * Cada caso define uma distribuição fixa entre inserções, remoções e buscas
     * ao longo de uma rodada com n passos.
     */
    public enum CasoMisto {
        C100I0R0S("100I0R0S"),
        C75I25R0S("75I25R0S"),
        C50I25R25S("50I25R25S"),
        C50I0R50S("50I0R50S"),
	C50I50R0S("50I50R0S");

        /**
         * Nome textual do cenário, usado principalmente para compor nomes de arquivos.
         */
        private final String nome;

        /**
         * Cria um cenário de workload com seu identificador textual.
         *
         * @param nome representação textual do caso misto
         */
        CasoMisto(String nome) {
            this.nome = nome;
        }

        /**
         * Retorna o nome textual do caso misto.
         *
         * @return nome do cenário
         */
        public String getNome() {
            return nome;
        }
    }

    /**
     * Conjunto das ordens de entrada avaliadas.
     *
     * Cada ordem aponta para uma lista de dados previamente carregada.
     */
    protected static final String[] ORDENS = { "random", "crescente", "decrescente" };

    /**
     * Operações executadas no modo de benchmark isolado.
     */
    protected static final Operacao[] OPERACOES_ISOLADAS = {
        Operacao.ADD,
        Operacao.SEARCH,
        Operacao.REMOVE
    };

    /**
     * Casos executados no modo de workload misto.
     */
    protected static final CasoMisto[] CASOS_MISTOS = {
        CasoMisto.C100I0R0S,
        CasoMisto.C75I25R0S,
        CasoMisto.C50I25R25S,
        CasoMisto.C50I0R50S,
	CasoMisto.C50I50R0S
    };

    /**
     * Dados de entrada em ordem aleatória.
     */
    protected static List<Integer> random;

    /**
     * Dados de entrada em ordem crescente.
     */
    protected static List<Integer> crescente;

    /**
     * Dados de entrada em ordem decrescente.
     */
    protected static List<Integer> decrescente;

    /**
     * Tamanho máximo de entrada usado para gerar as escalas do experimento.
     */
    protected static int entrada;

    /**
     * Indica se os arquivos de entrada já foram carregados.
     *
     * Isso evita reler os mesmos arquivos várias vezes dentro de uma mesma execução.
     */
    private static boolean dadosCarregados = false;

    /**
     * Quantidade de rodadas de aquecimento executadas antes das medições reais.
     *
     * O warmup é necessário para reduzir ruídos causados pelo estado inicial da JVM.
     */
    protected static final int RODADAS_WARMUP = 3;

    /**
     * Quantidade de rodadas reais usadas para cálculo dos resultados.
     *
     * Ao final dessas rodadas, a mediana é usada como valor representativo.
     */
    protected static final int RODADAS_MEDICAO = 9;

    /**
     * Quantidade de repetições internas por amostra nas operações ADD e SEARCH.
     *
     * Essas repetições existem porque o custo unitário dessas operações pode ser
     * muito pequeno em nanossegundos. Repetir várias vezes reduz a sensibilidade
     * a variações pontuais de medição.
     */
    protected static final int REPETICOES_POR_AMOSTRA = 2000;

    /**
     * Quantidade de repetições internas por amostra na operação REMOVE.
     *
     * A política de medição de REMOVE é diferente da usada em ADD e SEARCH:
     * a cada repetição a estrutura é recriada com n elementos e apenas um item é removido.
     *
     * Isso é feito para que o benchmark reflita o custo de uma remoção unitária.
     * Caso várias remoções fossem feitas em sequência sobre a mesma estrutura,
     * o estado interno seria continuamente alterado, e o resultado passaria a refletir
     * o custo acumulado da sequência, e não o custo da operação individual.
     */
    protected static final int REPETICOES_REMOVE = 200;

    /**
     * Referência auxiliar para impedir descarte prematuro do objeto medido
     * no benchmark de memória.
     */
    private static volatile Object referenciaMemoria;

    /**
     * Define o maior tamanho de entrada a ser usado na geração das escalas.
     *
     * @param tamanhoEntrada maior valor de entrada considerado no experimento
     * @throws IllegalArgumentException quando o valor informado é menor ou igual a zero
     */
    public void definirEntrada(int tamanhoEntrada) {
        if (tamanhoEntrada <= 0) {
            throw new IllegalArgumentException("O tamanho da entrada deve ser maior que zero.");
        }

        entrada = tamanhoEntrada;
    }

    /**
     * Executa o fluxo completo de benchmark.
     *
     * A ordem de execução é:
     *
     * 1. carregar os dados de entrada;
     * 2. executar benchmarks de operações isoladas;
     * 3. executar benchmarks de workloads mistos.
     *
     * Toda exceção lançada durante o processo é capturada e impressa,
     * permitindo identificar falhas na preparação dos dados ou na execução
     * das medições.
     */
    public void run() {
        try {
            lerDados();
            executarOperacoesIsoladas();
            executarCasosMistos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executa todos os experimentos de operações isoladas.
     *
     * Para cada operação suportada, o benchmark é executado nas três ordens
     * de entrada definidas pela classe.
     *
     * @throws IOException quando ocorre erro na escrita dos arquivos de saída
     */
    protected void executarOperacoesIsoladas() throws IOException {
        for (Operacao operacao : OPERACOES_ISOLADAS) {
            for (String ordem : ORDENS) {
                executarExperimentoOperacao(ordem, operacao);
            }
        }
    }

    /**
     * Executa todos os experimentos de workload misto.
     *
     * Para cada cenário misto suportado, o benchmark é executado nas três
     * ordens de entrada definidas pela classe.
     *
     * @throws IOException quando ocorre erro na escrita dos arquivos de saída
     */
    protected void executarCasosMistos() throws IOException {
        for (CasoMisto caso : CASOS_MISTOS) {
            for (String ordem : ORDENS) {
                executarExperimentoWorkload(ordem, caso);
            }
        }
    }

    /**
     * Executa as rodadas de warmup para uma operação isolada e um tamanho n.
     *
     * Essas execuções não são gravadas. Elas servem apenas para estabilizar
     * a execução antes da coleta das amostras reais.
     *
     * @param dados lista de dados correspondente à ordem em teste
     * @param n tamanho base da estrutura para a rodada
     * @param operacao operação que será aquecida
     */
    protected void executarWarmupOperacao(List<Integer> dados, int n, Operacao operacao) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            medirOperacao(dados, n, operacao, i);
        }
    }

    /**
     * Executa o benchmark de uma operação isolada para uma ordem específica.
     *
     * O fluxo do método é:
     *
     * 1. selecionar a lista de dados correspondente à ordem;
     * 2. abrir o arquivo CSV de saída;
     * 3. gerar as escalas de tamanho de entrada;
     * 4. para cada escala, executar warmup, coletar medições e gravar a mediana.
     *
     * A coluna de memória representa a ocupação aproximada da estrutura com n elementos,
     * calculada separadamente, mas gravada junto na mesma tabela.
     *
     * @param ordem ordem dos dados usada no experimento
     * @param operacao operação isolada a ser medida
     * @throws IOException quando ocorre erro ao criar ou escrever o CSV
     */
    protected void executarExperimentoOperacao(String ordem, Operacao operacao) throws IOException {
        List<Integer> dados = getDadosPorOrdem(ordem);
        String pathSaida = gerarPathArquivoSaidaOperacao(ordem, operacao);

        try (BufferedWriter writer = inicializarArquivoDeSaida(pathSaida)) {
            for (int n : gerarEscalas()) {
                executarWarmupOperacao(dados, n, operacao);

                long[] tempos = new long[RODADAS_MEDICAO];

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoOperacao resultado = medirOperacao(dados, n, operacao, rodada);
                    tempos[rodada] = resultado.tempoMedio;
                }

                long memoriaEstrutura = medirMemoriaEstrutura(dados, n);

                gravarLinhaResultado(
                        writer,
                        n,
                        operacao.name(),
                        calcularMediana(tempos),
                        memoriaEstrutura
                );
            }
        }
    }

    /**
     * Direciona a medição para o método específico da operação solicitada.
     *
     * Esse método concentra o despacho das operações e garante que o restante
     * da classe possa trabalhar com uma única interface de medição.
     *
     * @param dados lista de dados usada para construir ou consultar a estrutura
     * @param n tamanho base da rodada
     * @param operacao operação a ser medida
     * @param rodada índice da rodada atual
     * @return resultado contendo tempo médio da operação
     */
    protected ResultadoOperacao medirOperacao(List<Integer> dados, int n, Operacao operacao, int rodada) {
        switch (operacao) {
            case ADD:
                return medirAdd(dados, n, rodada);
            case SEARCH:
                return medirSearch(dados, n, rodada);
            case REMOVE:
                return medirRemove(dados, n, rodada);
            default:
                throw new IllegalArgumentException("Operação inválida: " + operacao);
        }
    }

    /**
     * Mede o custo médio da operação ADD.
     *
     * Em cada repetição interna:
     *
     * 1. uma nova estrutura é criada;
     * 2. a estrutura é previamente populada com n elementos;
     * 3. um elemento adicional é inserido;
     * 4. o tempo dessa última inserção é acumulado.
     *
     * @param dados lista de dados usada como base da construção e do elemento inserido
     * @param n tamanho inicial da estrutura antes da inserção medida
     * @param rodada índice da rodada atual
     * @return resultado médio da operação ADD
     */
    protected ResultadoOperacao medirAdd(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_POR_AMOSTRA; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valorNovo = dados.get(n + r + rodada);

            long inicio = System.nanoTime();
            estrutura.add(valorNovo);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_POR_AMOSTRA;
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede o custo médio da operação SEARCH.
     *
     * Em cada repetição interna:
     *
     * 1. uma nova estrutura é criada;
     * 2. a estrutura é preenchida com n elementos;
     * 3. um elemento já presente é buscado;
     * 4. o tempo da busca é acumulado.
     *
     * @param dados lista de dados usada na construção da estrutura e na busca
     * @param n tamanho da estrutura consultada
     * @param rodada índice da rodada atual
     * @return resultado médio da operação SEARCH
     */
    protected ResultadoOperacao medirSearch(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_POR_AMOSTRA; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valorBusca = dados.get(n - 1);

            long inicio = System.nanoTime();
            estrutura.search(valorBusca);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_POR_AMOSTRA;
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede o custo médio da operação REMOVE de forma unitária.
     *
     * Em cada repetição interna:
     *
     * 1. uma nova estrutura é criada;
     * 2. a estrutura é preenchida com n elementos;
     * 3. um único elemento é removido;
     * 4. o tempo da remoção é acumulado.
     *
     * @param dados lista de dados usada na construção e na remoção
     * @param n tamanho da estrutura antes da remoção medida
     * @param rodada índice da rodada atual
     * @return resultado médio da operação REMOVE
     */
    protected ResultadoOperacao medirRemove(List<Integer> dados, int n, int rodada) {
        long tempoTotal = 0L;

        for (int r = 0; r < REPETICOES_REMOVE; r++) {
            Estrutura estrutura = criarEstrutura();

            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            int valor = dados.get(n - 1);

            long inicio = System.nanoTime();
            estrutura.remove(valor);
            long fim = System.nanoTime();

            tempoTotal += (fim - inicio);
        }

        long tempoMedio = tempoTotal / REPETICOES_REMOVE;
        return new ResultadoOperacao(tempoMedio, 0L);
    }

    /**
     * Mede a ocupação de memória da estrutura após inserção de n elementos.
     *
     * @param dados lista de dados usada para popular a estrutura
     * @param n tamanho da estrutura
     * @return memória ocupada em bytes
     */
    protected long medirMemoriaEstrutura(List<Integer> dados, int n) {
        long[] memorias = new long[RODADAS_MEDICAO];

        for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
            estabilizarHeap();

            long memoriaAntes = getHeapUsedBytes();

            Estrutura estrutura = criarEstrutura();
            for (int i = 0; i < n; i++) {
                estrutura.add(dados.get(i));
            }

            referenciaMemoria = estrutura;
            estabilizarHeap();

            long memoriaDepois = getHeapUsedBytes();
            memorias[rodada] = Math.max(0L, memoriaDepois - memoriaAntes);

            referenciaMemoria = null;
            estabilizarHeap();
        }

        return calcularMediana(memorias);
    }

    /**
     * Executa um benchmark de workload misto para uma ordem e um cenário específicos.
     *
     * O fluxo do método é:
     *
     * 1. selecionar a lista de dados pela ordem;
     * 2. abrir o arquivo CSV do caso misto;
     * 3. gerar as escalas;
     * 4. executar warmup para cada escala;
     * 5. rodar várias medições do workload;
     * 6. calcular o tempo médio por tipo de operação dentro da rodada;
     * 7. gravar a mediana dessas médias no CSV.
     *
     * A coluna de memória representa a ocupação aproximada da estrutura com n elementos,
     * calculada separadamente, mas gravada junto na mesma tabela.
     *
     * @param ordem ordem de entrada usada no experimento
     * @param caso cenário misto a ser executado
     * @throws IOException quando ocorre erro ao criar ou escrever o CSV
     */
    protected void executarExperimentoWorkload(String ordem, CasoMisto caso) throws IOException {
        List<Integer> dados = getDadosPorOrdem(ordem);
        String pathSaida = gerarPathArquivoSaidaWorkload(ordem, caso);

        try (BufferedWriter writer = inicializarArquivoDeSaida(pathSaida)) {
            for (int n : gerarEscalas()) {
                executarWarmupWorkload(dados, n, caso, ordem);

                long[] temposAdd = new long[RODADAS_MEDICAO];
                long[] temposSearch = new long[RODADAS_MEDICAO];
                long[] temposRemove = new long[RODADAS_MEDICAO];

                boolean temAdd = false;
                boolean temSearch = false;
                boolean temRemove = false;

                for (int rodada = 0; rodada < RODADAS_MEDICAO; rodada++) {
                    ResultadoWorkload resultado = executarRoundWorkload(dados, n, caso, ordem, rodada);

                    if (resultado.qtdAdd > 0) {
                        temposAdd[rodada] = resultado.tempoAdd / resultado.qtdAdd;
                        temAdd = true;
                    }

                    if (resultado.qtdSearch > 0) {
                        temposSearch[rodada] = resultado.tempoSearch / resultado.qtdSearch;
                        temSearch = true;
                    }

                    if (resultado.qtdRemove > 0) {
                        temposRemove[rodada] = resultado.tempoRemove / resultado.qtdRemove;
                        temRemove = true;
                    }
                }

                long memoriaEstrutura = medirMemoriaEstrutura(dados, n);

                if (temAdd) {
                    gravarLinhaResultado(writer, n, Operacao.ADD.name(), calcularMediana(temposAdd), memoriaEstrutura);
                }

                if (temSearch) {
                    gravarLinhaResultado(writer, n, Operacao.SEARCH.name(), calcularMediana(temposSearch), memoriaEstrutura);
                }

                if (temRemove) {
                    gravarLinhaResultado(writer, n, Operacao.REMOVE.name(), calcularMediana(temposRemove), memoriaEstrutura);
                }
            }
        }
    }

    /**
     * Executa as rodadas de warmup para um workload misto específico.
     *
     * @param dados lista de dados usada no workload
     * @param n número total de passos da rodada
     * @param caso cenário misto em execução
     * @param ordem ordem de entrada usada como base
     */
    protected void executarWarmupWorkload(List<Integer> dados, int n, CasoMisto caso, String ordem) {
        for (int i = 0; i < RODADAS_WARMUP; i++) {
            executarRoundWorkload(dados, n, caso, ordem, i);
        }
    }

    /**
     * Executa uma rodada completa de workload misto.
     *
     * @param dados lista de dados usada pelo workload
     * @param n quantidade total de passos da rodada
     * @param caso cenário misto executado
     * @param ordem ordem de entrada usada
     * @param rodada índice da rodada atual
     * @return objeto com tempos acumulados e quantidades executadas por operação
     */
    protected ResultadoWorkload executarRoundWorkload(
            List<Integer> dados,
            int n,
            CasoMisto caso,
            String ordem,
            int rodada
    ) {
        ResultadoWorkload resultado = new ResultadoWorkload();

        Estrutura estrutura = criarEstrutura();
        java.util.ArrayList<Integer> ativos = new java.util.ArrayList<>();

        int addExecutados = 0;

        Random randomizador = new Random(
                31L * n
                        + 17L * rodada
                        + 13L * Math.abs(ordem.hashCode())
                        + 97L * caso.ordinal()
        );

        for (int passo = 0; passo < n; passo++) {
            Operacao operacao = descobrirOperacaoWorkload(caso, passo, n);

            long inicio = System.nanoTime();

            switch (operacao) {
                case ADD:
                    int valorInsercao = dados.get(addExecutados);
                    estrutura.add(valorInsercao);
                    ativos.add(valorInsercao);
                    addExecutados++;
                    resultado.qtdAdd++;
                    resultado.tempoAdd += (System.nanoTime() - inicio);
                    break;

                case SEARCH:
                    if (!ativos.isEmpty()) {
                        int indiceBusca = randomizador.nextInt(ativos.size());
                        int valorBusca = ativos.get(indiceBusca);
                        estrutura.search(valorBusca);
                        resultado.qtdSearch++;
                        resultado.tempoSearch += (System.nanoTime() - inicio);
                    }
                    break;

                case REMOVE:
                    if (!ativos.isEmpty()) {
                        int indiceRemocao = randomizador.nextInt(ativos.size());
                        int valorRemocao = ativos.remove(indiceRemocao);
                        estrutura.remove(valorRemocao);
                        resultado.qtdRemove++;
                        resultado.tempoRemove += (System.nanoTime() - inicio);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Operação inválida no workload: " + operacao);
            }
        }

        return resultado;
    }

    /**
     * Determina qual operação deve ser executada em um passo do workload.
     *
     * @param caso cenário misto em execução
     * @param passo posição atual dentro da rodada
     * @param n quantidade total de passos da rodada
     * @return operação correspondente ao passo
     */
    protected Operacao descobrirOperacaoWorkload(CasoMisto caso, int passo, int n) {
        int qtdAdd = quantidadeAdd(caso, n);
        int qtdSearch = quantidadeSearch(caso, n);

        switch (caso) {
            case C100I0R0S:
                return Operacao.ADD;
            case C75I25R0S:
            case C50I50R0S:
                return passo < qtdAdd ? Operacao.ADD : Operacao.REMOVE;
            case C50I25R25S:
                if (passo < qtdAdd) {
                    return Operacao.ADD;
                } else if (passo < qtdAdd + qtdSearch) {
                    return Operacao.SEARCH;
                } else {
                    return Operacao.REMOVE;
                }
            case C50I0R50S:
                return passo < qtdAdd ? Operacao.ADD : Operacao.SEARCH;
            default:
                throw new IllegalArgumentException("Caso misto inválido: " + caso.getNome());
        }
    }

    /**
     * Calcula quantas inserções devem ocorrer em um cenário misto de tamanho n.
     *
     * @param caso cenário misto avaliado
     * @param n tamanho total da rodada
     * @return quantidade de operações ADD do cenário
     */
    protected int quantidadeAdd(CasoMisto caso, int n) {
        switch (caso) {
            case C100I0R0S:
                return n;
            case C75I25R0S:
                return (int) Math.round(n * 0.75);
            case C50I25R25S:
            case C50I0R50S:
            case C50I50R0S:
                return n / 2;
            default:
                return 0;
        }
    }

    /**
     * Calcula quantas buscas devem ocorrer em um cenário misto de tamanho n.
     *
     * @param caso cenário misto avaliado
     * @param n tamanho total da rodada
     * @return quantidade de operações SEARCH do cenário
     */
    protected int quantidadeSearch(CasoMisto caso, int n) {
        switch (caso) {
            case C50I25R25S:
                return n / 4;
            case C50I0R50S:
                return n - quantidadeAdd(caso, n);
            default:
                return 0;
        }
    }

    /**
     * Gera as escalas de tamanho de entrada usadas no experimento.
     *
     * @return vetor com os tamanhos de entrada do benchmark
     */
    protected int[] gerarEscalas() {
        int minimo = 100;
        int maximo = entrada;

        if (maximo <= minimo) {
            return new int[] { maximo };
        }

        int quantidade = 10;
        java.util.Set<Integer> escalas = new java.util.LinkedHashSet<>();

        double logMin = Math.log10(minimo);
        double logMax = Math.log10(maximo);

        for (int i = 0; i < quantidade; i++) {
            double t = (double) i / (quantidade - 1);
            double valor = Math.pow(10, logMin + (logMax - logMin) * t);
            int escala = (int) Math.round(valor);

            if (escala <= maximo) {
                escalas.add(escala);
            }
        }

        escalas.add(maximo);

        return escalas.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Distribui um índice ao longo do intervalo da estrutura.
     *
     * @param iteracao posição atual da iteração
     * @param total número total de iterações
     * @param tamanho tamanho máximo do intervalo
     * @return índice distribuído dentro do intervalo válido
     */
    protected int distribuirIndice(int iteracao, int total, int tamanho) {
        if (tamanho <= 1) {
            return 0;
        }

        int indice = (int) (((long) iteracao * tamanho) / Math.max(1, total));
        return Math.min(indice, tamanho - 1);
    }

    /**
     * Calcula a mediana de um vetor de valores long.
     *
     * @param valores vetor de valores a ser ordenado e analisado
     * @return valor mediano do conjunto
     */
    protected long calcularMediana(long[] valores) {
        long[] copia = Arrays.copyOf(valores, valores.length);
        Arrays.sort(copia);

        int meio = copia.length / 2;

        if (copia.length % 2 == 0) {
            return (copia[meio - 1] + copia[meio]) / 2;
        }

        return copia[meio];
    }

    /**
     * Retorna a lista de dados correspondente à ordem informada.
     *
     * @param ordem identificador da ordem desejada
     * @return lista de inteiros associada à ordem
     * @throws IllegalArgumentException quando a ordem informada não é válida
     */
    protected List<Integer> getDadosPorOrdem(String ordem) {
        switch (ordem) {
            case "random":
                return random;
            case "crescente":
                return crescente;
            case "decrescente":
                return decrescente;
            default:
                throw new IllegalArgumentException("Ordem inválida: " + ordem);
        }
    }

    /**
     * Gera o caminho do arquivo CSV de saída para um benchmark de operação isolada.
     *
     * @param ordem ordem de entrada do experimento
     * @param operacao operação isolada medida
     * @return caminho completo do arquivo CSV
     */
    protected String gerarPathArquivoSaidaOperacao(String ordem, Operacao operacao) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_" + operacao.name().toLowerCase() + ".csv";
    }

    /**
     * Gera o caminho do arquivo CSV de saída para um benchmark de workload misto.
     *
     * @param ordem ordem de entrada do experimento
     * @param caso cenário misto medido
     * @return caminho completo do arquivo CSV
     */
    protected String gerarPathArquivoSaidaWorkload(String ordem, CasoMisto caso) {
        String nomeEstrutura = getNomeEstrutura().toLowerCase();
        return "src/main/java/dev/ProjetoEDA/repository/results/"
                + nomeEstrutura + "/result_"
                + nomeEstrutura + "_" + ordem + "_workload_" + caso.getNome() + ".csv";
    }

    /**
     * Cria e inicializa o arquivo CSV de saída.
     *
     * @param resultFilePath caminho do arquivo de saída
     * @return writer já posicionado para escrita do conteúdo
     * @throws IOException quando ocorre falha na criação ou abertura do arquivo
     */
    protected BufferedWriter inicializarArquivoDeSaida(String resultFilePath) throws IOException {
        File file = new File(resultFilePath);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            file.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("TamanhoEntrada,Operacao,TempoMedio(ns),MemoriaUso(bytes)\n");
        return writer;
    }

    /**
     * Grava uma linha de resultado no arquivo CSV.
     *
     * @param writer writer associado ao arquivo de saída
     * @param tamanhoEntrada tamanho de entrada medido
     * @param operacao nome da operação registrada
     * @param tempoMedio tempo médio ou mediano calculado
     * @param memoriaUso memória média ou mediana calculada
     * @throws IOException quando ocorre falha na escrita do arquivo
     */
    protected void gravarLinhaResultado(
            BufferedWriter writer,
            int tamanhoEntrada,
            String operacao,
            long tempoMedio,
            long memoriaUso
    ) throws IOException {
        writer.write(
                tamanhoEntrada + "," +
                operacao + "," +
                tempoMedio + "," +
                memoriaUso + "\n"
        );
    }

    /**
     * Carrega os arquivos de entrada em memória.
     *
     * @throws IOException quando ocorre falha na leitura dos arquivos
     */
    protected void lerDados() throws IOException {
        if (dadosCarregados) {
            return;
        }

        random = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv");
        crescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv");
        decrescente = carregarInteiros("src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv");

        dadosCarregados = true;
    }

    /**
     * Lê um arquivo CSV e extrai a primeira coluna numérica de cada linha válida.
     *
     * @param path caminho do arquivo a ser lido
     * @return lista de inteiros extraída do arquivo
     * @throws IOException quando ocorre falha na leitura do arquivo
     */
    protected List<Integer> carregarInteiros(String path) throws IOException {
        try (Stream<String> linhas = Files.lines(Paths.get(path))) {
            return linhas
                    .map(String::trim)
                    .filter(linha -> !linha.isEmpty())
                    .filter(linha -> !linha.matches(".*[a-zA-Z].*"))
                    .map(linha -> linha.split(",")[0].trim())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Retorna a quantidade de heap atualmente usada pela JVM.
     *
     * @return heap usada em bytes
     */
    protected long getHeapUsedBytes() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Tenta estabilizar a heap antes de uma leitura de memória.
     */
    protected void estabilizarHeap() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retorna o nome da estrutura concreta em benchmark.
     *
     * @return nome textual da estrutura
     */
    protected abstract String getNomeEstrutura();

    /**
     * Cria uma nova instância vazia da estrutura concreta em benchmark.
     *
     * @return nova estrutura vazia
     */
    protected abstract Estrutura criarEstrutura();

    /**
     * Representa o resultado agregado de uma operação isolada.
     *
     * O objeto guarda o tempo médio e a memória média obtidos dentro de uma rodada.
     */
    protected static class ResultadoOperacao {

        /**
         * Tempo médio calculado para a operação.
         */
        long tempoMedio;

        /**
         * Memória média calculada para a operação.
         */
        long memoriaMedia;

        /**
         * Cria um resultado de operação isolada.
         *
         * @param tempoMedio tempo médio medido
         * @param memoriaMedia memória média medida
         */
        ResultadoOperacao(long tempoMedio, long memoriaMedia) {
            this.tempoMedio = tempoMedio;
            this.memoriaMedia = memoriaMedia;
        }
    }

    /**
     * Representa os acumuladores de uma rodada de workload misto.
     *
     * O objeto armazena tempos totais e quantidades executadas por tipo de operação.
     * Depois da rodada, esses valores são usados para calcular o tempo médio por operação.
     */
    protected static class ResultadoWorkload {

        /**
         * Tempo total acumulado pelas inserções da rodada.
         */
        long tempoAdd;

        /**
         * Tempo total acumulado pelas buscas da rodada.
         */
        long tempoSearch;

        /**
         * Tempo total acumulado pelas remoções da rodada.
         */
        long tempoRemove;

        /**
         * Quantidade de inserções executadas na rodada.
         */
        int qtdAdd;

        /**
         * Quantidade de buscas executadas na rodada.
         */
        int qtdSearch;

        /**
         * Quantidade de remoções executadas na rodada.
         */
        int qtdRemove;
    }
}
