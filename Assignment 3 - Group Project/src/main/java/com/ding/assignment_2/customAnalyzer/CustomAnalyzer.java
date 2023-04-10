package com.ding.assignment_2.customAnalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.ding.assignment_2.utils.Constants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.HyphenatedWordsFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAnalyzer extends Analyzer {
	
    public static Logger logger = LoggerFactory.getLogger(CustomAnalyzer.class);
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
//        tokenStream = new FlattenGraphFilter(tokenStream)
//        tokenStream = new EnglishPossessiveFilter(tokenStream);
//        tokenStream = new LowerCaseFilter(tokenStream);
//        tokenStream = new TrimFilter(tokenStream);
//        tokenStream = new StopFilter(tokenStream, getStopWords());
////        tokenStream = new SynonymGraphFilter(tokenStream, new SynonymMap(), false);
//        
////        tokenStream = new HyphenatedWordsFilter(tokenStream);
//        tokenStream = new KStemFilter(tokenStream);
//        tokenStream = new PorterStemFilter(tokenStream);
//
//
//
//        return new TokenStreamComponents(tokenizer, tokenStream);
//        tokenStream = new FlattenGraphFilter(new WordDelimiterGraphFilter(tokenStream,
//                WordDelimiterGraphFilter.SPLIT_ON_NUMERICS
//                        | WordDelimiterGraphFilter.GENERATE_WORD_PARTS
//                        | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS
//                        | WordDelimiterGraphFilter.PRESERVE_ORIGINAL,
//                null));
        tokenStream = new TrimFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        //filter = new StopFilter(filter, stopwords);
        tokenStream = new KStemFilter(tokenStream);
        //return new TokenStreamComponents(tokenizer, filter);
//        tokenStream = new StopFilter(tokenStream, getStopWords());
        tokenStream = new SnowballFilter(tokenStream, "English");

        return new TokenStreamComponents(tokenizer, tokenStream);
    }
    
    private CharArraySet getStopWords(){
        CharArraySet stopwords = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(Constants.STOPWORD_FILE));
            String[] words = new String(encoded, StandardCharsets.UTF_8).split("\n");
            stopwords =  new CharArraySet(Arrays.asList(words), true);
        } catch (IOException ioe) {
            logger.error("Error reading stopwords file" + Constants.STOPWORD_FILE, ioe);
        }
        return stopwords;
    }
}

