package ppr;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CalcParallel {

    private static final AtomicInteger minValue = new AtomicInteger(Integer.MAX_VALUE);
    private static final int THREAD_NUMBER = 8;

    public static void main(String[] args) {
        startCalcParallel();
    }

    private static void startCalcParallel() {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        try {
            Collection<String[]> words = Reader.readFromFile();
            System.out.println(String.format("Read from file time: %s ms", (System.currentTimeMillis() - startTime)));
            startTime = System.currentTimeMillis();

            for (String[] pair : words) {
                executor.submit(() -> {
                    int c = Algorithm.levenshtein(pair[0], pair[1]);
                    if (c < minValue.get()) {
                        minValue.set(c);
                    }
                });
            }
            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                System.out.println(String.format("Calculation time: %s ms", (System.currentTimeMillis() - startTime)));
                System.out.println(minValue.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Cannot load resource. " + e.getMessage());
        }
    }
}