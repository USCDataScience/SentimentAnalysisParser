package opennlp.tools.sentiment;

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceCodec;

/**
 * Class for creating sentiment factories for training.
 */
public class SentimentFactory extends BaseToolFactory {

  /**
   * Validates the artifact map --> nothing to validate.
   */
  @Override
  public void validateArtifactMap() throws InvalidFormatException {
    // nothing to validate
  }

  /**
   * Creates a new context generator.
   *
   * @return a context generator for Sentiment Analysis
   */
  public SentimentContextGenerator createContextGenerator() {
    return new SentimentContextGenerator();
  }

}
