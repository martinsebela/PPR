package ppr;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class Calc {

    private static final AtomicInteger minValue = new AtomicInteger(Integer.MAX_VALUE);

    public static void main(String[] args) throws IOException {
        startCalc();
    }

    private static void startCalc() throws IOException {
        long startTime = System.currentTimeMillis();
        Collection<WordPair> words = Reader.readFromFile();
        System.out.println(String.format("Read from file time: %s ms", (System.currentTimeMillis() - startTime)));

        startTime = System.currentTimeMillis();
        words.forEach(pair -> {
            int c = Algorithm.levenshtein(pair.getFirst(), pair.getSecond());
            if (c < minValue.get()) {
                minValue.set(c);
            }
        });

        System.out.println(String.format("Calculation time %s ms:", (System.currentTimeMillis() - startTime)));
        System.out.println(minValue);
    }
}