package opennlp.tools.sentiment;

import java.util.Map;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.model.BaseModel;

/**
 * Class for the basis of the Sentiment Analysis model.
 */
public class SentimentModel extends BaseModel {

  private static final String COMPONENT_NAME = "SentimentME";
  private static final String SENTIMENT_MODEL_ENTRY_NAME = "sentiment.model";

  /**
   * Initializes the Sentiment Analysis model.
   *
   * @param languageCode
   *          the code for the language of the text, e.g. "en"
   * @param sentimentModel
   *          a MaxEnt sentiment model
   * @param manifestInfoEntries
   *          additional information in the manifest
   * @param factory
   *          a Sentiment Analysis factory
   */
  public SentimentModel(String languageCode, MaxentModel sentimentModel,
      Map<String, String> manifestInfoEntries, SentimentFactory factory) {
    super(COMPONENT_NAME, languageCode, manifestInfoEntries, factory);
    artifactMap.put(SENTIMENT_MODEL_ENTRY_NAME, sentimentModel);
    checkArtifactMap();
  }

}
