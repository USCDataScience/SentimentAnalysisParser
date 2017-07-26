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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class NASAStandardsTrainNew {

  public static void parse(String inputName, String outputName)
      throws IOException, SAXException, TikaException {

    Path outputFileTemp = Paths.get(outputName);
    Charset encoding = Charset.forName("UTF-8");
    PrintWriter outputStream = new PrintWriter(
        Files.newBufferedWriter(outputFileTemp, encoding));
    File input = new File(inputName);

    MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

    TesseractOCRParser ocrParser = new TesseractOCRParser();
    PDFParser pdfParser = new PDFParser();

    CompositeParser parser = new CompositeParser(mediaTypeRegistry,
        Arrays.asList(ocrParser, pdfParser));

    TesseractOCRConfig config = new TesseractOCRConfig();
    PDFParserConfig pdfConfig = new PDFParserConfig();
    pdfConfig.setExtractInlineImages(true);

    ParseContext parseContext = new ParseContext();
    parseContext.set(TesseractOCRConfig.class, config);
    parseContext.set(PDFParserConfig.class, pdfConfig);
    // need to add this to make sure recursive parsing happens
    parseContext.set(Parser.class, parser);

    for (File file : input.listFiles()) {

      String fileName = file.getName();
      int extIndex = fileName.lastIndexOf('.');
      String name = fileName.substring(0, extIndex);
      String extension = fileName.substring(extIndex + 1).toLowerCase();

      if ("pdf".equals(extension)) {
        System.out.println(name);

        BodyContentHandler handlerOCR = new BodyContentHandler(-1);
        FileInputStream stream = new FileInputStream(file);
        Metadata metadata = new Metadata();

        pdfParser.parse(stream, handlerOCR, metadata, parseContext);
        stream.close();

        String content = handlerOCR.toString();
        //System.out.println(content);

        int nameIndex = content.indexOf("NASA-STD-");
        int nameSpace = Math.min(content.indexOf(' ', nameIndex),
            content.indexOf('\n', nameIndex));
        String documentId = content.substring(nameIndex, nameSpace);
        String label = content.substring("NASA-STD-".length() + nameIndex,
            nameSpace + 1);

        int start = content.lastIndexOf("\n1. ");
        int scopeIndex = content.indexOf("SCOPE", start);
        while (scopeIndex - start > 10) {
          start = content.lastIndexOf("\n1. ", start);
          scopeIndex = content.indexOf("SCOPE", start);
        }

        int end = content.indexOf("\n2. ", start);

        StringBuilder scope = new StringBuilder();
        int pageIndex;
        do {
          pageIndex = content.indexOf(" of ", start);
          if (pageIndex == -1) {
            scope.append(content.substring(start, end));
          } else if (pageIndex < end) {
            int currentPage = Math.max(content.lastIndexOf(" ", pageIndex - 1),
                content.lastIndexOf("\n", pageIndex - 1));
            int totalPage = Math.min(
                content.indexOf(" ", pageIndex + " of ".length() + 1),
                content.indexOf("\n", pageIndex + " of ".length() + 1));
            String current = content.substring(currentPage + 1, pageIndex);
            String total = content.substring(pageIndex + " of ".length(),
                totalPage);

            try {
              Integer.valueOf(current);
              Integer.valueOf(total);

              int lastIndex = content.lastIndexOf(documentId, currentPage);
              // footer
              if (currentPage - lastIndex <= 100) {
                currentPage = lastIndex;
              }

              scope.append(content.substring(start, currentPage));
              start = totalPage + total.length() + 1;
            } catch (NumberFormatException e) {
              scope.append(content.substring(start, totalPage + 1));
              start = totalPage + 1;
            }
          } else {
            scope.append(content.substring(start, end));
          }
        } while (pageIndex != -1 && pageIndex < end && start < end);

        outputStream.write(label + " " + scope + "\n");
      }
    }
    outputStream.close();
  }


  /*
   * ../../../DHS-ASSESS/NASA-STDs ../../../DHS-ASSESS/nasa-train
   * ../../../DHS-ASSESS/test-nasa ../../../DHS-ASSESS/nasa-st-test1
   */
  public static void main(String[] args)
      throws IOException, SAXException, TikaException {

    parse(args[0], args[1]);

  }
}
