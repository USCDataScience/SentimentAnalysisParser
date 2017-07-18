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

import org.apache.commons.io.FileUtils;

/*
 * A class that analyses the outputs of running different models on the given data and helps make correlation between the results
 */
public class OutputAnalyzer {

  private String model = "";

  private static int positiveRelevant = 0;
  private static int positiveNotRelevant = 0;
  private static int negativeRelevant = 0;
  private static int negativeNotRelevant = 0;

  public static int angryRelevant = 0;
  public static int sadRelevant = 0;
  public static int neutralRelevant = 0;
  public static int likeRelevant = 0;
  public static int loveRelevant = 0;
  public static int angryNotRelevant = 0;
  public static int sadNotRelevant = 0;
  public static int neutralNotRelevant = 0;
  public static int likeNotRelevant = 0;
  public static int loveNotRelevant = 0;

  public OutputAnalyzer(String m) {
    this.model = m;
  }

  public void countRelevant(String path1) throws IOException {
    int rel = 0;
    int notRel = 0;
    File input = new File(path1);
    for (File file : input.listFiles()) {
      String out1 = FileUtils.readFileToString(file);
      if (out1.indexOf("NOT_RELEVANT") > -1) {
        notRel++;
      } else {
        rel++;
      }
    }
    System.out.println("RELEVANT\t" + rel);
    System.out.println("NOT RELEVANT\t" + notRel);
  }

  public void parse(String pathName1, String pathName2) throws IOException {

    File input1 = new File(pathName1);
    File input2 = new File(pathName2);
    boolean relevant = false;
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;

      String out1 = FileUtils.readFileToString(file1);
      if (out1.indexOf("NOT_RELEVANT") > -1) {
        relevant = false;
      } else {
        relevant = true;
      }
      File file2 = new File(pathName2 + "/" + id);
      String out2 = FileUtils.readFileToString(file2);
      if ((out2.indexOf("positive") != -1) && relevant == true) {
        positiveRelevant++;
      } else if ((out2.indexOf("positive") != -1) && relevant == false) {
        positiveNotRelevant++;
      } else if ((out2.indexOf("negative") != -1) && relevant == true) {
        negativeRelevant++;
      } else if ((out2.indexOf("negative") != -1) && relevant == false) {
        negativeNotRelevant++;
      }
    }

  }

  public void parseCateg(String pathName1, String pathName2)
      throws IOException {

    File input1 = new File(pathName1);
    File input2 = new File(pathName2);
    boolean relevant = false;
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;

      String out1 = FileUtils.readFileToString(file1);
      if (out1.indexOf("NOT_RELEVANT") > -1) {
        relevant = false;
      } else {
        relevant = true;
      }
      File file2 = new File(pathName2 + "/" + id);
      String out2 = FileUtils.readFileToString(file2);
      if ((out2.indexOf("angry") != -1) && relevant == true) {
        angryRelevant++;
      } else if ((out2.indexOf("angry") != -1) && relevant == false) {
        angryNotRelevant++;
      } else if ((out2.indexOf("sad") != -1) && relevant == true) {
        sadRelevant++;
      } else if ((out2.indexOf("sad") != -1) && relevant == false) {
        sadNotRelevant++;
      } else if ((out2.indexOf("neutral") != -1) && relevant == true) {
        neutralRelevant++;
      } else if ((out2.indexOf("neutral") != -1) && relevant == false) {
        neutralNotRelevant++;
      } else if ((out2.indexOf("like") != -1) && relevant == true) {
        likeRelevant++;
      } else if ((out2.indexOf("like") != -1) && relevant == false) {
        likeNotRelevant++;
      } else if ((out2.indexOf("love") != -1) && relevant == true) {
        loveRelevant++;
      } else if ((out2.indexOf("sad") != -1) && relevant == false) {
        loveNotRelevant++;
      }
    }

  }

  public void parseIEEE(String pathName1) throws IOException {

    File input = new File(pathName1);
    int networks = 0, software = 0, system = 0, management = 0, equipment = 0,
        wireless = 0, security = 0, design = 0, data = 0, radio = 0, devices = 0, circuit=0, interf = 0, metadata = 0;

    for (File file : input.listFiles()) {
      String id = file.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;

      String out = FileUtils.readFileToString(file);
      if ((out.indexOf("wireless_network") != -1)) {
        networks++;
      } else if ((out.indexOf("software") != -1)) {
        software++;
      } else if ((out.indexOf("devices") != -1)) {
        devices++;
      } else if ((out.indexOf("interfaces") != -1)) {
        interf++;
      } else if ((out.indexOf("systems") != -1)) {
        system++;
      } else if ((out.indexOf("circuits") != -1)) {
        circuit++;
      } else if ((out.indexOf("management") != -1)) {
        management++;
      } else if ((out.indexOf("equipment") != -1)) {
        equipment++;
      } else if ((out.indexOf("security") != -1)) {
        security++;
      } else if ((out.indexOf("design") != -1)) {
        design++;
      } else if ((out.indexOf("data") != -1)) {
        data++;
      } else if ((out.indexOf("radioactive") != -1)) {
        radio++;
      }
    }
    System.out.println("Wireless Networks\t" + networks);
    System.out.println("Software\t" + software);
    System.out.println("Devices\t" + devices);
    System.out.println("Interfaces\t" + interf);
    System.out.println("Systems\t" + system);
    System.out.println("Circuits\t" + circuit);
    System.out.println("Management\t" + management);
    System.out.println("Equipment\t" + equipment);
    System.out.println("Security\t" + security);
    System.out.println("Design\t" + design);
    System.out.println("Data\t" + data);
    System.out.println("Radioactive\t" + radio);
  }
  

  public static void main(String[] args) throws IOException {
    /*
     * example: binary ../sentiment-examples/ht-lead-gen-out
     * ../sentiment-examples/ht-leadg-bin-out
     * 
     * binary --> binary model labels categorical --> categorical model labels
     */
    OutputAnalyzer analyze = new OutputAnalyzer(args[0]);

    // analyze.parse(args[1], args[2]);
    // System.out.println("POS REL: " + analyze.positiveRelevant);
    // System.out.println("POS NOT_REL: " + analyze.positiveNotRelevant);
    // System.out.println("NEG REL: " + analyze.negativeRelevant);
    // System.out.println("NEG NOT_REL: " + analyze.negativeNotRelevant);

    System.out.println();

//    analyze.parseCateg(args[1], args[2]);
//    System.out.println("ANGRY REL: " + analyze.angryRelevant);
//    System.out.println("ANGRY NOT_REL: " + analyze.angryNotRelevant);
//    System.out.println("SAD REL: " + analyze.sadRelevant);
//    System.out.println("SAD NOT_REL: " + analyze.sadNotRelevant);
//    System.out.println("NEUTRAL REL: " + analyze.neutralRelevant);
//    System.out.println("NEUTRAL NOT_REL: " + analyze.neutralNotRelevant);
//    System.out.println("LIKE REL: " + analyze.likeRelevant);
//    System.out.println("LIKE NOT_REL: " + analyze.likeNotRelevant);
//    System.out.println("LOVE REL: " + analyze.loveRelevant);
//    System.out.println("LOVE NOT_REL: " + analyze.loveNotRelevant);
    //analyze.parseIEEE(args[1]);
    
    analyze.countRelevant(args[0]);

    // analyze.countRelevant(args[1]);
  }

}
