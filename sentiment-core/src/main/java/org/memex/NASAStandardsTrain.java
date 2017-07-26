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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/*
 * A class to build a training dataset for NASA Standards
 */
public class NASAStandardsTrain {

  public static void parse(String inputName, String outputName)
      throws IOException, SAXException, TikaException {
    // String tempOutput = "html-out";
    Path outputFileTemp = Paths.get(outputName);
    Charset encoding = Charset.forName("UTF-8");
    PrintWriter outputStream = new PrintWriter(
        Files.newBufferedWriter(outputFileTemp, encoding));
    File input = new File(inputName);
 

    MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

    TesseractOCRParser ocrParser = new TesseractOCRParser();
    //PDFParser pdfParser = new PDFParser();

    AutoDetectParser parser = new AutoDetectParser();
    //CompositeParser parser = new CompositeParser(mediaTypeRegistry,
        //Arrays.asList(ocrParser, pdfParser));

    TesseractOCRConfig config = new TesseractOCRConfig();
    PDFParserConfig pdfConfig = new PDFParserConfig();
    pdfConfig.setExtractInlineImages(true);

    ParseContext parseContext = new ParseContext();
    parseContext.set(TesseractOCRConfig.class, config);
    parseContext.set(PDFParserConfig.class, pdfConfig);
    // need to add this to make sure recursive parsing happens
    //parseContext.set(Parser.class, parser);
    //parseContext.set
    
//    String startRegex = "^1.+(SCOPE)";
//    String endRegex = "^2.+[A-Z]+";
    Pattern patternStart = Pattern.compile("^1.+(SCOPE)");
    Pattern patternEnd = Pattern.compile("^2.+[A-Z]+");
    
    
    
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

        parser.parse(stream, handlerOCR, metadata, parseContext);
        stream.close();

        String content = handlerOCR.toString();
        System.out.println("HELLO");

        int nameIndex = content.indexOf("NASA-STD-");
        int nameSpace = Math.min(content.indexOf(' ', nameIndex),
            content.indexOf('\n', nameIndex));
        String documentId = content.substring(nameIndex, nameSpace);
        String label = content.substring("NASA-STD-".length() + nameIndex,
            nameSpace + 1);
        
        Matcher matcherStart = patternStart.matcher(content);
        Matcher matcherEnd = patternEnd.matcher(content);
        int start = 0, end = 0;
        
        if (matcherStart.find()) {
          start = matcherStart.start();
        }
        if (matcherEnd.find()) {
          end = matcherEnd.start();
        }
        
        String scope = content.substring(start, end + 1);
        outputStream.write(label + " " + scope + "\n");
        
      }
    }
    
    outputStream.close();
  }
      
      
      
      
      
      // BodyContentHandler handlerNormal = new BodyContentHandler(-1);
      // Parser parser1 = new RecursiveParserWrapper(new AutoDetectParser(),
      // (ContentHandlerFactory) new BasicContentHandlerFactory(
      // handlerType, -1));
      //Metadata metadata = new Metadata();
      
      // Chris said: use XHTML content handler instead --> annotated parsed text

      // ToXMLContentHandler handlerOCR = new ToXMLContentHandler();

//      ContentHandler handler = new BodyContentHandler(-1);
//      XHTMLContentHandler xhtmlHandler = new XHTMLContentHandler(handler,
//          metadata);
//      xhtmlHandler.element("Scope", "");
      
//      BodyContentHandler handlerOCR = new BodyContentHandler(-1);
//      Parser parser = new AutoDetectParser();
//
//      TesseractOCRConfig config = new TesseractOCRConfig();
//      PDFParserConfig pdfConfig = new PDFParserConfig();
//      pdfConfig.setExtractInlineImages(true);
//
//      ParseContext parseContext = new ParseContext();
//      parseContext.set(TesseractOCRConfig.class, config);
//      parseContext.set(PDFParserConfig.class, pdfConfig);
//      // need to add this to make sure recursive parsing happens!
//      parseContext.set(Parser.class, parser);
//
//      Pattern div = Pattern.compile("</?div(|\\s+[^>]+)>");
//      // Pattern p = Pattern.compile("</?p(|\\s+[^>]+)>");
//      Pattern meta = Pattern.compile("</?meta(|\\s+[^>]+)>");
//      
//      Pattern patternStart = Pattern.compile("^1.+(SCOPE)");
//      Pattern patternEnd = Pattern.compile("^2.+[A-Z]+");

//      FileInputStream stream = new FileInputStream(file);
//      parser.parse(stream, handlerOCR, metadata, parseContext);
//      // </div> <div class="page">
//      String content = handlerOCR.toString().toLowerCase().replaceAll("\t",
//          " ");
//      // .replaceAll("\t", " ")
//      // .replaceAll("</div>", " ")
//      // .replaceAll("<div class=" + "\"" + "page" + "\"" + ">", "
//      // ").replaceAll("</?meta(|\\s+[^>]+)>", "").replaceAll("\n", " ");
//      String fileName = file.getName().replace(".pdf", "");
//      String[] metadataNames = metadata.names();
//      String[] fileN = {};
//      String label = "";
//
//      if (fileName.contains("-")) {
//        fileN = fileName.split("-");
//        label = fileN[fileN.length - 1];
//      } else if (fileName.contains("_")) {
//        fileN = fileName.split("_");
//        label = fileN[fileN.length - 1];
//      } else {
//        label = fileName;
//      }
//      int start = findPosition(content, "1. scope", 1);
//      int end = findPosition(content, "2. applicable documents", 1);
//
//      // int start = content.indexOf("1. scope");// + "<p>1. scope".length();
//      // int end = content.indexOf("2. applicable documents") - 1;
//      // int start = content.indexOf("<p>1. scope");// + "<p>1. scope".length();
//      // int end = content.indexOf("<p>2. applicable documents") - 1;// + "2.
//      // applicable
//      // documents".length()-1;
//
//      // for (int i = start; i <= end; i++) {
//      String scope = content.substring(start, end + 1);
//      // .replaceAll("<p>", " ")
//      // .replaceAll("</p>", " ").replaceAll("<p />", " ")
//      // .replaceAll("\n", " ");
//      // }
//
//      // System.out.println(content);
//
//      outputStream.write(label + " " + scope + "\n");
//
//      stream.close();
      // xhtmlHandler.startDocument();
      // xhtmlHandler.endDocument();

      // System.out.println(xhtmlHandler.toString());

      // outputStream.write(label + " " + content + "\n");

      // //parsing the document using PDF parser
      // PDFParser pdfparser = new PDFParser();
      // pdfparser.parse(inputstream, handlerNormal, metadata, pcontext);
      //
      // String contentNormal = handlerNormal.toString();


    // BufferedReader reader = Files.newBufferedReader(outputFileTemp,
    // encoding);
    // Path outputFileName = Paths.get(outputName);
    // PrintWriter outputStreamFinal = new PrintWriter(
    // Files.newBufferedWriter(outputFileName, encoding));
    // String line;
    // int i = 0;
    // while ((line = reader.readLine().toLowerCase().replaceAll("\t", " ")) !=
    // null) {
    // if (line.equals("<p>1. scope")) {
    // System.out.println("HEY " + i);
    // i++;
    // }
    // }

  private static int findPosition(String content, String str, int i) {
    List<Integer> positions = new LinkedList<>();
    int position = content.indexOf(str, 0);

    while (position != -1) {
      positions.add(position);
      position = content.indexOf(str, position + 1);
    }

    return positions.get(i);

    // Pattern pattern = Pattern.compile(str);
    // Matcher matcher = pattern.matcher(content);
    // positions = new LinkedList<>();
    //
    // while (matcher.find()) {
    // positions.add(matcher.start());
    // }

  }

  /*
   * ../../../DHS-ASSESS/NASA-STDs ../../../DHS-ASSESS/nasa-train
   */
  public static void main(String[] args)
      throws IOException, SAXException, TikaException {

    parse(args[0], args[1]);

  }

}
