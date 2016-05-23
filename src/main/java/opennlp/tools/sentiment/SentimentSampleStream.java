package opennlp.tools.sentiment;

import java.io.IOException;

import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

public class SentimentSampleStream
    extends FilterObjectStream<String, SentimentSample> {

  public SentimentSampleStream(ObjectStream<String> samples) {
    super(samples);
  }

  @Override
  public SentimentSample read() throws IOException {
    String sentence = samples.read();

    if (sentence != null) {

      // Whitespace tokenize entire string
      String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(sentence);

      SentimentSample sample;

      if (tokens.length > 1) {
        String sentiment = tokens[0];
        String sentTokens[] = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, sentTokens, 0, tokens.length - 1);

        sample = new SentimentSample(sentiment, sentTokens);
      } else {
        throw new IOException(
            "Empty lines, or lines with only a category string are not allowed!");
      }

      return sample;
    }

    return null;
  }

}
