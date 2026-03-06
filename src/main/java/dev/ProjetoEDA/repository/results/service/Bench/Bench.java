package dev.ProjetoEDA.service.Bench;

public abstract class Bench {

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decresente;
    protected static  List<Integer> entradas;


    // ---------------- 10^3 RANDOM ----------------

    public abstract void n1e3Random50Insert25Remove25Search();
    public abstract void n1e3Random75Insert25Remove();
    public abstract void n1e3Random75Insert25Search();
    public abstract void n1e3Random100Insert();

    // ---------------- 10^3 CRESCENTE ----------------

    public abstract void n1e3Crescente50Insert25Remove25Search();
    public abstract void n1e3Crescente75Insert25Remove();
    public abstract void n1e3Crescente75Insert25Search();
    public abstract void n1e3Crescente100Insert();

    // ---------------- 10^3 DECRESCENTE ----------------

    public abstract void n1e3Decrescente50Insert25Remove25Search();
    public abstract void n1e3Decrescente75Insert25Remove();
    public abstract void n1e3Decrescente75Insert25Search();
    public abstract void n1e3Decrescente100Insert();

    // ---------------- 10^6 RANDOM ----------------

    public abstract void n1e6Random50Insert25Remove25Search();
    public abstract void n1e6Random75Insert25Remove();
    public abstract void n1e6Random75Insert25Search();
    public abstract void n1e6Random100Insert();

    // ---------------- 10^6 CRESCENTE ----------------

    public abstract void n1e6Crescente50Insert25Remove25Search();
    public abstract void n1e6Crescente75Insert25Remove();
    public abstract void n1e6Crescente75Insert25Search();
    public abstract void n1e6Crescente100Insert();

    // ---------------- 10^6 DECRESCENTE ----------------

    public abstract void n1e6Decrescente50Insert25Remove25Search();
    public abstract void n1e6Decrescente75Insert25Remove();
    public abstract void n1e6Decrescente75Insert25Search();
    public abstract void n1e6Decrescente100Insert();

    protected abstract void experimento(int entrada, String test, String tipo, BufferedWriter writer);

    protected  static void lerDados() throws IOException{
        String caminhoRandom = "data/entradas/dados/random.csv";
        String caminhoCrescente = "data/entradas/dados/crescente.csv";
        String caminhoDecresente = "data/entradas/dados/decresente.csv";
        String caminhoentradas = "data/entradas/tamanho/entradas.csv";

        random = Files.lines(Paths.get(caminhoRandom)).map(Integer::valueOf).collect(Collectors.toList());
        crescente = Files.lines(Paths.get(caminhoCrescente)).map(Integer::valueOf).collect(Collectors.toList());
        decresente = Files.lines(Paths.get(caminhoDecresente)).map(Integer::valueOf).collect(Collectors.toList());
        entradas = Files.lines(Paths.get(caminhoentradas)).map(Integer::valueOf).collect(Collectors.toList());
    }

    protected static long calcularMediana(long[] valores) {
        Arrays.sort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2]; 
        } else {
            long mediana = (valores[n / 2 - 1] + valores[n / 2]) / 2;  
            return mediana;
        }
    }


}