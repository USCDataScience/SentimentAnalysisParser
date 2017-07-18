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

import java.awt.List;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.opencsv.CSVReader;

/*
 * A class to parse a CSV file with IEEE standards and build a training dataset out of them
 */
public class CSVParserTrain {

  private static String input;
  private PrintWriter outputStream;

  public CSVParserTrain(String inputFile, String outputFile) throws IOException {
    this.input = inputFile;
    Path outputFileName = Paths.get(outputFile);
    Charset encoding = Charset.forName("UTF-8");
    outputStream = new PrintWriter(
        Files.newBufferedWriter(outputFileName, encoding));
  }

  private int getHeaderLocation(String[] headers, String columnName) {
    return Arrays.asList(headers).indexOf(columnName);
  }

  public void read() throws IOException {
    // FileInputStream file = new FileInputStream(new File(input));
    CSVReader reader = new CSVReader(new FileReader(input));

    String[] firstLine = reader.readNext();

    String[] nextLine;
    int categoryPosition, abstractPosition;
    
    List categories = new List();
    Set<String> setCateg = new HashSet<>();

    nextLine = reader.readNext();
    categoryPosition = getHeaderLocation(nextLine, "Category");
    abstractPosition = getHeaderLocation(nextLine, "Abstract");
    
//    int i = 0;
//    while ((nextLine = reader.readNext()) != null && categoryPosition > -1) {
//      if (nextLine[categoryPosition].length() > 0) {
//        //categories.add(nextLine[categoryPosition].toLowerCase());
//        setCateg.add(nextLine[categoryPosition].toLowerCase());
//        //categories[i] = nextLine[categoryPosition].toLowerCase();
//      }
//      i++;
//    }
//    reader.close();
//    
//    //String[] arr = (String[]) setCateg.toArray();
//    String[] arr = setCateg.toArray(new String[setCateg.size()]);
//    String write = "";
//    for (int l = 0; l < arr.length; l++) {
//      for (int k = 1; k < arr.length-l; k++) {
//        if (arr[k].indexOf(arr[l]) != -1) {
//          write = arr[k];
//           //System.out.println(arr[k]);
//        }
//      }
//    }
//    
//    CSVReader reader2 = new CSVReader(new FileReader(input));
//    String[] firstLine2 = reader.readNext();
//    String[] nextLine2 = reader.readNext();
//    while ((nextLine = reader2.readNext()) != null && categoryPosition > -1) {
//      for (int c = 0; c < setCateg.size(); c++) {
//        if (nextLine2[categoryPosition] ) {
//          
//        }
//      }
//      // nextLine[] is an array of values from the line
//      if (nextLine[categoryPosition].length() > 1
//          && nextLine[abstractPosition].length() > 0) {
//        if (nextLine2[categoryPosition]) {
//          outputStream.write(nextLine[categoryPosition].toLowerCase() + " ");
//        }
//        outputStream.write(nextLine[abstractPosition].toLowerCase() + "\n");
//      }
//    }
//    outputStream.close();
    
//    Object[] arr = setCateg.toArray();
//    for (int k = 0; k < setCateg.size(); k++) {
//      System.out.println(arr[k]);
//    }
    
    //Set<String> set = new HashSet<>(categories);
    
    

    while ((nextLine = reader.readNext()) != null && categoryPosition > -1) {
      // nextLine[] is an array of values from the line
      if (nextLine[categoryPosition].length() > 1
          && nextLine[abstractPosition].length() > 0) {
        if (nextLine[categoryPosition].toLowerCase().equals("network") || nextLine[categoryPosition].toLowerCase().equals("networks") || nextLine[categoryPosition].toLowerCase().equals("networking") || nextLine[categoryPosition].toLowerCase().equals("wirelessnetworks") || nextLine[categoryPosition].toLowerCase().equals("wireless")) {
          outputStream.write("wireless_networks ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("devices") || nextLine[categoryPosition].toLowerCase().equals("device")) {
          outputStream.write("devices ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("interface") || nextLine[categoryPosition].toLowerCase().equals("interfaces")) {
          outputStream.write("interfaces ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("system") || nextLine[categoryPosition].toLowerCase().equals("systems")) {
          outputStream.write("systems ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("circuit") || nextLine[categoryPosition].toLowerCase().equals("circuits")) {
          outputStream.write("circuits ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("equipment") || nextLine[categoryPosition].toLowerCase().equals("equipmentunder")) {
          outputStream.write("equipment ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("design") || nextLine[categoryPosition].toLowerCase().equals("qualifieddesign")) {
          outputStream.write("design ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("radio") || nextLine[categoryPosition].toLowerCase().equals("radioactive") || nextLine[categoryPosition].toLowerCase().equals("radionuclide") || nextLine[categoryPosition].toLowerCase().equals("radionuclides")) {
          outputStream.write("radioactive ");
        } else if (nextLine[categoryPosition].toLowerCase().equals("data") || nextLine[categoryPosition].toLowerCase().equals("metadata")) {
          outputStream.write("data ");
        } else {
          outputStream.write(nextLine[categoryPosition].toLowerCase() + " ");
        }
        outputStream.write(nextLine[abstractPosition].toLowerCase() + "\n");
      }
    }
    outputStream.close();
    reader.close();

  }

  public static void main(String[] args) throws IOException {
    CSVParserTrain parser = new CSVParserTrain(args[0], args[1]);

    parser.read();
  }

}
