package com.ding.assignment_2.datamodels;

public class SearchTopic {
    public String number = "";
    public String title = "";
    public String description = "";
    public String narrative = "";

    public SearchTopic() {

    }

    public SearchTopic(String number, String title, String description, String narrative) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.narrative = narrative;
    }
}
