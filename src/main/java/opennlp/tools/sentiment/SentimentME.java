/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.sentiment;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameContextGenerator;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceCodec;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.AdditionalContextFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

/**
 * Class for creating a maximum-entropy-based Sentiment Analysis model.
 */
public class SentimentME {

  public static final String OTHER = "other";
  public static final String START = "start";
  public static final String CONTINUE = "cont";
  public static final int DEFAULT_BEAM_SIZE = 3;

  private static String[][] EMPTY = new String[0][0];

  protected SentimentContextGenerator contextGenerator;
  private AdditionalContextFeatureGenerator additionalContextFeatureGenerator = new AdditionalContextFeatureGenerator();
  private Sequence bestSequence;
  protected SequenceClassificationModel<String> model;
  private SequenceValidator<String> sequenceValidator;
  private SentimentFactory factory;
  private MaxentModel maxentModel;
  private SequenceCodec<String> seqCodec = new BioCodec();

  public SentimentME(SentimentModel sentModel) {

    this.model = sentModel.getSentimentModel();
    maxentModel = sentModel.getMaxentModel();

    factory = sentModel.getFactory();

    contextGenerator = factory.createContextGenerator();
  }

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

  public String predict(String sentence) {
    String[] tokens = factory.getTokenizer().tokenize(sentence);

    double prob[] = probabilities(tokens);
    String sentiment = getBestSentiment(prob);

    return sentiment;
  }

  public String getBestSentiment(double[] outcome) {
    return maxentModel.getBestOutcome(outcome);
  }

  /**
   * Categorizes the given text.
   *
   * @param text
   *          the text to categorize
   */
  public double[] probabilities(String text[]) {
    return maxentModel.eval(contextGenerator.getContext(text));
  }

  public Span[] predict2(String[] tokens) {
    return predict2(tokens, EMPTY);
  }

  public Span[] predict2(String[] tokens, String[][] additionalContext) {

    additionalContextFeatureGenerator.setCurrentContext(additionalContext);

    bestSequence = model.bestSequence(tokens, additionalContext,
        contextGenerator, sequenceValidator);

    List<String> c = bestSequence.getOutcomes();

    Span[] spans = seqCodec.decode(c);
    return spans;
  }

}
