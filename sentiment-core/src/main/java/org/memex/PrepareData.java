package org.memex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PrepareData {

	public static void main(String[] args) {
		String filaName = args[0];
		String outputFile = args[1];
		// count labels
		Map<String, Integer> countLabels = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filaName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				String label = tokens[0];
				Integer count = countLabels.get(label);
				if(count == null) {
					count = 0;
				}
				countLabels.put(label, count + 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// find min
		int min = Integer.MAX_VALUE;
		for(Integer count : countLabels.values()) {
			min = Math.min(min, count);
		}
		// make distribution
		countLabels.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(filaName));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				String label = tokens[0];
				Integer count = countLabels.get(label);
				if(count == null) {
					count = 0;
				}
				if(count < min) {
					writer.write(line);
					writer.write("\n");
					countLabels.put(label, count + 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
