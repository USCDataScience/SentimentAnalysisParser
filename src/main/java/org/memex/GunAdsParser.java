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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import opennlp.tools.sentiment.SentimentME;
import opennlp.tools.sentiment.SentimentModel;

public class GunAdsParser {

  private static String fq = "mainType:text";
  private static int start = 0;
  private static int rows = 10;
  private SolrClient solrCore;
  private static File modelFile = null;

  public GunAdsParser(String url) throws MalformedURLException {
    SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
    solrCore = new HttpSolrClient(url, httpClient);
  }

  public void getAds(String modelFilePath, int start) throws SolrServerException, IOException {
    File modelFile = new File(modelFilePath);
    
    // Construct a SolrQuery
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFields("content", "id");
    query.addFilterQuery(fq);
    query.setStart(start);
    query.setRows(rows);

    QueryResponse rsp = this.solrCore.query(query);
    SolrDocumentList list = rsp.getResults();
   
    SentimentModel model = new SentimentModel(modelFile);
    SentimentME sentiment = new SentimentME(model);
    
    while (list.getNumFound() > list.getStart() + rows) {
      long index = list.getStart();
      for (SolrDocument doc: list) {
        String result = sentiment.predict((doc.getFieldValue("content")).toString());
        SolrInputDocument document = new SolrInputDocument();
        Map<String, Object> operation = new HashMap<>();
        operation.put("set",  result);
        document.addField("id", doc.getFieldValue("id"));
        document.addField("sentiment_s_md", operation);
        System.out.println(doc.getFieldValue("id"));
        solrCore.add(document);
        solrCore.commit();
        if (true) {
          return;
        }
        System.out.println(index + ": " + result);
        index++;
      }
      //commit
      solrCore.commit();
      if (list.getNumFound() > list.getStart() + rows) {
        query.setStart((int) (list.getStart() + rows));
        rsp = this.solrCore.query(query);
        list = rsp.getResults();
      }
    }
    
  }
  
  

  public static void main(String[] args)
      throws SolrServerException, IOException {
    GunAdsParser parser = new GunAdsParser(args[0]);
    parser.getAds(args[1], Integer.valueOf(args[2]));
  }

}
