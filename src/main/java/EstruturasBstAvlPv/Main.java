package EstruturasBstAvlPv;

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


                
                System.out.println("PriorityQueue crescente 10^6 75%Insertion 25%Remove " + (obsTime[14]) + " " + (obsMemory[14]));
            }

        } catch (IOException ioe) {}

    }

    private static void test1(String[] v){
        PriorityQueue hp = new PriorityQueue();

        for(int i = 0; i < v.length; i++){
            if(v[i].equals("I")){
                hp.add(Integer.parseInt(v[i+1]));
                i++;

            }else if(v[i].equals("R")) hp.remove();
        }
    }


}

