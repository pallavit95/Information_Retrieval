package com.ding.assignment_2.parser;

import com.ding.assignment_2.datamodels.SearchTopic;
import com.ding.assignment_2.utils.Constants;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class TopicParser {
    public static ArrayList<SearchTopic> parseQueries(String topicFilePath) {
        ArrayList<SearchTopic> topicArrayList = new ArrayList<>();

        try {
            // Open the topic file
            BufferedReader topicFileReader = new BufferedReader(new InputStreamReader(Files.newInputStream(new File(topicFilePath).toPath())));

            String line;
            boolean readingDescription = false;
            boolean readingNarrative = false;

            // Read the topic file line by line
            SearchTopic newTopic = new SearchTopic();
            while((line = topicFileReader.readLine()) != null) {
                // Some lines have no data
                // Ignore them
                line = line.trim();
                if(line.length() == 0)
                    continue;

                // Read each field's identifier and process them accordingly
                if(line.startsWith("<num>")) {
                    readingDescription = false;
                    readingNarrative = false;
                    newTopic.number = line.substring("<num> Number: ".length());

                } else if(line.startsWith("<title>")) {
                    readingDescription = false;
                    readingNarrative = false;

                    newTopic.title = line.substring("<title> ".length());

                } else if(line.startsWith("<desc>")) {
                    readingDescription = true;
                    readingNarrative = false;

                } else if(line.startsWith("<narr>")) {
                    readingDescription = false;
                    readingNarrative = true;

                } else if(line.startsWith("</top>")) {
                    readingNarrative = false;
                    topicArrayList.add(newTopic);
                    newTopic = new SearchTopic();

                // If the line has no identifier, it's a description or a
                // narrative
                } else if(readingDescription) {
                	line = line.trim();
                    newTopic.description += line + Constants.WORD_SEPARATOR;

                } else if(readingNarrative) {
                	line = line.trim();
                    newTopic.narrative += line + Constants.WORD_SEPARATOR;
                }
            }

            topicFileReader.close();
        } catch(IOException e) {
            System.err.println("Error reading the topic file at: " + topicFilePath);
        }


        return topicArrayList;
    }
}
