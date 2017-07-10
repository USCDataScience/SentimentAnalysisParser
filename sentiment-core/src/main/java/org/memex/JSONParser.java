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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.tika.parser.tagRatio.TextToTagRatio;
//import org.apache.maven.scm.provider.svn.svnexe.command.changelog.IllegalOutputException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/*
 * A class that parses a JSON file, extracts the values of the required fields and outputs them in a preferred manner
 */
public class JSONParser {

  private static final Logger LOG = LoggerFactory.getLogger(JSONParser.class);

  private String fileName;
  private String outputName;
  private boolean print = false;
  private boolean trim = true;
  private String delimeter = " ";// "\t";
  private boolean csv = true;
  private boolean separateFile = false; // !!!

  public JSONParser(String fileName, String outputName) {
    this.fileName = fileName;
    this.outputName = outputName;
  }

  public void process(String pathFileId, String[] paths) {
    Path inputFileName = Paths.get(fileName);
    Path outputFileName = Paths.get(outputName);

    // we use java 8 auto closable feature (the stream will be close
    // automatically).
    Charset encoding = Charset.forName("UTF-8");
    try (
        BufferedReader reader = Files.newBufferedReader(inputFileName,
            encoding);
        PrintWriter writer = separateFile ? null
            : new PrintWriter(
                Files.newBufferedWriter(outputFileName, encoding))) {
      Gson gson = new GsonBuilder().create();

      // create typeToken to map json objects to the map
      // looked in the google how to map gson to the map
      TypeToken<Map<String, Object>> mapTypeToken = new TypeToken<Map<String, Object>>() {

      };
      Type type = mapTypeToken.getType();

      String line;
      while ((line = reader.readLine()) != null) {
        Map<String, Object> json = gson.fromJson(line, type);
        // now we can use json as a simple map
        String[] values = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
          String path = paths[i];
          String value = valueString(json, path);
          if (trim) {
            value = value.trim().replaceAll("\\s+", " ");
          }
          values[i] = value;
        }
        if (separateFile) {
          if (csv) {
            String name = valueString(json, pathFileId);
            Path outputFile = Paths.get(outputName, name /* + ".csv" */);
            try (PrintWriter fileWriter = new PrintWriter(Files
                .newBufferedWriter(outputFile, Charset.forName("UTF-8")))) {
              printCSV(paths, values, fileWriter);
            }
          }
        } else {
          if (csv) {
            printCSV(paths, values, writer);
          }
        }
        if (print) {
          printCSV(paths, values, System.out);
        }
      }
    } catch (FileNotFoundException e) {
      // we use {} because string concatenation is expensive ( "hot" +
      // "dog" )
      // in case we decide don't write to log files concatenation will
      // still work but on the next operation Logger.error will do
      // nothing.
      // so we use format {}. the concatenation will not happens until
      // logger decide to log this message.
      LOG.error("File not found {}", fileName);
    } catch (IOException e) {
      // here we pass exception, because we want to see it in the logs.
      // We don't use {}, because Logger.error has following construction
      // Logger.error(message, exception)
      LOG.error("Could not read from the file", e);
    }
  }

  private void printCSV(String[] paths, String[] values, PrintWriter writer) {
    for (int i = 0; i < values.length; i++) {
      if (i > 0) {
        writer.write(delimeter);
      }
      writer.write(values[i]);
    }
    writer.write("\n");
  }

  private void printCSV(String[] paths, String[] values, PrintStream stream) {
    for (int i = 0; i < values.length; i++) {
      if (i > 0) {
        stream.print(delimeter);
      }
      stream.print(values[i]);
      stream.println();
    }
  }

  private static String valueString(Map<String, Object> json, String path) {
    return (String) value(json, path);
  }

  private static Object value(Map<String, Object> json, String path) {
    String[] paths = path.split("\\.");
    return value(json, paths);
  }

  private static Object value(Map<String, Object> json, String[] paths) {
    Map<String, Object> node = json;
    for (int i = 0; i < paths.length - 1; i++) {
      Object inside = get(node, paths[i]);
      if (inside instanceof Map) {
        node = (Map<String, Object>) inside;
      } else {
        throw new UnsupportedOperationException("Could not find path "
            + Arrays.toString(paths) + " stop on " + paths[i]);
      }
    }
    return get(node, paths[paths.length - 1]);
  }

  private static Object get(Map<String, Object> json, String path) {
    int bracket = path.indexOf('[');
    if (bracket >= 0) {
      int close = path.indexOf(']', bracket + 1);
      if (close >= 0) {
        String name = path.substring(0, bracket);
        List<Object> array = (List<Object>) json.get(name);
        String ind = path.substring(bracket + 1, close);
        return array.get(Integer.valueOf(ind));
      }
    }
    return json.get(path);
  }

  public static void main(String[] args) throws ParseException {
    String fileName = args[0];
    String outputName = args[1];

    JSONParser parser = new JSONParser(fileName, outputName);
    // parser.process("_id", new String[] { /*"cluster_id", */
    // /*"_source.extracted_text"*/ "annotation", "_source.extracted_text" });
    parser.process("_id", new String[] { "annotation", "_source.raw_content" });

    // TextToTagRatio textToTagRatio=new TextToTagRatio();
    // Metadata metadata = new Metadata();
    // StringWriter writer = new StringWriter();
    // ParseContext context = new ParseContext();
    // context.set(org.apache.tika.parser.tagRatio.TextToTagRatio.class,textToTagRatio);
  }
}
