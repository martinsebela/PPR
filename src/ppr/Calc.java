package ppr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
			int c = lev(pair[0],pair[1]);
			if (c<min) {
				min = c;
			}
		}
		System.out.println("calculation time: " + (System.currentTimeMillis() - startTime) + "ms");
		System.out.println(min);
	}
	
	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
	
	private static int lev(String a, String b) {
		int aLen = a.length();
		int bLen = b.length();
        if (Math.min(aLen, bLen) == 0) {
        	return Math.max(aLen, bLen);
        }
        
        int k = 1;
        if (a.charAt(0) == b.charAt(0)) {
        	k = 0;
        }
 
        return minimum(lev(a.substring(1), b) + 1,
        		lev(a, b.substring(1)) + 1, 
        		lev(a.substring(1), b.substring(1)) + k);
    }
}