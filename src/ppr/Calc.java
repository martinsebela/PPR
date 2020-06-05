package ppr;

import java.io.IOException;
import java.util.Collection;

public class Calc {

    public static void main(String[] args) throws IOException {
        startCalc();
    }

    private static void startCalc() throws IOException {
        long startTime = System.currentTimeMillis();
        Collection<String[]> words = Reader.readFromFile();
        System.out.println(String.format("Read from file time: %s ms", (System.currentTimeMillis() - startTime)));
        int min = Integer.MAX_VALUE;
        startTime = System.currentTimeMillis();
        for (String[] pair : words) {
            int c = Algorithm.levenshtein(pair[0], pair[1]);
            if (c < min) {
                min = c;
            }
        }
        System.out.println(String.format("Calculation time %s ms:", (System.currentTimeMillis() - startTime)));
        System.out.println(min);
    }
}