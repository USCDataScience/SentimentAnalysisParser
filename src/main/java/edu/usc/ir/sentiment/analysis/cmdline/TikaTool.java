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

  private Parser parser;
  private String encoding = null;
  private Detector detector;
  private ParseContext context;

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

    @Override
    public void endDocument() {
      String[] names = metadata.names();
      Arrays.sort(names);
      outputMetadata(names);
      writer.flush();
      this.metOutput = true;
    }

    public void outputMetadata(String[] names) {
      for (String name : names) {
        for (String value : metadata.getValues(name)) {
          writer.println(name + ": " + value);
        }
      }
    }

    public boolean metOutput() {
      return this.metOutput;
    }

  }

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

  public void process(String arg) throws MalformedURLException {
    URL url;
    File file = new File(arg);
    if (file.isFile()) {
      url = file.toURI().toURL();
    } else {
      url = new URL(arg);
    }
    Metadata metadata = new Metadata();
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

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public void run(String[] args) {
    try {
      process(args[0]);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) throws Exception {
    TikaTool tool = new TikaTool();
    tool.process(args[0]);
  }

}
