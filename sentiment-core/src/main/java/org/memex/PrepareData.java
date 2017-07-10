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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * A class to make the training dataset more even in terms of the number of elements per each label, as well as to shuffle the data.
 */
public class PrepareData {

  public static void main(String[] args) {
    String filaName = args[0];
    String outputFile = args[1];
    // count labels
    Map<String, Integer> countLabels = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filaName))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(" ");
        String label = tokens[0];
        Integer count = countLabels.get(label);
        if (count == null) {
          count = 0;
        }
        countLabels.put(label, count + 1);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // find min
    int min = Integer.MAX_VALUE;
    for (Integer count : countLabels.values()) {
      min = Math.min(min, count);
    }
    // make distribution
    countLabels.clear();
    try (BufferedReader br = new BufferedReader(new FileReader(filaName));
        BufferedWriter writer = new BufferedWriter(
            new FileWriter(outputFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(" ");
        String label = tokens[0];
        Integer count = countLabels.get(label);
        if (count == null) {
          count = 0;
        }
        if (count < min) {
          writer.write(line);
          writer.write("\n");
          countLabels.put(label, count + 1);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
