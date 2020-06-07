package ppr;

public class WordPair {
    private final String first;

    private final String second;

    public static WordPair createPair(String first, String second) {
        return new WordPair(first, second);
    }

    public WordPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}
