package com.ding.assignment_2.parser;

import com.ding.assignment_2.datamodels.DocumentModel;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.lucene.document.Document;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetParser {

    public static Logger logger = LoggerFactory.getLogger(DatasetParser.class);

    /**
     * Method to Parse the Documents at a given path
     * Takes in filePath as parameter
     * 
     * @return a list of LuceneDocuments object
     */

    public static ArrayList<Document> parse(String filepath) throws IOException {

        ArrayList<Document> parsedDocuments = new ArrayList<>();
        DocumentModel doc;

        File newsDirectory = new File(filepath);
        File[] subFiles = newsDirectory.listFiles();
        File[] files;

        // Make sure we were able to read the document files
        if(subFiles == null) {
            logger.error("No documents could be found in the path " + filepath);
            return parsedDocuments;
        }

        for (File myFile : subFiles) {
            if (!myFile.isDirectory()) {
                org.jsoup.nodes.Document parsedDoc = Jsoup.parse(myFile, "UTF-8");
                Elements documents = parsedDoc.select("doc");

                for (Element document : documents) {

                    String docNo = document.select("docno").text();

                    // The documents have either title or headline
                    String title = document.select("ti").text() + document.select("headline").text();
                    title.replaceAll("[^\\w,?\";. -]", "");

                    String text = document.select("text").text();
                    text.replaceAll("[^\\w,?\";. -]", "");

                    doc = new DocumentModel(docNo, title, text);
                    parsedDocuments.add(createLuceneDocument(doc));
                }
            } else {
                files = myFile.listFiles();
                for (File file : files) {
                    org.jsoup.nodes.Document parsedDoc = Jsoup.parse(file, "UTF-8");
                    Elements documents = parsedDoc.select("doc");

                    for (Element document : documents) {

                        String docNo = document.select("docno").text();
                        String title = document.select("ti").text() + document.select("headline").text();
                        title.replaceAll("[^\\w,?\";. -]", "");
                        String text = document.select("text").text();
                        text.replaceAll("[^\\w,?\";. -]", "");

                        doc = new DocumentModel(docNo, title, text);
                        parsedDocuments.add(createLuceneDocument(doc));
                    }
                }
            }
        }

        return parsedDocuments;
    }

    /**
     * Method to parse all datasets given a list of file paths and return a List of
     * LuceneDocuments
     * Takes in String array of filepaths as parameter
     * 
     * @return an ArrayList of LuceneDocument objects
     */

    public static ArrayList<Document> parseDatasets(String[] datasetPaths) throws IOException {
        ArrayList<Document> parsedDocuments = new ArrayList<>();
        for (String path : datasetPaths) {
            parsedDocuments.addAll(parse(path));
        }
        return parsedDocuments;
    }

    /**
     * Method to create LuceneDocument from DocumentModel object
     * Takes in DocumentModel object as parameter
     * 
     * @return a Lucene Document object
     */
    private static Document createLuceneDocument(DocumentModel doc) {
        Document document = new Document();
//        System.out.println(doc.docNo + "##" +doc.title + "##" + doc.text);
        document.add(new StringField("docNo", doc.docNo, Field.Store.YES));
        document.add(new TextField("title", doc.title, Field.Store.YES));
        document.add(new TextField("text", doc.text, Field.Store.YES));
        return document;
    }
}