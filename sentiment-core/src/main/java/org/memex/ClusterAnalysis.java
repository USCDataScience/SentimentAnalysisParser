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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/*
 * A class to analyse the distribution of categorical sentiment in relation to the cluster_id
 */
public class ClusterAnalysis {

  private String modelOutput;
  private String clusterId;
  
  private static double angry = 0.0;
  private static double sad = 0.0;
  private static double neutral = 0.0;
  private static double like = 0.0;
  private static double love = 0.0;

  private Map<String, Map<String, Integer>> map;

  public ClusterAnalysis(String sentiment, String cluster) {
    this.modelOutput = sentiment;
    this.clusterId = cluster;
    this.map = new HashMap<String, Map<String, Integer>>();
  }

  public void analyse() throws IOException {
    File cl = new File(clusterId);
    Set<String> cls = new HashSet<>();
    for (File file1 : cl.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;
      String cluster = FileUtils.readFileToString(file1);
      cls.add(cluster); // unique
    }
    Iterator iter = cls.iterator();
    while (iter.hasNext()) {
      Map<String, Integer> singleMap = new HashMap<String, Integer>();
      singleMap.put("love", 0);
      singleMap.put("like", 0);
      singleMap.put("neutral", 0);
      singleMap.put("sad", 0);
      singleMap.put("angry", 0);
      singleMap.put("total", 0);
      map.put((String) iter.next(), singleMap);
    }

    File sentiment = new File(modelOutput);
    for (File file2 : sentiment.listFiles()) {
      String id = file2.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;
      id = id.replace(".out", "");
      File clusterFile = new File(clusterId + "/" + id);
      String sent = FileUtils.readFileToString(file2); 
      String clId = FileUtils.readFileToString(clusterFile);
      
      Iterator it = cls.iterator();
      while (it.hasNext()) {
        String cluster_id = (String) it.next();
        Map<String, Integer> miniMap = map.get(cluster_id);
        if (sent.indexOf("love") != -1 && clId.equals(cluster_id)) {
          miniMap.put("love", map.get(cluster_id).get("love") + 1);
          miniMap.put("total", map.get(cluster_id).get("total") + 1);
          map.put(cluster_id, miniMap);
        } else if (sent.indexOf("like") != -1 && clId.equals(cluster_id)) {
          miniMap.put("like", map.get(cluster_id).get("like") + 1);
          miniMap.put("total", map.get(cluster_id).get("total") + 1);
          map.put(cluster_id, miniMap);
        } else if (sent.indexOf("neutral") != -1 && clId.equals(cluster_id)) {
          miniMap.put("neutral", map.get(cluster_id).get("neutral") + 1);
          miniMap.put("total", map.get(cluster_id).get("total") + 1);
          map.put(cluster_id, miniMap);
        } else if (sent.indexOf("sad") != -1 && clId.equals(cluster_id)) {
          miniMap.put("sad", map.get(cluster_id).get("sad") + 1);
          miniMap.put("total", map.get(cluster_id).get("total") + 1);
          map.put(cluster_id, miniMap);
        } else if (sent.indexOf("angry") != -1 && clId.equals(cluster_id)) {
          miniMap.put("angry", map.get(cluster_id).get("angry") + 1);
          miniMap.put("total", map.get(cluster_id).get("total") + 1);
          map.put(cluster_id, miniMap);
        }
      }
    }

    System.out.println(map.entrySet());
    System.out.println();
    
    Iterator iterator = cls.iterator();
    while (iterator.hasNext()) {
      String clusterid = (String) iterator.next();
      Map<String, Integer> singleMap = map.get(clusterid);
      double tot = (double) singleMap.get("total");
      double lo = (double) singleMap.get("love");
      double li = (double) singleMap.get("like");
      double n = (double) singleMap.get("neutral");
      double s = (double) singleMap.get("sad");
      double a = (double) singleMap.get("angry");
      love = love + lo/tot;
      like = like + li/tot;
      neutral = neutral + n/tot;
      sad = sad + s/tot;
      angry = angry + a/tot;
    }

  }

  public static void main(String[] args) throws IOException {

    /*
     * ../sentiment-examples/src/main/resources/memex/ht-lg-stanford-out
     * ../sentiment-examples/src/main/resources/memex/ht-lg-clusters
     */

    ClusterAnalysis ca = new ClusterAnalysis(args[0], args[1]);
    ca.analyse();
    System.out.println("angry\t" + ca.angry);
    System.out.println("sad\t" + ca.sad);
    System.out.println("neutral\t" + ca.neutral);
    System.out.println("like\t" + ca.like);
    System.out.println("love\t" + ca.love);

  }

}
