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
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import opennlp.tools.sentiment.SentimentME;
import opennlp.tools.sentiment.SentimentModel;

public class GunAdsParser {

  private static String url = "http://darpamemex:darpamemex@imagecat.dyndns.org/solr/imagecatdev/";
  // private static String field1 = "content";
  // private static String field2 = "country";
  private static String fq = "mainType:text";
  private static int start = 0;
  private static int rows = 10;
  private SolrClient solrCore;// = new
                              // HttpSolrClient.Builder(url).build();
  //private static String m = "/model/org/apache/tika/parser/sentiment/topic/en-stanford-sentiment.bin";
  private static File modelFile = null;

  public GunAdsParser() throws MalformedURLException {
    //SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
    //HttpClientBuilder.
    //HttpClientBuilder httpClient = HttpClientBuilder.create();
    //HttpClient httpClient = new HttpClient();
    SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
    //CloseableHttpClient hc = new CloseableHttpClient();
    solrCore = new HttpSolrClient(url, httpClient);//new HttpSolrClient.Builder(url).build();//new HttpSolrClient(url);//new HttpSolrClient(url);// .build();
    // this.solrCore = new HttpSolrServer(url);
    //modelFile = new File(m);
  }

  public void getAds(String m, int num) throws SolrServerException, IOException {
    // Get an instance of server first
    // SolrServer server = getSolrServer();

    File modelFile = new File(m);
    
    // Construct a SolrQuery
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFields("content", "id");
    query.addFilterQuery(fq);
    query.setStart(num);
    query.setRows(rows);
    // query.addSort(field1, SolrQuery.ORDER.asc); //used to be addSortField -->
    // sorts in ascending order

    // Query the server
    QueryResponse rsp = this.solrCore.query(query);

    // Get the results
    SolrDocumentList list = rsp.getResults();

//    for (SolrDocument doc : docs) {
//      //System.out.println(doc.getFieldNames());
//    }
   
    SentimentModel model = new SentimentModel(modelFile);
    SentimentME sentiment = new SentimentME(model);
    
    while (list.getNumFound() > list.getStart() + rows) {
      long index = list.getStart();
      for (SolrDocument doc: list) {
        String result = sentiment.predict((doc.getFieldValue("content")).toString());
        SolrInputDocument document = new SolrInputDocument();
        Map<String, Object> operation = new HashMap<>();
        operation.put("set",  result);
        //SolrSearchUtil.addToDocument(document, "id", doc.getFieldValue("id").toString());
        document.addField("id", doc.getFieldValue("id"));
        //SolrSearchUtil.addToDocument(document, "sentiment_s_md", operation);
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
    //solrCore.commit();
    
//    long st = docs.getStart();
//    long numFound = docs.getNumFound();
//    
//    long counter = st;
//    
//    while (counter <= numFound) {
//      String result = sentiment.predict((docs.get((int) counter).getFieldValue("content")).toString());
//      System.out.println(result);
//      counter++;
//    }
    
    //int counter = 1;
    
//    while (counter <= 10) {
//      int j = counter;
//      //curl  "$URL$j" > all-ads-out/$j.doc
//      for (SolrDocument doc : docs) {
//        String result = sentiment.predict((doc.getFieldValue("content")).toString());
//        System.out.println(result);
//      }
//      //String result = sentiment.predict(sentence);
//      int i = 0;
//      while (i < 2) {
//        int k = j + i;
//        i++;
//      }
//      counter += 2;
////          curl  "$URL$j" > all-ads-out/$j.doc
////          i=0
////          while [  $i -lt 100  ]; do
////            k=$((j + i))
////            cat all-ads-out/$j.doc | ./jq -r .response.docs[$i].content > all-ads-out/$k.sent
////            let i=i+1
////          done
////
////          let COUNTER=COUNTER+100
//    }

    //System.out.println(docs);
  }
  
  

  public static void main(String[] args)
      throws SolrServerException, IOException {
    GunAdsParser parser = new GunAdsParser();
    parser.getAds(args[args.length - 2], Integer.valueOf(args[args.length - 1]));
    //parser.getAds(args[1], Integer.valueOf(args[2]));
    //parser.getAds(args[args.length - 2], args[args.length - 1]);
  }

}
