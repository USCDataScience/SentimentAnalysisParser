import java.io.*;
import java.util.Scanner;
import java.io.File;

public class SentimentCountCateg {
	
	private int angry;
	private int sad;
	private int neutral;
	private int like;
	private int love;
	
	public SentimentCountCateg() {
		this.angry = 0;
		this.sad = 0;
		this.neutral = 0;
		this.like = 0;
		this.love = 0;
	}
	
	public void counter(File file) throws FileNotFoundException {
		String content = new Scanner(file).useDelimiter("\\Z").next();
		for (int i = 0; i < content.length() - 8; i++) {
			if (content.substring(i, i+5).equalsIgnoreCase("angry")) {
				this.angry++;
			}
			else if (content.substring(i, i+3).equalsIgnoreCase("sad")) {
				this.sad++;
			}
			else if (content.substring(i, i+7).equalsIgnoreCase("neutral")) {
				this.neutral++;
			}
			else if (content.substring(i, i+4).equalsIgnoreCase("like")) {
				this.like++;
			}
			else if (content.substring(i, i+4).equalsIgnoreCase("love")) {
				this.love++;
			}
		}
	}

		
	public static void main(String args[]) throws FileNotFoundException {
		SentimentCountCateg sentCount = new SentimentCountCateg();
		File file = new File("../reviews/review_categ_dataset");
		sentCount.counter(file);
		System.out.println("Angry: " + sentCount.angry);
		System.out.println("Sad: " + sentCount.sad);
		System.out.println("Neutral: " + sentCount.neutral);
		System.out.println("Like: " + sentCount.like);
		System.out.println("Love: " + sentCount.love);
		
	}
}
