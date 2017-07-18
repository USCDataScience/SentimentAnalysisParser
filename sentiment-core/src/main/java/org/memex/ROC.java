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

package org.memex;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/*
 * A class to help get data to build the ROC curve for our 80:20 tests for the HT-LG model.
 */
public class ROC {

  private static final String correctLabels = "../sentiment-examples/src/main/resources/hybrid/hybrid-labels-20";
  private static final String predictResults = "../sentiment-examples/src/main/resources/hybrid/hybrid-test20-out";

  private static String outputFile = "../sentiment-examples/src/main/resources/hybrid_roc_data.tsv";
  private PrintWriter outputStream;

  private final int SIZE = 4449;
  private TprFpr[] tprFpr;

  private class TprFpr {
    private double tpr = 0.0;
    private double fpr = 0.0;

    public TprFpr(double fpr, double tpr) {
      this.tpr = tpr;
      this.fpr = fpr;
    }

    public double getTpr() {
      return this.tpr;
    }

    public double getFpr() {
      return this.fpr;
    }

  }

  public ROC() throws IOException {
    tprFpr = new TprFpr[SIZE];
    Path outputFileName = Paths.get(outputFile);
    Charset encoding = Charset.forName("UTF-8");
    outputStream = new PrintWriter(
        Files.newBufferedWriter(outputFileName, encoding));
  }

  public void fillArray() throws IOException {
    outputStream.write("fpr\ttpr\n");
    double trueRel = 0.0;
    double trueNotRel = 0.0;
    double falseRel = 0.0;
    double falseNotRel = 0.0;
    File input1 = new File(predictResults);
    File input2 = new File(correctLabels);
    int sampleNum = 0;
    TestModel test = new TestModel(predictResults);
    test.compareLabels();
    double totalTrueRel = test.getTrueRel();
    double totalFalseRel = test.getFalseRel();
    String result = ""; // TN, TP, FN, FP
    double tpr = 0.0, fpr = 0.0;
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;
      id = id.replace(".out", "");
      int idint = Integer.valueOf(id) + 1;
      String out1 = FileUtils.readFileToString(file1);
      File file2 = new File(correctLabels + "/" + idint);
      String out2 = FileUtils.readFileToString(file2);
      if (out1.indexOf("NOT_RELEVANT") == -1
          && out2.indexOf("NOT_RELEVANT") == -1) { // TP ==> TR
        result = "TP";
        trueRel++;
        tpr = trueRel / totalTrueRel;
        fpr = falseRel / totalFalseRel;
      } else if (out1.indexOf("NOT_RELEVANT") == -1
          && out2.indexOf("NOT_RELEVANT") != -1) { // FP ==> FR
        result = "FP";
        falseRel++;
        fpr = falseRel / totalFalseRel;
        tpr = trueRel / totalTrueRel;
      }
      tprFpr[sampleNum] = new TprFpr(fpr, tpr);
      outputStream.write(tprFpr[sampleNum].getFpr() + "\t"
          + tprFpr[sampleNum].getTpr() + "\n");
      sampleNum++;
    }
    outputStream.close();
  }

  public static void main(String[] args) throws IOException {
    ROC roc = new ROC();
    roc.fillArray();
  }

}
