package FilaPrioridadeHeap;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = "";

            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(" ");

                Runtime rt = Runtime.getRuntime();

                // limpar memoria, para tirar rúidos
                rt.gc();

                long[] obsTime = new long[30];
                long[] obsMemory = new long[30];

                for(int i = 0; i < 30; i++){

                    long memoryBefore = rt.totalMemory() - rt.freeMemory();
                    long start = System.nanoTime();
                 
                    // test a ser medido
                    test1(tokens);
                
                    long end = System.nanoTime();
                    long memoryAfter = rt.totalMemory() - rt.freeMemory();

                    long time = end - start;
                    long memory = memoryAfter - memoryBefore;

                    obsTime[i] = time;
                    obsMemory[i] = memory;


                }

                Arrays.sort(obsTime);
                Arrays.sort(obsMemory);


                
                System.out.println("heap50%Insertion50%Remove " + tokens.length + " " + (obsTime[14]));
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

