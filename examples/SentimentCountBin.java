import java.io.*;
import java.util.Scanner;
import java.io.File;

public class SentimentCountBin {
	
	private int negative;
	private int positive;
	
	public SentimentCountBin() {
		this.negative = 0;
		this.positive = 0;
	}
	
	public void counter(File file) throws FileNotFoundException {
		String content = new Scanner(file).useDelimiter("\\Z").next();
		for (int i = 0; i < content.length() - 8; i++) {
			if (content.substring(i, i+8).equalsIgnoreCase("negative")) {
				this.negative++;
			}
			else if (content.substring(i, i+8).equalsIgnoreCase("positive")) {
				this.positive++;
			}
		}
	}

		
	public static void main(String args[]) throws FileNotFoundException {
		SentimentCountBin sentCount = new SentimentCountBin();
		File file = new File("../reviews/review_bin_dataset");
		sentCount.counter(file);
		System.out.println("Negative: " + sentCount.negative);
		System.out.println("Positive: " + sentCount.positive);
		
	}
}
