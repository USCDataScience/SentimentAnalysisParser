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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;

import opennlp.tools.sentiment.SentimentFactory;
import opennlp.tools.sentiment.SentimentME;
import opennlp.tools.sentiment.SentimentModel;
import opennlp.tools.sentiment.SentimentSample;
import opennlp.tools.sentiment.SentimentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.InputStreamFactory;

/**
 * Class for performing tests on the Sentiment Analysis model training.
 */
public class SentimentSampleStreamTest {

  /**
   * Reads the data from the given file to perform the test.
   */
  @Test
  public void dataReadTest() throws IOException {
    Charset charset = Charset.forName("UTF-8");
    InputStreamFactory isf = new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return new FileInputStream("../sentiment-examples/src/main/resources/edu/usc/irds/sentiment/train/categorical-stanford-train");
            }
        };

    ObjectStream<String> lineStream = new PlainTextByLineStream(isf, charset);
    ObjectStream<SentimentSample> sampleStream = new SentimentSampleStream(
        lineStream);

    SentimentModel model;

    SentimentFactory factory = new SentimentFactory();

    try {
      model = SentimentME.train("en", sampleStream,
          TrainingParameters.defaultParams(), factory);
    } finally {
      sampleStream.close();
    }
    
  }

}
