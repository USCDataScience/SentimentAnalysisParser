package org.memex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class TestModel {

  private static String inputFile;
  private static String outputFile;

  private static final String labels = "../sentiment-examples/src/main/resources/train/htlg-labels-r";
  private static final int num = 4449;

//  private static int match;
//  private static int notMatch;
  
  private static int trueRel;
  private static int trueNotRel;
  private static int falseRel;
  private static int falseNotRel;

  public TestModel(String input, String output) {
    this.inputFile = input;
    this.outputFile = output;
  }

  public TestModel(String input) {
    this.inputFile = input;
  }

  public void saveLabels() throws IOException {
    Path inputFileName = Paths.get(inputFile);
    Path outputFileName = Paths.get(outputFile);

    BufferedReader reader = Files.newBufferedReader(inputFileName,
        Charset.forName("UTF-8"));
    // Path outputFileName = Paths.get(outputFile);
    // try (Scanner sc = new Scanner(new File(inputFile))) {
    // Scanner sc = new Scanner(new File(inputFile));
    // while (sc.hasNextLine()) {
    String line;
    int i = 0;
    while ((line = reader.readLine()) != null) {
      String[] delims = line.split(" ");
      String label = delims[0];
      String name = Integer.toString(i);
      Path output = Paths.get(outputFile, name + ".out");
      PrintWriter fileWriter = new PrintWriter(
          Files.newBufferedWriter(output, Charset.forName("UTF-8")));
      fileWriter.write(label);
      
      // PrintWriter outputStream = new PrintWriter(outputFile);
      // outputStream.println(sc.next());// write first word from line
//      fileWriter.write(sc.next());
      fileWriter.close();
      i++;
//      sc.nextLine();// consume rest of text from that line
    }
  }
  // catch (IOException e) {
  // e.printStackTrace();
  // }

  public void removeLabels() throws IOException {
    Path inputFileName = Paths.get(inputFile);
    Path outputFileName = Paths.get(outputFile);

    BufferedReader reader = Files.newBufferedReader(inputFileName,
        Charset.forName("UTF-8"));
    // PrintWriter writer = new
    // PrintWriter(Files.newBufferedWriter(outputFileName));

    String line;
    int i = 0;
    while ((line = reader.readLine()) != null) {
      String content = line.substring(line.indexOf(" ") + 1);
      String name = Integer.toString(i);
      Path output = Paths.get(outputFile, name + ".geot");
      PrintWriter fileWriter = new PrintWriter(
          Files.newBufferedWriter(output, Charset.forName("UTF-8")));
      fileWriter.write(content);
      fileWriter.close();
      i++;

    }
  }

  public void compareLabels() throws IOException {
    File input1 = new File(inputFile);
    File input2 = new File(labels);
    for (File file1 : input1.listFiles()) {
      String id = file1.getName();
      if (id.equals(".DS_Store") || id.equals("..out") || id.equals(".out"))
        continue;
      String out1 = FileUtils.readFileToString(file1);
      File file2 = new File(labels + "/" + id);
      String out2 = FileUtils.readFileToString(file2);
      if (out1.indexOf("NOT_RELEVANT") > -1
          && out2.indexOf("NOT_RELEVANT") > -1) {
        trueNotRel++;
      } else if (out1.indexOf("NOT_RELEVANT") <= -1
          && out2.indexOf("NOT_RELEVANT") <= -1) {
        trueRel++;
      } else if (out1.indexOf("NOT_RELEVANT") > -1 //not rel output
          && out2.indexOf("NOT_RELEVANT") <= -1) { //relevant actually
        falseNotRel++;
      } else if (out1.indexOf("NOT_RELEVANT") <= -1
          && out2.indexOf("NOT_RELEVANT") > -1) {
        falseRel++;
      }
    }

  }

  public static void main(String[] args) throws IOException {

    String fileName = args[0];
    String outputName = args[1];

    // TestModel test = new TestModel(fileName, outputName);
    TestModel test = new TestModel(fileName, outputName);
    //test.saveLabels();
     //test.removeLabels();
    test.compareLabels();
    System.out.println("TNR: " + trueNotRel);
    System.out.println("TR: " + trueRel);
    System.out.println("FNR: " + falseNotRel);
    System.out.println("FR: " + falseRel);
    // System.out.println("MATCH: " + match);
    // System.out.println("NOT MATCH: " + notMatch);

  }

}
