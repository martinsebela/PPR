package ppr;

public class Algorithm {
    
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int levenshtein(String a, String b) {
        int aLen = a.length();
        int bLen = b.length();
        if (Math.min(aLen, bLen) == 0) {
            return Math.max(aLen, bLen);
        }

        int k = 1;
        if (a.charAt(0) == b.charAt(0)) {
            k = 0;
        }

        return minimum(levenshtein(a.substring(1), b) + 1,
                levenshtein(a, b.substring(1)) + 1,
                levenshtein(a.substring(1), b.substring(1)) + k);
    }
}



