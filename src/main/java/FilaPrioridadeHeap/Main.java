import java.io.*;
import java.util.*;
public class Main {

    public static void main(String[] args) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = "";

            System.out.println("test time size");

            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(" ");
                int[] s = new int[tokens.length];

                for(int i = 0; i < s.length; i++){
                    s[i] = Integer.parseInt(tokens[i]);
                }
               
                long start = System.nanoTime();
                 
                // test a ser medido
                //test1();
                
                long end = System.nanoTime();

                long time = end - start;
                
                System.out.println("test1 " + (time) + " " + tokens.length);

            }
        } catch (IOException ioe) {}

    }


}

