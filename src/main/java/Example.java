import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
//import opennlp.tools.util.Span;

public class Example {

	public static void main(String[] args) throws FileNotFoundException {
		InputStream modelIn = new FileInputStream("en-token.bin");

		try {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String tokens[] = tokenizer.tokenize("An input sample sentence.");
			System.out.println(tokens);
			//Span tokenSpans[] = tokenizer.tokenizePos("An input sample sentence.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		
		

	}

}
