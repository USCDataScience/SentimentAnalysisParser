/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.Span;
import opennlp.tools.util.eval.Evaluator;
import opennlp.tools.util.eval.FMeasure;

public class SentimentEvaluator extends Evaluator<SentimentSample> {

  private FMeasure fmeasure = new FMeasure();

  private SentimentME sentiment;

  @Override
  protected SentimentSample processSample(SentimentSample reference) {

    Span predictedNames[] = sentiment.find(reference.getSentence());
    // Span references[] = reference.getNames();

    // OPENNLP-396 When evaluating with a file in the old format
    // the type of the span is null, but must be set to default to match
    // the output of the name finder.
    // for (int i = 0; i < references.length; i++) {
    // if (references[i].getType() == null) {
    // references[i] = new Span(references[i].getStart(),
    // references[i].getEnd(), "default");
    // }
    // }

    // fmeasure.updateScores(references, predictedNames);

    // return new SentimentSample(predictedNames, reference.getSentence());
    return null;
  }

}
