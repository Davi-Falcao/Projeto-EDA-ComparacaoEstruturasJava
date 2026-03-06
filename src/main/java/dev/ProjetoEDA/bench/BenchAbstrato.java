public abstract class BenchAbstrato{

    protected static List<Integer> random;
    protected static List<Integer> crescente;
    protected static List<Integer> decresente;
    protected static List<Integer> entradas;

    protected static final int REPETICOES = 18;

    public abstract void run();

    protected   long calcularMediana(long[] valores) {
        Arrayssort(valores);

        int n = valores.length;
        if (n % 2 == 1) {
            return valores[n / 2];
        } else {
            return (valores[n / 2 - 1] + valores[n / 2]) / 2;
        }
    }

    protected static void lerDados() throws IOException{
        String caminhoRandom = "src/main/java/dev/ProjetoEDA/repository/entry/entradaRandomUnica.csv";
        String caminhoCrescente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaCrescenteUnica.csv";
        String caminhoDecresente = "src/main/java/dev/ProjetoEDA/repository/entry/entradaDecrescenteUnica.csv";
        String caminhoentradas = "src/main/java/dev/ProjetoEDA/repository/entry/tamanhoEntrada.csv";

        random = Files.lines(Paths.get(caminhoRandom)).map(Integer::valueOf).collect(Collectors.toList());
        crescente = Files.lines(Paths.get(caminhoCrescente)).map(Integer::valueOf).collect(Collectors.toList());
        decresente = Files.lines(Paths.get(caminhoDecresente)).map(Integer::valueOf).collect(Collectors.toList());
        entradas = Files.lines(Paths.get(caminhoentradas)).map(Integer::valueOf).collect(Collectors.toList());
    }
