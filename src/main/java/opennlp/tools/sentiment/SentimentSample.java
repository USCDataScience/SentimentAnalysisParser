package opennlp.tools.sentiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class for holding text used for sentiment analysis.
 */
public class SentimentSample {

  private final String sentiment;
  private final List<String> sentence;

  /**
   * Initializes the current instance.
   *
   * @param sentiment training sentiment
   * @param sentence training sentence
   */
  public SentimentSample(String sentiment, String[] sentence) {
    if (sentiment == null) {
      throw new IllegalArgumentException("sentiment must not be null");
    }
    if (sentence == null) {
      throw new IllegalArgumentException("sentence must not be null");
    }

    this.sentiment = sentiment;
    this.sentence = Collections
        .unmodifiableList(new ArrayList<String>(Arrays.asList(sentence)));
  }

  public String getSentiment() {
    return sentiment;
  }

  public String[] getSentence() {
    return sentence.toArray(new String[0]);
  }
}
