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
