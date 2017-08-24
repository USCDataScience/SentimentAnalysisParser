package org.memex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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

import com.mashape.unirest.http.exceptions.UnirestException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TopicsExtraction {

  public static void main(String[] args) throws IOException, UnirestException, SAXException, TikaException {
    // HttpResponse<String> response =
    // Unirest.post("http://api.meaningcloud.com/topics-2.0")
    // .header("content-type", "application/x-www-form-urlencoded")
    // .body("key=fa5a3bee198c119d21b4adcae7ac4ab8&lang=en&txt=&url=&doc=./nasa-scope-1&tt=a")
    // .asString();
    String doc = args[0];
    File file = new File(doc);
    OkHttpClient client = new OkHttpClient();
    
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
    parseContext.set(Parser.class, parser);
    BodyContentHandler handlerOCR = new BodyContentHandler(-1);
    FileInputStream stream = new FileInputStream(file);
    Metadata metadata = new Metadata();
    
    pdfParser.parse(stream, handlerOCR, metadata, parseContext);
    stream.close();
    
    String content = handlerOCR.toString();

    // fa5a3bee198c119d21b4adcae7ac4ab8
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType,
        "key=fa5a3bee198c119d21b4adcae7ac4ab8&lang=en&txt=" + content + "&url=&doc=&tt=c");
    Request request = new Request.Builder()
        .url("http://api.meaningcloud.com/topics-2.0").post(body)
        .addHeader("content-type", "application/x-www-form-urlencoded") // application/x-www-form-urlencoded
        .build();

    Response response1 = client.newCall(request).execute();

    // System.out.print(response1.toString());
    System.out.print(response1.body().string());

  }

}
