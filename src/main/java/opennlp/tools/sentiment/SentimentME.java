package opennlp.tools.sentiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Class for creating a maximum-entropy-based Sentiment Analysis model.
 */
public class SentimentME {

  public static final String OTHER = "other";
  public static final String START = "start";
  public static final String CONTINUE = "cont";

  /**
   * Trains a Sentiment Analysis model.
   *
   * @param languageCode
   *          the code for the language of the text, e.g. "en"
   * @param samples
   *          the sentiment samples to be used
   * @param trainParams
   *          parameters for training
   * @param factory
   *          a Sentiment Analysis factory
   * @return a Sentiment Analysis model
   */
  public static SentimentModel train(String languageCode,
      ObjectStream<SentimentSample> samples, TrainingParameters trainParams,
      SentimentFactory factory) throws IOException {

    Map<String, String> entries = new HashMap<String, String>();

    MaxentModel sentimentModel = null;

    SequenceClassificationModel<String> seqModel = null;

    TrainerType trainerType = TrainerFactory
        .getTrainerType(trainParams.getSettings());

    ObjectStream<Event> eventStream = new SentimentEventStream(samples,
        factory.createContextGenerator());

    EventTrainer trainer = TrainerFactory
        .getEventTrainer(trainParams.getSettings(), entries);
    sentimentModel = trainer.train(eventStream);

    Map<String, String> manifestInfoEntries = new HashMap<String, String>();

    return new SentimentModel(languageCode, sentimentModel, manifestInfoEntries,
        factory);

  }

}
