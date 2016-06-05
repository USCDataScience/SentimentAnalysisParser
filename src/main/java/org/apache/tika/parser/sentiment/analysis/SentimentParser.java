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

package org.apache.tika.parser.sentiment.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import opennlp.tools.sentiment.SentimentME;
import opennlp.tools.sentiment.SentimentModel;

/**
 * The main class for creating a sentiment analysis parser.
 */
public class SentimentParser extends AbstractParser {

  private static final Set<MediaType> SUPPORTED_TYPES = Collections
      .singleton(MediaType.application("sentiment"));
  public static final String HELLO_MIME_TYPE = "application/sentiment";
  private static final Logger LOG = Logger
      .getLogger(SentimentParser.class.getName());

  private SentimentME sentiment;
  private URL modelUrl;
  private File modelFile;
  private boolean initialised;
  private boolean available;

  public SentimentParser() {
    System.out.println("Create sentiment parser");
  }

  public void initialise(URL url) {
    try {
      if (this.modelUrl != null
          && this.modelUrl.toURI().equals(modelUrl.toURI())) {
        return;
      }
    } catch (URISyntaxException e1) {
      throw new RuntimeException(e1.getMessage());
    }

    this.modelUrl = url;

    this.available = url != null;

    if (this.available) {
      try {
        SentimentModel model = new SentimentModel(url);
        this.sentiment = new SentimentME(model);
      } catch (Exception e) {
        LOG.warning("Sentiment Parser setup failed: " + e);
        this.available = false;
      }

    }
    initialised = true;
  }

  public void initialise(File file) {
    this.modelFile = file;

    try {
      SentimentModel model = new SentimentModel(file);
      this.sentiment = new SentimentME(model);
      this.available = true;
    } catch (IOException e) {
      LOG.warning("Sentiment Parser setup failed: " + e);
      this.available = false;
    }
    initialised = true;
  }

  @Override
  public Set<MediaType> getSupportedTypes(ParseContext arg0) {
    return SUPPORTED_TYPES;
  }

  @Override
  public void parse(InputStream stream, ContentHandler handler,
      Metadata metadata, ParseContext context)
      throws IOException, SAXException, TikaException {
    if (!initialised) {
      initialise(new File(
          "target/sentiment/model/org/apache/tika/parser/sentiment/topic/en-sentiment.bin"));
    }
    if (available) {
      String inputString = IOUtils.toString(stream, "UTF-8");
      String output = sentiment.predict(inputString);
      metadata.add("Sentiment: ", output);
    } else {
      metadata.add("Error", "Model is not available");
    }
  }

}
