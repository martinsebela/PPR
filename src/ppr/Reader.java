package ppr;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class Reader {

    private static final String FILE_NAME = "text.txt";
    private static final String EMPTY_STRING = " ";

    public static Collection<WordPair> readFromFile() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Collection<WordPair> store = new ArrayList<>();

        try (InputStream input = cl.getResourceAsStream(FILE_NAME)) {
            if (input == null) {
                throw new FileNotFoundException(String.format("File %s does't exist.", FILE_NAME));
            }
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] pair = line.split(EMPTY_STRING);
                store.add(WordPair.createPair(pair[0], pair[1]));
            }
        }
        return store;
    }
}
