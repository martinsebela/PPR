package ppr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

public class Reader {

    private static final String FILE_NAME = "text.txt";

    public static Collection<String[]> readFromFile() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Collection<String[]> store = new ArrayList<>();

        try (InputStream input = cl.getResourceAsStream(FILE_NAME)) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                store.add(line.split(" "));
            }
        }
        return store;
    }
}
