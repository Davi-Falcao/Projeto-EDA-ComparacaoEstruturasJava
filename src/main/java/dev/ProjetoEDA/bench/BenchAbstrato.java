import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BenchAbstrato{

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decresente;
    protected static List<Integer> entradas;

    protected static final int REPETICOES = 18;

    public abstract void run();

    protected abstract void test(BufferedWriter aux) throws IOException;

    protected void experimento(int entrada, String estrutura ,String test, BufferedWriter writer, String tipo){
        List<Integer> dados = super.getDados();

        long[] tempos = new long[REPETICOES];
        long[] memorias = new long[REPETICOES];

        for (int s = 0; s < REPETICOES; s++) {
            long memoriaAntes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long tempoAntes = System.nanoTime();

            executaCaso(dados, entrada, test);

            long memoriaDepois = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long tempoDepois = System.nanoTime();

            tempos[s] = tempoDepois - tempoAntes;
            memorias[s] = memoriaDepois - memoriaAntes;
        }

        long tempoMediana = calcularMediana(tempos);
        long memoriaMediana = calcularMediana(memorias);

        try{
            writer.write(
                entrada + "," +
                test + "," +
                estrutura + "," +
                tipo + "," +
                tempoMediana + "," +
                memoriaMediana + "\n"
            );
        }catch(IOException io){
            io.printStackTrace();
        }
    }

    protected executaCaso(List<Integer> dados, int entrada, String test){
            switch (test) {

                case "100I0R":
                    executarI100_R0_S0(dados, entrada);
                    break;

                case "50I50R0S": {
                    executarI50_R50_S0(dados, entrada);
                    break;
                }

                case "75I25R": {
                    executarI75_R25_S0(dados, entrada);
                    break;
                }

                case "50I25R25S": {
                    executarI50_R25_S25(dados, entrada);
                }

                case "50I0R50S": {
                    executarI50_R0_S50(dados, entrada);
                    break;
                }default:
                    break;
        }
    }

    protected abstract void executarI100_R0_S0(List<Integer> dados, int n);
    protected abstract void executarI50_R50_S0(List<Integer> dados, int n);
    protected abstract void executarI75_R25_S0(List<Integer> dados, int n);
    protected abstract void executarI50_R25_S25(List<Integer> dados, int n);
    protected abstract void executarI50_R0_S50(List<Integer> dados, int n);

    protected List<Integer> getDados(String dado){
        if(dado.equals("random")) return random;
        else if(dado.equals("crescente")) return crescente;
        return decresente;
    }

    protected   long calcularMediana(long[] valores) {
        Arrayssort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2];
        } else {
            return (valores[n / 2 - 1] + valores[n / 2]) / 2;
        }
    }

    protected void lerDados() throws IOException{
        String caminhoRandom = "src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv";
        String caminhoCrescente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv";
        String caminhoDecresente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv";
        String caminhoentradas = "src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntrada.csv";

        random = Files.lines(Paths.get(caminhoRandom)).map(Integer::valueOf).collect(Collectors.toList());
        crescente = Files.lines(Paths.get(caminhoCrescente)).map(Integer::valueOf).collect(Collectors.toList());
        decresente = Files.lines(Paths.get(caminhoDecresente)).map(Integer::valueOf).collect(Collectors.toList());
        entradas = Files.lines(Paths.get(caminhoentradas)).map(Integer::valueOf).collect(Collectors.toList());
    }
}
