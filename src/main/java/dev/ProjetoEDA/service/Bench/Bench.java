package dev.ProjetoEDA.service.Bench;

public abstract class Bench {

    //** Lista de dados de entrada aleatórios. */
    protected static List<Integer> random;
    //** Lista de dados de entrada ordenados crescente. */
    protected static List<Integer> crescente;
    //** Lista de dados de entrada ordenados decrescente. */
    protected static List<Integer> decrescente;
    //** Lista dos tamanho das entradas utilizados nos testes. */
    protected static List<Integer> entradas;
    //** Número de repetições para cada experimento. */
    protected static final int REPETICOES = 18;


    /**
     * Método principal de execução do benchmark.
     * Deve ser implementado pelas subclasses para iniciar os testes
     * específicos de cada estrutura.
     */
    public abstract void run();

    /**
     * Executa os testes para um determinado conjunto de dados.
     *
     * @param aux writer utilizado para registrar os resultados no arquivo CSV
     * @throws IOException caso ocorra erro na escrita do arquivo
     */
    
    protected abstract void test(BufferedWriter aux) throws IOException;

    /**
     * Executa um experimento de benchmark para um cenário específico.
     *
     * O experimento mede tempo de execução e uso de memória para uma
     * determinada estrutura de dados, cenário de operações e tipo de entrada.
     *
     * @param tamanhoEntrada tamanho da entrada utilizada no experimento
     * @param tipoDado tipo de dado de entrada (random, crescente ou decrescente)
     * @param casoTest cenário de operações a ser executado
     * @param estrutura nome da estrutura de dados testada
     * @param writer writer utilizado para registrar o resultado
     */

    protected void experimento(int tamanhoEntrada, String tipoDado, String casoTest, String estrutura, BufferedWriter writer) {
        List<Integer> dados = getDados(tipoDado);

        long[] tempos = new long[REPETICOES];
        long[] memorias = new long[REPETICOES];

        for (int s = 0; s < REPETICOES; s++) {
            long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long tempoAntes = System.nanoTime();

            executarCaso(dados, tamanhoEntrada, casoTest);

            long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long tempoDepois = System.nanoTime();

            tempos[s] = tempoDepois - tempoAntes;
            memorias[s] = memoriaDepois - memoriaAntes;
        }

        long tempoMediana = calcularMediana(tempos);
        long memoriaMediana = calcularMediana(memorias);

        try {
            writer.write(
                tamanhoEntrada + "," +
                tipoDado + "," +
                casoTest + "," +
                estrutura + "," +
                tempoMediana + "," +
                memoriaMediana + "\n"
            );
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    
    /**
     * Executa o cenário de teste correspondente ao identificador informado.
     *
     * @param dados lista de dados de entrada
     * @param tamanhoEntrada tamanho da entrada utilizada
     * @param casoTest identificador do cenário de teste
     */
    
    protected void executarCaso(List<Integer> dados, int tamanhoEntrada, String casoTest) {
        switch (casoTest) {
            case "100I0R0S":
                executarI100_R0_S0(dados, tamanhoEntrada);
                break;

            case "50I50R0S":
                executarI50_R50_S0(dados, tamanhoEntrada);
                break;

            case "75I25R0S":
                executarI75_R25_S0(dados, tamanhoEntrada);
                break;

            case "50I25R25S":
                executarI50_R25_S25(dados, tamanhoEntrada);
                break;

            case "50I0R50S":
                executarI50_R0_S50(dados, tamanhoEntrada);
                break;

            default:
                throw new IllegalArgumentException("Caso de teste inválido: " + casoTest);
        }
    }

    /** Executa cenário com 100% inserções. */
    protected abstract void executarI100_R0_S0(List<Integer> dados, int n);
    
    /** Executa cenário com 50% inserções e 50% remoções. */
    protected abstract void executarI50_R50_S0(List<Integer> dados, int n);
    
    /** Executa cenário com 75% inserções e 25% remoções. */
    protected abstract void executarI75_R25_S0(List<Integer> dados, int n);
    
    /** Executa cenário com 50% inserções, 25% remoções e 25% buscas. */
    protected abstract void executarI50_R25_S25(List<Integer> dados, int n);
    
    /** Executa cenário com 50% inserções e 50% buscas. */
    protected abstract void executarI50_R0_S50(List<Integer> dados, int n);

     /**
     * Retorna o conjunto de dados correspondente ao tipo informado.
     *
     * @param dado tipo de dado
     * @return lista de inteiros correspondente
     */

    protected List<Integer> getDados(String dado) {
        if (dado.equals("random")) return random;
        if (dado.equals("crescente")) return crescente;
        if(dado.equals("decrescente")) return decrescente;
        return entradas;
    }

     /**
     * Calcula a mediana de um conjunto de valores.
     *
     * @param valores vetor de valores medidos
     * @return valor mediano
     */

    protected long calcularMediana(long[] valores) {
        Arrays.sort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2];
        } else {
            return (valores[n / 2 - 1] + valores[n / 2]) / 2;
        }
    }

 /**
     * Realiza a leitura dos dados de entrada utilizados nos experimentos.
     *
     * Os dados são carregados a partir de arquivos CSV localizados no
     * diretório {@code repository/entry}.
     *
     * @throws IOException caso ocorra erro na leitura dos arquivos
     */

    protected void lerDados() throws IOException {
        String caminhoRandom = "src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv";
        String caminhoCrescente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv";
        String caminhoDecrescente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv";
        String caminhoEntradas = "src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntrada.csv";

        random = Files.lines(Paths.get(caminhoRandom))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        crescente = Files.lines(Paths.get(caminhoCrescente))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        decrescente = Files.lines(Paths.get(caminhoDecrescente))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        entradas = Files.lines(Paths.get(caminhoEntradas))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }


}