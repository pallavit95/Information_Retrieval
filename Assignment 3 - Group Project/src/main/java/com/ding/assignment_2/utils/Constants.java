package com.ding.assignment_2.utils;

public class Constants {

    // For Parsing

    private static final String BASE_PATH = "data/Assignment_Two - All/";
    private static final String FBIS_PATH = BASE_PATH + "fbis";
    private static final String LATIMES_PATH = BASE_PATH + "latimes";
    private static final String FR94_PATH = BASE_PATH + "fr94";
    private static final String FT_PATH = BASE_PATH + "ft";
//    private static final String DT_PATH = BASE_PATH + "dtds";

    public static String[] getDatasetPaths() {
        String[] paths = { FBIS_PATH, LATIMES_PATH, FR94_PATH, FT_PATH};
        return paths;
    }

    // Old down below - will be removed when there usage is
    public static final char DOT = '.';
    public static final char WORD_SEPARATOR = ' ';
    public static final char Index = 'I';
    public static final char Title = 'T';
    public static final char Author = 'A';
    public static final char Biblio = 'B';
    public static final char Words = 'W';

    // For File Paths
    // public static String docPath = "data/cran.all.1400";
    public static String queryPath = "data/Assignment_Two - All/topics";
    public static String INDEX_PATH = "index/";
    public static String STOPWORD_FILE = "stopwords.txt";
    public static String OUTPUT_FILE = "output/results.txt";

    // For Queries
    public static final int NUM_RESULTS = 1000;

}
