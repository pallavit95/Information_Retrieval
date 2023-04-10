package com.ding.assignment_2.datamodels;

public class CranfieldDocument {
    public String docid = "";
    public String title = "";
    public String author = "";
    public String biblio = "";
    public String words = "";


    public CranfieldDocument(String docid, String title, String author, String biblio, String words) {
        this.docid = docid;
        this.title = title;
        this.author = author;
        this.biblio = biblio;
        this.words = words;
    }

    public CranfieldDocument() {
    }

}
