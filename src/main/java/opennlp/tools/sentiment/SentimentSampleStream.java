package opennlp.tools.sentiment;

import java.io.IOException;

import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * Class for converting Strings through Data Stream to SentimentSample using
 * tokenised text.
 */
public class SentimentSampleStream
    extends FilterObjectStream<String, SentimentSample> {

  /**
   * Initializes the sample stream.
   *
   * @param samples
   *          the sentiment samples to be used
   */
  public SentimentSampleStream(ObjectStream<String> samples) {
    super(samples);
  }

  /**
   * Reads the text
   *
   * @return a ready-to-be-trained SentimentSample object
   */
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
