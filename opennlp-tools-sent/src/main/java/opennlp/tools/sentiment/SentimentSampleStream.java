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

package opennlp.tools.sentiment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * Class for converting Strings through Data Stream to SentimentSample using
 * tokenised text.
 */
public class SentimentSampleStream extends FilterObjectStream<String, SentimentSample> {

	private boolean jsonl;
	private String labelPath;
	private String textPath;
	private Gson gson;
	private Type type;
	private Tokenizer tokenizer;

	public SentimentSampleStream(ObjectStream<String> samples) {
		this(samples, false, null, null);
	}

	/**
	 * Initializes the sample stream.
	 *
	 * @param samples
	 *            the sentiment samples to be used
	 */
	public SentimentSampleStream(ObjectStream<String> samples, boolean jsonl, String labelPath, String textPath) {
		super(samples);
		this.jsonl = jsonl;
		tokenizer = WhitespaceTokenizer.INSTANCE;
		if (jsonl) {
			this.labelPath = Objects.requireNonNull(labelPath);
			this.textPath = Objects.requireNonNull(textPath);

			this.gson = new GsonBuilder().create();
			TypeToken<Map<String, Object>> mapTypeToken = new TypeToken<Map<String, Object>>() {
			};
			type = mapTypeToken.getType();
		}
	}

	/**
	 * Reads the text
	 *
	 * @return a ready-to-be-trained SentimentSample object
	 */
	@Override
	public SentimentSample read() throws IOException {
		String line = samples.read();

		if (line != null) {
			if (jsonl) {
				return processJsonl(line);
			} else {
				return processPlain(line);
			}
		}

		return null;
	}

	private SentimentSample processPlain(String line) throws IOException {
		String tokens[] = tokenizer.tokenize(line);

		SentimentSample sample;

		if (tokens.length > 1) {
			String sentiment = tokens[0];
			String sentTokens[] = new String[tokens.length - 1];
			System.arraycopy(tokens, 1, sentTokens, 0, tokens.length - 1);

			sample = new SentimentSample(sentiment, sentTokens);
		} else {
			throw new IOException("Empty lines, or lines with only a category string are not allowed!");
		}

		return sample;
	}

	private SentimentSample processJsonl(String line) {
		Map<String, Object> json = gson.fromJson(line, type);

		String label = valueString(json, labelPath);
		label = label.trim();

		String text = valueString(json, textPath);
		text = text.trim().replaceAll("\\s+", " ");
		String tokens[] = tokenizer.tokenize(text);

		return new SentimentSample(label, tokens);
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
				throw new UnsupportedOperationException(
						"Could not find path " + Arrays.toString(paths) + " stop on " + paths[i]);
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
}
