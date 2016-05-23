import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import opennlp.tools.sentiment.SentimentFactory;
import opennlp.tools.sentiment.SentimentME;
import opennlp.tools.sentiment.SentimentModel;
import opennlp.tools.sentiment.SentimentSample;
import opennlp.tools.sentiment.SentimentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class SentimentSampleStreamTest {
	
	@Test
	public void dataReadTest() throws IOException {
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream("test"),
		    charset);
		ObjectStream<SentimentSample> sampleStream = new SentimentSampleStream(lineStream);
		
		SentimentModel model;

		SentimentFactory factory = new SentimentFactory();
		
		try {
		  model = SentimentME.train("en", sampleStream, TrainingParameters.defaultParams(), factory);
		}
		finally {
			sampleStream.close();
		}
		
	}

}
