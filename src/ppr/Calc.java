package ppr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calc {
	
	public static void main(String[] args) throws IOException {		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		List<String[]> words = new ArrayList<String[]>();
		long startTime = System.currentTimeMillis();
		try(InputStream input = cl.getResourceAsStream("text.txt")) {
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				words.add(line.split(" "));
			}
		}
		System.out.println("read from file time: " + (System.currentTimeMillis() - startTime) + "ms");
		int min = Integer.MAX_VALUE;
		startTime = System.currentTimeMillis();
		for (String[] pair : words) {
			int c = calculate(pair[0],pair[1]);
			if (c<min) {
				min = c;
			}
		}
		System.out.println("calculation time: " + (System.currentTimeMillis() - startTime) + "ms");
		System.out.println(min);
	}
	
	static int calculate(String x, String y) {
        if (x.isEmpty()) {
            return y.length();
        }
 
        if (y.isEmpty()) {
            return x.length();
        } 
 
        int substitution = calculate(x.substring(1), y.substring(1)) 
         + costOfSubstitution(x.charAt(0), y.charAt(0));
        int insertion = calculate(x, y.substring(1)) + 1;
        int deletion = calculate(x.substring(1), y) + 1;
 
        return min(substitution, insertion, deletion);
    }
	
	public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
	
	public static int min(int... numbers) {
        return Arrays.stream(numbers)
          .min().orElse(Integer.MAX_VALUE);
    }
}