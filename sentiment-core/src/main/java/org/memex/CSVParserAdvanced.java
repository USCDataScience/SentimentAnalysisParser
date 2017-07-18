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

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;

/*
 * A class to parse a CSV file with IEEE standards and build a training dataset out of them
 */
public class CSVParserAdvanced {

  private String inputOld;
  private String inputNew;
  private String outputName;
  private PrintWriter outputStream;

  public CSVParserAdvanced(String inputFileOld, String inputFileNew,
      String outputPath) throws IOException {
    this.inputOld = inputFileOld;
    this.inputNew = inputFileNew;
    this.outputName = outputPath;
  }

  private int getHeaderLocation(String[] headers, String columnName) {
    return Arrays.asList(headers).indexOf(columnName);
  }

  public void read() throws IOException {
    // read old
    Path inputFileOld = Paths.get(this.inputOld);
    CSVReader readerOld = new CSVReader(new FileReader(inputOld));
    readerOld.readNext();
    List<String> headerOld = Arrays.asList(readerOld.readNext());
    int abstractOldIndex = headerOld.indexOf("Abstract");

    String[] nextLineOld;
    Set<String> oldAbstracts = new HashSet<>();
    while ((nextLineOld = readerOld.readNext()) != null) {
      oldAbstracts.add(nextLineOld[abstractOldIndex]);
    }
    readerOld.close();

    // read new
    Path inputFileNew = Paths.get(this.inputNew);
    CSVReader readerNew = new CSVReader(new FileReader(inputNew));
    List<String> headerNew = Arrays.asList(readerNew.readNext()); // all the
                                                                  // unnecessary
                                                                  // stuff in
                                                                  // the 1st
                                                                  // line of the
                                                                  // old file
    int abstractNewIndex = headerNew.indexOf("Abstract");
    String[] nextLineNew;
    int id = 0;
    while ((nextLineNew = readerNew.readNext()) != null) {
      String abstractValue = nextLineNew[abstractNewIndex];
      if (!oldAbstracts.contains(abstractValue)) {
        System.out.println(abstractValue);
        String outFileName = outputName + "/" + id + ".sent";
        Path outputFile = Paths.get(outFileName);
        PrintWriter fileWriter = new PrintWriter(
            Files.newBufferedWriter(outputFile, Charset.forName("UTF-8")));
        fileWriter.write(abstractValue);
        fileWriter.close();
        id++;
      }
    }
    readerNew.close();
  }

  public static void main(String[] args) throws IOException {
    CSVParserAdvanced parser = new CSVParserAdvanced(args[0], args[1], args[2]);

    parser.read();
  }

}
