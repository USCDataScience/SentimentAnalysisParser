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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/*
 * A class to build training dataset for the hybrid model.
 */
public class HybridModel {

  private static String stanfordFile;
  private static String htReviewFile;
  private static String geotFile;
  private static String adFile;
  private static String htLeadGen;

  private static String outputFile;
  private PrintWriter outputStream;

  public HybridModel(String stanford, String ht, String geot, String ad,
      String out, String htlg) throws IOException {
    this.stanfordFile = stanford;
    this.htReviewFile = ht;
    this.geotFile = geot;
    this.adFile = ad;
    this.htLeadGen = htlg;
    this.outputFile = out;
    Path outputFileName = Paths.get(outputFile);
    Charset encoding = Charset.forName("UTF-8");
    outputStream = new PrintWriter(Files.newBufferedWriter(outputFileName, encoding));
  }

  public void parse() throws IOException {
    File input1 = new File(stanfordFile);
    Path outputFileName = Paths.get(outputFile);
    Charset encoding = Charset.forName("UTF-8");
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out")) {
        continue;
      }
      File file2 = new File(htReviewFile + "/" + id);
      File file3 = new File(geotFile + "/" + id);
      String adFileName = id.replace(".out", ".sent");
      File file4 = new File(adFile + "/" + adFileName);
      File ht_lg = new File(htLeadGen + "/" + id);
      String htLabel = FileUtils.readFileToString(ht_lg);
      Map<String, String> stanfordMap = new HashMap<String, String>();
      Map<String, String> htMap = new HashMap<String, String>();
      List<String> lines1 = FileUtils.readLines(file1, "utf-8");
      List<String> lines2 = FileUtils.readLines(file2, "utf-8");
      List<String> lines3 = FileUtils.readLines(file3, "utf-8");
      for (int i = 0; i < lines1.size(); i++) {
        String[] s1 = lines1.get(i).split(": ");
        String[] s2 = lines2.get(i).split(": ");
        stanfordMap.put(s1[0], s1[1]);
        htMap.put(s2[0], s2[1]);
      }
      if (htLabel.equals("NOT_RELEVANT")) {
        outputStream.write("NOT_RELEVANT ");
      } else {
        outputStream.write("RELEVANT ");
      }
      if (stanfordMap.get("Sentiment").equals("love")) {
        outputStream.write("yes; ");
      } else {
        outputStream.write("no; ");
      }
      if (htMap.get("Sentiment").equals("love")) {
        outputStream.write("yes; ");
      } else {
        outputStream.write("no; ");
      }
      this.getGeolocation(lines3);
      this.findNegation(file4);
    }
    outputStream.close();
  }

  private void getGeolocation(List<String> lines3) { //Optional_NAME1
    Map<String, String> geotMap = new HashMap<String, String>();
    StringBuffer stream = new StringBuffer();
    for (int i = 0; i < lines3.size(); i++) {
      String[] s1 = lines3.get(i).split(": ");
      geotMap.put(s1[0], s1[1]);
    }
    if (geotMap.containsKey("Geographic_NAME")) {
      stream.append(geotMap.get("Geographic_NAME") + ", ");
      for (int k = 1; k <= 5; k++) {
        StringBuffer fieldName = new StringBuffer();
        fieldName.append("Optional_NAME").append(Integer.toString(k));
        if (geotMap.containsKey(fieldName.toString())) {
          //System.out.println("helloo");
          stream.append(geotMap.get(fieldName.toString()) + ", ");
        }
      }
    } 
    else {
      stream.append("NONE, ");
    }
    
    String outp = stream.toString();
    if (outp.charAt(outp.length()-2) == ',') {
      outputStream.write(outp.substring(0, outp.length()-2) + "; ");
    }
  }
  
  private void findNegation(File file4) throws IOException {
    String out1 = FileUtils.readFileToString(file4);
    String[] adText = out1.split("\\s+");
    boolean negation = false;
    for (int i = 0; i < adText.length; i++) {
      if (adText[i].toLowerCase().equals("no") || adText[i].toLowerCase().equals("don't") || adText[i].toLowerCase().equals("not")) {
        negation = true;
        break;
      } else {
        negation = false;
      }
    } if (negation) {
      outputStream.write("yes\n");
    } else {
      outputStream.write("no\n");
    }
  }

  public static void main(String[] args) throws IOException {

    /*
     * ../sentiment-examples/src/main/resources/hybrid/ht-lg-stanford-out
     * ../sentiment-examples/src/main/resources/hybrid/ht-lg-htcat-out
     * ../../geot-out2
     * ../sentiment-examples/src/main/resources/hybrid/ht-lg-train-ads
     * ../sentiment-examples/src/main/resources/train/hybrid-train-v2
     * ../sentiment-examples/src/main/resources/hybrid/ht-lg-labels
     */

    HybridModel test = new HybridModel(args[0], args[1], args[2], args[3],
        args[4], args[5]);
    test.parse();

  }

}
