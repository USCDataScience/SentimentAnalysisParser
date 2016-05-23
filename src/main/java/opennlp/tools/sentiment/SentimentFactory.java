package opennlp.tools.sentiment;

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceCodec;

public class SentimentFactory extends BaseToolFactory{

	@Override
	public void validateArtifactMap() throws InvalidFormatException {
		// nothing to validate
	}

	public SentimentContextGenerator createContextGenerator() {
		return null;
		//return new DefaultSentimentContextGenerator();
	}

	public SequenceCodec<String> createSequenceCodec() {
		return null;
	}

	
}
