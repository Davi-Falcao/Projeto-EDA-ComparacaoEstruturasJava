package dev.ProjetoEDA.bench;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import dev.ProjetoEDA.estruturas.arraylist.ArrayList;


public class BenchArrayList {

    /**
     * Método principal que executa o benchmark das operações em um ArrayList.
     * Ele lê um arquivo CSV contendo uma sequência de operações (inserção, remoção e busca),
     * executa cada operação no ArrayList, registra os tempos de execução e uso de memória,
     * e grava os resultados em um arquivo CSV de saída.
     * 
     * @param args Argumentos passados pela linha de comando, que incluem o JSON de configuração e o nome do arquivo de saída.
     * @throws IOException Caso ocorra algum erro na leitura ou escrita de arquivos.
     */
    public static void main(String[] args) throws IOException {
        System.gc();

        if (args.length < 1) {
            System.err.println("O nome do arquivo de saída não foi fornecido.");
            return;
        }

        String resultFilePath = args[args.length - 1]; 

        JSONObject config = extrairConfigOpcional(args);
        boolean ordemAdicao = config != null && config.optBoolean("OrdemAdicao", false);
        boolean ordemBusca = config != null && config.optBoolean("OrdemBusca", false);

        String filePath = "data/entradas/ArrayList/crescente_n100000_I50_R0_S50.csv";  

        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath, true));
        
        inicializarArquivoDeSaida(writer, resultFilePath);

        processarOperacoes(filePath, writer, ordemAdicao, ordemBusca);

        writer.close();
        System.out.println("Resultados gravados em: " + resultFilePath);
    }

    /**
     * Extrai a configuração opcional fornecida como argumento de linha de comando no formato JSON.
     * 
     * Este método verifica se há argumentos passados para o programa. Se não houver nenhum argumento, 
     * ele retorna {@code null}. Caso o argumento seja um único JSON válido, ele tenta parseá-lo e retorná-lo 
     * como um objeto {@link JSONObject}. Se ocorrer algum erro ao processar o JSON, uma mensagem de erro é exibida 
     * e {@code null} é retornado.
     * 
     * @param args O array de argumentos de linha de comando, que deve conter no máximo um argumento JSON.
     * @return O objeto {@link JSONObject} correspondente ao argumento JSON fornecido, ou {@code null} em caso de erro ou ausência de argumento válido.
     */
    private static JSONObject extrairConfigOpcional(String[] args) {
        if (args.length == 0) {
            return null;
        }
        try {
            return new JSONObject(args[0]);
        } catch (Exception e) {
            System.err.println("Erro ao processar o JSON fornecido. Configuração será ignorada.");
            return null;
        }
    }

    /**
     * Inicializa o arquivo de saída, verificando se o arquivo está vazio para adicionar o cabeçalho.
     * 
     * Esse método é chamado ao abrir o arquivo de saída. Caso o arquivo esteja vazio, ele escreve o cabeçalho 
     * com os nomes das colunas: "Operacao", "TamanhoEntrada", "TempoExecucao(ns)", e "MemoriaUso(bytes)".
     * 
     * @param writer O objeto {@link BufferedWriter} usado para escrever os resultados no arquivo de saída.
     * @param resultFilePath O caminho do arquivo de saída.
     * @throws IOException Caso ocorra erro na escrita no arquivo de saída.
     */
    private static void inicializarArquivoDeSaida(BufferedWriter writer, String resultFilePath) throws IOException {
        if (new File(resultFilePath).length() == 0) {
            writer.write("Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)\n");
        }
    }

    /**
     * Processa as operações a partir do arquivo CSV, executando as operações no ArrayList 
     * e gravando os resultados de tempo e uso de memória em um arquivo CSV.
     * 
     * Este método realiza a leitura do arquivo de entrada, executa as operações no ArrayList conforme
     * o tipo de operação (inserção, remoção ou busca), calcula o tempo e memória antes e depois da operação,
     * e grava os resultados no arquivo de saída.
     * 
     * @param filePath O caminho do arquivo de entrada com as operações a serem realizadas.
     * @param writer O objeto {@link BufferedWriter} usado para gravar os resultados.
     * @param ordemAdicao Flag indicando se a ordem de adição deve ser respeitada.
     * @param ordemBusca Flag indicando se a ordem de busca deve ser respeitada.
     * @throws IOException Caso ocorra erro na leitura ou escrita dos arquivos.
     */
    private static void processarOperacoes(String filePath, BufferedWriter writer, boolean ordemAdicao, boolean ordemBusca) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        ArrayList lista = new ArrayList(10000);
        
        int[] indexFound = new int[1];

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 3) {
                System.err.println("Linha mal formatada: " + line);
                continue;
            }

            String operacao = parts[0].trim();
            String indice = parts[1].trim();
            String valor = parts[2].trim();
            long[] tempoEMemoriaAntes = calcularTempoEMemoria();
            
            realizarOperacao(lista, operacao, indice, valor, ordemAdicao, ordemBusca, indexFound);

            long[] tempoEMemoriaDepois = calcularTempoEMemoria();
            
            long tempoExecucao = tempoEMemoriaDepois[0] - tempoEMemoriaAntes[0];
            long memoriaUso = tempoEMemoriaDepois[1] - tempoEMemoriaAntes[1];

            registrarResultado(writer, operacao, lista.size(), tempoExecucao, memoriaUso, indexFound[0]);
        }

        reader.close();
    }

    /**
     * Calcula o tempo de execução e o uso de memória atuais.
     * 
     * Este método captura o tempo e a memória antes e depois de uma operação para monitorar o uso de recursos.
     * Ele retorna um array com dois valores: tempo (em nanossegundos) e memória (em bytes).
     * 
     * @return Um array contendo o tempo e o uso de memória no momento da execução.
     */
    private static long[] calcularTempoEMemoria() {
        long memoria = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long tempo = System.nanoTime();
        return new long[] { tempo, memoria };
    }

    /**
     * Registra os resultados de uma operação no arquivo CSV de saída.
     * 
     * Este método escreve os resultados da operação, incluindo a operação realizada, o tamanho da entrada,
     * o tempo de execução e o uso de memória no arquivo de saída.
     * 
     * @param writer O objeto {@link BufferedWriter} usado para gravar os resultados no arquivo de saída.
     * @param operacao O tipo de operação realizada (inserção, remoção ou busca).
     * @param tamanhoEntrada O tamanho da entrada após a operação.
     * @param tempoExecucao O tempo de execução da operação em nanossegundos.
     * @param memoriaUso O uso de memória durante a operação em bytes.
     * @throws IOException Caso ocorra erro na escrita do arquivo de saída.
     */
    private static void registrarResultado(BufferedWriter writer, String operacao, int tamanhoEntrada, long tempoExecucao, long memoriaUso, int indexFound) throws IOException {
        if (operacao.equals("S")){

            writer.write(operacao + "," + indexFound + "," + tempoExecucao + "," + memoriaUso + "\n");
            return;
        }
        writer.write(operacao + "," + tamanhoEntrada + "," + tempoExecucao + "," + memoriaUso + "\n");
    }

    /**
     * Realiza a operação indicada no ArrayList.
     * 
     * Este método executa a operação solicitada (inserção, remoção ou busca) no ArrayList, de acordo com os parâmetros fornecidos.
     * Ele também respeita as flags de ordem de adição e de busca, quando aplicável.
     * 
     * @param lista A lista em que a operação será realizada.
     * @param operacao O tipo de operação a ser executada ("I" para inserção, "R" para remoção, "S" para busca).
     * @param indice O índice ou valor da operação.
     * @param valor O valor da operação (usado para inserção e busca).
     * @param ordemAdicao Flag indicando se a ordem de adição deve ser respeitada.
     * @param ordemBusca Flag indicando se a ordem de busca deve ser respeitada.
     */
    private static void realizarOperacao(ArrayList lista, String operacao, String indice, String valor, boolean ordemAdicao, boolean ordemBusca, int[] indexFound) {
        switch (operacao) {
            case "I":
                int indexInsert = Integer.parseInt(indice);
                int valueInsert = Integer.parseInt(valor);
                if (ordemAdicao || ordemBusca) {
                    lista.add(valueInsert);
                } else {
                    lista.add(indexInsert, valueInsert);
                }
                break;
            case "R":
                int indexRemove = Integer.parseInt(indice);
                lista.remove(indexRemove);
                break;
            case "S":
                int valueSearch = Integer.parseInt(valor);
                indexFound[0] = lista.indexOf(valueSearch); 
                break;
        }
    }
}