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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/*
 * A class to help divide the given training dataset into train and test data (80:20)
 */
public class TestModel {

  private static String inputFile;
  private static String outputFile;

  private static final String labels = "../sentiment-examples/src/main/resources/hybrid/hybrid-labels-20";
  private static final int num = 4449;

  // private static int match;
  // private static int notMatch;

  private double trueRel;
  private double trueNotRel;
  private double falseRel;
  private double falseNotRel;

  public TestModel(String input, String output) {
    this.inputFile = input;
    this.outputFile = output;
  }

  public TestModel(String input) {
    this.inputFile = input;
  }

  public void saveLabels() throws IOException {
    Path inputFileName = Paths.get(inputFile);
    Path outputFileName = Paths.get(outputFile);

    BufferedReader reader = Files.newBufferedReader(inputFileName,
        Charset.forName("UTF-8"));
    String line;
    int i = 0;
    PrintWriter fileWriter = new PrintWriter(
        Files.newBufferedWriter(outputFileName, Charset.forName("UTF-8")));
    while ((line = reader.readLine()) != null) {
      String[] delims = line.split(" ");
      String label = delims[0];
      String name = Integer.toString(i);
      // Path output = Paths.get(outputFile, name + ".out");
      // PrintWriter fileWriter = new PrintWriter(
      // Files.newBufferedWriter(outputFileName, Charset.forName("UTF-8")));
      fileWriter.write(label + "\n");
      // fileWriter.close();
      i++;
    }
    fileWriter.close();
  }

  public void removeLabels() throws IOException {
    Path inputFileName = Paths.get(inputFile);
    Path outputFileName = Paths.get(outputFile);

    BufferedReader reader = Files.newBufferedReader(inputFileName,
        Charset.forName("UTF-8"));

    String line;
    int i = 0;
    while ((line = reader.readLine()) != null) {
      String content = line.substring(line.indexOf(" ") + 1);
      String name = Integer.toString(i);
      Path output = Paths.get(outputFile, "/" + name + ".sent");
      PrintWriter fileWriter = new PrintWriter(
          Files.newBufferedWriter(output, Charset.forName("UTF-8")));
      fileWriter.write(content);
      fileWriter.close();
      i++;

    }
  }

  public void compareLabels() throws IOException {
    File input1 = new File(inputFile);
    File input2 = new File(labels);
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;
      id = id.replace(".out", "");
      int idint = Integer.valueOf(id) + 1;
      String out1 = FileUtils.readFileToString(file1);
      File file2 = new File(labels + "/" + idint);
      String out2 = FileUtils.readFileToString(file2);
      if (out1.indexOf("NOT_RELEVANT") > -1
          && out2.indexOf("NOT_RELEVANT") > -1) {
        trueNotRel++;
      } else if (out1.indexOf("NOT_RELEVANT") <= -1
          && out2.indexOf("NOT_RELEVANT") <= -1) {
        trueRel++;
      } else if (out1.indexOf("NOT_RELEVANT") > -1 // not rel output
          && out2.indexOf("NOT_RELEVANT") <= -1) { // relevant actually
        falseNotRel++;
      } else if (out1.indexOf("NOT_RELEVANT") <= -1
          && out2.indexOf("NOT_RELEVANT") > -1) {
        falseRel++;
      }
    }

  }

  public double getTrueRel() {
    return trueRel;
  }

  public void setTrueRel(int trueRel) {
    this.trueRel = trueRel;
  }

  public double getFalseRel() {
    return falseRel;
  }

  public void setFalseRel(int falseRel) {
    this.falseRel = falseRel;
  }

  /*
   * ../sentiment-examples/src/main/resources/hybrid/hybrid-test20-out
   */
  public static void main(String[] args) throws IOException {

    String fileName = args[0];
    //String outputName = args[1];

    TestModel test = new TestModel(fileName);//, outputName);
    // test.saveLabels();
    // test.removeLabels();
    test.compareLabels();
    System.out.println("TNR: " + test.trueNotRel);
    System.out.println("TR: " + test.getTrueRel());
    System.out.println("FNR: " + test.falseNotRel);
    System.out.println("FR: " + test.getFalseRel());
    // System.out.println("MATCH: " + match);
    // System.out.println("NOT MATCH: " + notMatch);

  }

}
