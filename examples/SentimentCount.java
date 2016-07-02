import java.io.*;
import java.util.Scanner;
import java.io.File;

public class SentimentCount {
	
	private int pos;
	private int neg;
	
	public SentimentCount() {
		this.pos = 0;
		this.neg = 0;
	}
	
	public void counter(File file) throws FileNotFoundException {
		String content = new Scanner(file).useDelimiter("\\Z").next();
		for (int i = 0; i < content.length() - 8; i++) {
			if (content.substring(i, i+8).equalsIgnoreCase("positive")) {
				this.pos++;
			}
			else if (content.substring(i, i+8).equalsIgnoreCase("negative")) {
				this.neg++;
			}
		}
	}

		
	public static void main(String args[]) throws FileNotFoundException {
		SentimentCount sentCount = new SentimentCount();
		File file = new File("./out1");
		sentCount.counter(file);
		System.out.println("Positive: " + sentCount.pos);
		System.out.println("Negative: " + sentCount.neg);
	}
}
