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

package edu.usc.ir.sentiment.analysis.cmdline;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
//import org.apache.tika.fork.ForkParser;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.sentiment.analysis.SentimentParser;
import org.xml.sax.SAXException;
//import org.apache.tika.parser.PasswordProvider;
import org.xml.sax.helpers.DefaultHandler;

import opennlp.tools.cmdline.BasicCmdLineTool;

/**
 * Class for launching the parser using Apache Tika.
 */
public class TikaTool extends BasicCmdLineTool {
  private static final Logger LOG = Logger.getLogger(TikaTool.class.getName());

  private static final String DEFAULT_MODEL = "en-sentiment.bin";

  private Parser parser;
  private String encoding = null;
  private Detector detector;
  private ParseContext context;
  private String url;

  /**
   * The constructor
   */
  public TikaTool() {
    detector = new DefaultDetector();
    parser = new AutoDetectParser(detector);
    context = new ParseContext();
    context.set(Parser.class, parser);
  }

  private class NoDocumentMetHandler extends DefaultHandler {

    protected final Metadata metadata;

    protected PrintWriter writer;

    private boolean metOutput;

    public NoDocumentMetHandler(Metadata metadata, PrintWriter writer) {
      this.metadata = metadata;
      this.writer = writer;
      this.metOutput = false;
    }

    /**
     * Ends the document given
     */
    @Override
    public void endDocument() {
      String[] names = metadata.names();
      Arrays.sort(names);
      outputMetadata(names);
      writer.flush();
      this.metOutput = true;
    }

    /**
     * Outputs the metadata
     *
     * @param names
     *          the names provided
     */
    public void outputMetadata(String[] names) {
      for (String name : names) {
        for (String value : metadata.getValues(name)) {
          writer.println(name + ": " + value);
        }
      }
    }

    /**
     * Checks the output
     *
     * @return true or false
     */
    public boolean metOutput() {
      return this.metOutput;
    }

  }

  /**
   * Returns a writer
   *
   * @param output
   *          the output stream
   * @param encoding
   *          the encoding required to create a writer
   */
  private static Writer getOutputWriter(OutputStream output, String encoding)
      throws UnsupportedEncodingException {
    if (encoding != null) {
      return new OutputStreamWriter(output, encoding);
    } else if (System.getProperty("os.name").toLowerCase(Locale.ROOT)
        .startsWith("mac os x")) {
      return new OutputStreamWriter(output, UTF_8);
    } else {
      return new OutputStreamWriter(output, Charset.defaultCharset());
    }
  }

  /**
   * Performs the analysis
   *
   * @param fileName
   *          the file with text to be analysed
   * @param model
   *          the analysis model to be used
   */
  public void process(String fileName, String model)
      throws MalformedURLException {
    URL url;
    File file = new File(fileName);
    if (file.isFile()) {
      url = file.toURI().toURL();
    } else {
      url = new URL(fileName);
    }
    Metadata metadata = new Metadata();
    metadata.add(SentimentConstant.MODEL, model);
    try (InputStream input = TikaInputStream.get(url, metadata)) {
      process(input, System.out, metadata);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    } finally {
      System.out.flush();
    }
  }

  /**
   * Performs the analysis
   *
   * @param input
   *          the input
   * @param output
   *          the output
   * @param metadata
   *          the metadata to be used
   */
  public void process(InputStream input, OutputStream output, Metadata metadata)
      throws IOException, SAXException, TikaException {
    Parser p = parser;
    final PrintWriter writer = new PrintWriter(
        getOutputWriter(output, encoding));
    NoDocumentMetHandler handler = new NoDocumentMetHandler(metadata, writer);
    p.parse(input, handler, metadata, context);

    if (!handler.metOutput()) {
      handler.endDocument();
    }
  }

  /**
   * Help method
   */
  @Override
  public String getHelp() {
    return null;
  }

  /**
   * Runs the parser and performs analysis
   *
   * @param args
   *          arguments required
   */
  @Override
  public void run(String[] args) {
    String fileName = null;
    String model = DEFAULT_MODEL;
    if (args.length > 0) {
      for (int i = 0; i < args.length - 1; i++) {
        switch (args[i]) {
        case "-m":
          model = args[i + 1];
          break;
        }
      }
      fileName = args[args.length - 1];
    }
    try {
      process(fileName, model);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) throws Exception {
    TikaTool tool = new TikaTool();
    tool.process(args[0], args[1]);
  }

}
