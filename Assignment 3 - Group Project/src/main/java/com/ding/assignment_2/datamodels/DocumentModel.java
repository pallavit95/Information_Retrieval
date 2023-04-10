package com.ding.assignment_2.datamodels;

public class DocumentModel {

    public String docNo = "";
    public String title = "";
    public String text = "";

    public DocumentModel() {

    }
    public DocumentModel(String docNo, String title, String text) {
        this.docNo = docNo;
        this.title = title;
        this.text = text;
    }

}
