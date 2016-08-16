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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReviewParser {

  private String model = "";

  private String binary = "binary";
  private String categ = "categorical";
  
  public static int negative = 0;
  public static int positive = 0;
  
  public static int angry = 0;
  public static int sad = 0;
  public static int neutral = 0;
  public static int like = 0;
  public static int love = 0;

  private final String veryNeg = "angry";// "very_negative";
  private final String neg = "sad";// "negative";
  private final String neu = "neutral";// "wow";
  private final String pos = "like";// "positive";
  private final String veryPos = "love";// "very_positive";

  StringBuilder reviews = null;

  public ReviewParser(String m) {
    this.model = m;
    this.reviews = new StringBuilder();
  }

  public void parse(String fileName) throws IOException {

    File input = new File(fileName);
    if (input.isDirectory()) {
      for (File file : input.listFiles()) {
        parseFile(file);
      }
    } else {
      parseFile(input);
    }
  }

  private void parseFile(File input) throws IOException {

    Document doc = Jsoup.parse(input, "UTF-8", "");

    Elements type = doc.select("dl.review-statistics dl");
    int rating = 0, sum = 0, counter = 0;
    double total = 0.0;
    for (Element rank : type) {
      String text = rank.text().trim();
      rating = Integer.valueOf(text.charAt(0) - '0');
      if (rating >= 0 && rating <= 10) {
        sum += rating;
        counter++;
      }
    }

    Elements revs = doc.select(
        "div.content-layout-area-review-details > div.content-layout-area-inner");

    if (counter > 0 && revs.size() > 0) {
      total = sum / counter;
      String label = null;
      if (this.model.equals(binary)) {
        label = getBinaryLabel(total);
      } else if (this.model.equals(categ)) {
        label = getCategLabel(total);
      }
      if (label != null) {
        this.reviews.append(label);
        for (Element review : revs) {
          this.reviews.append(" ");
          this.reviews.append(review.text().trim().replaceAll("\n", " "));
        }
        this.reviews.append("\n");
      }

    }

  }

  private String getBinaryLabel(double total) {

    if (total <= 4.0) {
      negative++;
      return "negative";
    } else if (total >= 6.0) {
      positive++;
      return "positive";
    }
    return null;

  }

  private String getCategLabel(double total) {
    if (total <= 2.0) {
      angry++;
      return veryNeg;
    } else if (total <= 4.0) {
      sad++;
      return neg;
    } else if (total <= 6.0) {
      neutral++;
      return neu;
    } else if (total <= 8.0) {
      like++;
      return pos;
    } else if (total <= 10.0) {
      love++;
      return veryPos;
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    /*
     * binary --> binary model labels categorical --> categorical model labels
     */
    ReviewParser parser = new ReviewParser(args[0]);
    parser.parse(args[1]);
    System.out.println(parser.reviews.toString());
    System.out.println(parser.angry);
    System.out.println(parser.sad);
    System.out.println(parser.neutral);
    System.out.println(parser.like);
    System.out.println(parser.love);
  }

}
