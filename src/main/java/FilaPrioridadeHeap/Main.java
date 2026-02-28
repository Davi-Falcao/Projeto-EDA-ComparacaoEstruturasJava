package FilaPrioridadeHeap;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = "";

            System.out.println("test time  memory size");

            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(" ");

                Runtime rt = Runtime.getRuntime();

                // limpar memoria, para tirar rúidos
                rt.gc();

                long memoryBefore = rt.totalMemory() - rt.freeMemory();
                long start = System.nanoTime();
                 
                // test a ser medido
                test1(tokens);
                
                long end = System.nanoTime();
                long memoryAfter = rt.totalMemory() - rt.freeMemory();

                long time = end - start;
                long memory = memoryAfter - memoryBefore;


                
                System.out.println("test1 " + (time) + " " + (memory) + " " + tokens.length);
            }

        } catch (IOException ioe) {}

    }

    private static void test1(String[] v){
        Heap hp = new Heap();

        for(int i = 0; i < v.length; i++){
            if(v[i].equals("I")){
                hp.add(Integer.parseInt(v[i+1]));
                i++;

            }else if(v[i].equals("R")) hp.remove();
        }
    }


}

