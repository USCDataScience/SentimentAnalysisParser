package org.memex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class NASAStandardsTrainLast {
	public static void parse(String inputName, String outputName) throws IOException, SAXException, TikaException {

		Path outputFileTemp = Paths.get(outputName);
		Charset encoding = Charset.forName("UTF-8");
		PrintWriter outputStream = new PrintWriter(Files.newBufferedWriter(outputFileTemp, encoding));
		File input = new File(inputName);

		Pattern div = Pattern.compile("</?div(|\\s+[^>]+)>");
		// Pattern p = Pattern.compile("</?p(|\\s+[^>]+)>");
		Pattern meta = Pattern.compile("</?meta(|\\s+[^>]+)>");

		MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

		TesseractOCRParser ocrParser = new TesseractOCRParser();
		PDFParser pdfParser = new PDFParser();

		CompositeParser parser = new CompositeParser(mediaTypeRegistry, Arrays.asList(ocrParser, pdfParser));

		TesseractOCRConfig config = new TesseractOCRConfig();
		PDFParserConfig pdfConfig = new PDFParserConfig();
		pdfConfig.setExtractInlineImages(true);

		ParseContext parseContext = new ParseContext();
		parseContext.set(TesseractOCRConfig.class, config);
		parseContext.set(PDFParserConfig.class, pdfConfig);
		// need to add this to make sure recursive parsing happens!
		parseContext.set(Parser.class, parser);
		
		Pattern patternStart = Pattern.compile("1\\.\\s*SCOPE");
	    //Pattern patternEnd = Pattern.compile("2\\.\\s*APPLICABLE DOCUMENTS");
		Pattern patternEnd = Pattern.compile("\n2\\.\\s*[A-Z]+");


		for (File file : input.listFiles()) {

			String fileName = file.getName();
			int extIndex = fileName.lastIndexOf('.');
			String name = fileName.substring(0, extIndex);
			String extension = fileName.substring(extIndex + 1).toLowerCase();

			if ("pdf".equals(extension)) {
				BodyContentHandler handlerOCR = new BodyContentHandler(-1);
				FileInputStream stream = new FileInputStream(file);
				Metadata metadata = new Metadata();

				pdfParser.parse(stream, handlerOCR, metadata, parseContext);
				stream.close();
				
				String content = handlerOCR.toString();
				
				Matcher scopeMatcher = patternStart.matcher(content);
				int start = 0;
				if(scopeMatcher.find()) {
					start = scopeMatcher.end() + 1;
					 if(scopeMatcher.find()) {
						 start = scopeMatcher.end() + 1;
					 }
				}
				
				Matcher endMatcher = patternEnd.matcher(content);
				int end = content.length();
				endMatcher.region(start, end);
				
				if(endMatcher.find()) {
					end = endMatcher.start() - 1;
				}
				
				//============
				String fName = file.getName().replace(".pdf", "");
		      String[] fileN = {};
				String label = "";
				
				      if (fName.contains("-")) {
				        fileN = fName.split("-");
				        label = fileN[fileN.length - 1];
				      } else if (fName.contains("_")) {
				        fileN = fName.split("_");
				        label = fileN[fileN.length - 1];
				      } else {
				        label = fName;
				      }
				//============
				

				int nameIndex = content.indexOf("NASA-STD-");
				//int nameSpace = Math.min(content.indexOf(' ', nameIndex), content.indexOf('\n', nameIndex));
				//String documentId = content.substring(nameIndex, nameSpace);
				//String label = content.substring("NASA-STD-".length() + nameIndex, nameSpace);
				String scope = content.substring(start, end).trim().replaceAll("\n", " ").replaceAll("\t", " ");
				
				/*
				int start = content.lastIndexOf("\n1. ");
				int scopeIndex = content.indexOf("SCOPE", start);
				while(scopeIndex - start > 10) {
					start = content.lastIndexOf("\n1. ", start);
					scopeIndex = content.indexOf("SCOPE", start);
				}
				
				int end = content.indexOf("\n2. ", start);
				
				StringBuilder scope = new StringBuilder();
				int pageIndex;
				do {
					pageIndex = content.indexOf(" of ", start);
					if(pageIndex == -1) {
						scope.append( content.substring(start, end) );
					} else if(pageIndex < end){
						int currentPage = Math.max(content.lastIndexOf(" ", pageIndex - 1), content.lastIndexOf("\n", pageIndex - 1));
						int totalPage = Math.min(content.indexOf(" ", pageIndex + " of ".length() + 1), content.indexOf("\n", pageIndex + " of ".length() + 1));
						String current = content.substring(currentPage + 1, pageIndex);
						String total = content.substring(pageIndex + " of ".length(), totalPage);
						
						try {
							Integer.valueOf(current);
							Integer.valueOf(total);
							
							int lastIndex = content.lastIndexOf(documentId, currentPage);
							// footer
							if(currentPage - lastIndex <= 100) {
								currentPage = lastIndex;
							}
							
							scope.append( content.substring(start, currentPage) );
							start = totalPage + total.length() + 1;
						} catch(NumberFormatException e) {
							scope.append( content.substring(start, totalPage + 1) );
							start = totalPage + 1;
						}
					} else {
						scope.append( content.substring(start, end) );
					}
				} while(pageIndex != -1 && pageIndex < end && start < end);
				*/
				
//				int documentIndex = content.indexOf(documentId, start);
//				int pageIndex = content.indexOf(ch, documentIndex);
				
			/*
				int lastIndex = content.lastIndexOf(documentId, end);
				if(end - lastIndex <= 100) {
					end = lastIndex;
				}
				String scope = content.substring(start, end).trim();
		    */
				// int start = content.indexOf("1. scope");// + "<p>1.
				// scope".length();
				// int end = content.indexOf("2. applicable documents") - 1;
				// int start = content.indexOf("<p>1. scope");// + "<p>1.
				// scope".length();
				// int end = content.indexOf("<p>2. applicable documents") -
				// 1;// +
				// "2.
				// applicable
				// documents".length()-1;

				// for (int i = start; i <= end; i++) {
				
				
				
				// .replaceAll("<p>", " ")
				// .replaceAll("</p>", " ").replaceAll("<p />", " ")
				// .replaceAll("\n", " ");
				// }

				// System.out.println(content);

				outputStream.write(label + " " + scope + "\n");

				
				// xhtmlHandler.startDocument();
				// xhtmlHandler.endDocument();

				// System.out.println(xhtmlHandler.toString());

				// outputStream.write(label + " " + content + "\n");

				// //parsing the document using PDF parser
				// PDFParser pdfparser = new PDFParser();
				// pdfparser.parse(inputstream, handlerNormal, metadata,
				// pcontext);
				//
				// String contentNormal = handlerNormal.toString();
			}
		}
		outputStream.close();

		// BufferedReader reader = Files.newBufferedReader(outputFileTemp,
		// encoding);
		// Path outputFileName = Paths.get(outputName);
		// PrintWriter outputStreamFinal = new PrintWriter(
		// Files.newBufferedWriter(outputFileName, encoding));
		// String line;
		// int i = 0;
		// while ((line = reader.readLine().toLowerCase().replaceAll("\t", " "))
		// !=
		// null) {
		// if (line.equals("<p>1. scope")) {
		// System.out.println("HEY " + i);
		// i++;
		// }
		// }
	}

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
	public static void main(String[] args) throws IOException, SAXException, TikaException {

		parse(args[0], args[1]);

	}
}
