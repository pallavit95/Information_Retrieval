package com.ding.assignment_2.lucene_cranfield;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermAutomatonQuery;
import org.apache.lucene.search.TokenStreamToTermAutomatonQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ding.assignment_2.datamodels.SearchTopic;
import com.ding.assignment_2.parser.TopicParser;
import com.ding.assignment_2.utils.Constants;

public class Searcher {

	private static Logger logger = LoggerFactory.getLogger(Searcher.class);
	private static Directory directory;
	private static DirectoryReader indexReader;
	private static PrintWriter writer;
	private static IndexSearcher indexSearcher;
	private static Analyzer analyzer;

	/**
	 * Method to run queries on the indexer Takes in querypath, number of results to
	 * produce, analyzer, similarity
	 */
	public static void runQueries(String queryPath, Indexer.Analyzers analyserChoice,
			Indexer.Similarities similarityChoice) {
		try {
			initialise(analyserChoice, similarityChoice);

			ArrayList<SearchTopic> queries = TopicParser.parseQueries(queryPath);

			MultiFieldQueryParser queryParser = createMultiFieldQueryParser();

			logger.debug("Running search engine, max_hits set to: " + Constants.NUM_RESULTS);

			for (SearchTopic topic : queries) {
				BooleanQuery query = createQuery(topic, queryParser);
				if(query!=null)
					search(indexSearcher, query, writer, topic.number, queries.indexOf(topic),analyserChoice.toString() + similarityChoice);
			}

			close();
			logger.info("Searching complete output written to " + Constants.OUTPUT_FILE);

		} catch (IOException | ParseException exception) {
			logger.error("Error while running queries", exception);
		}
	}

	private static MultiFieldQueryParser createMultiFieldQueryParser() {
		HashMap<String, Float> boostMap = new HashMap<String, Float>();
		boostMap.put("title", 0.5f);
		boostMap.put("text", 10f);
		return new MultiFieldQueryParser(new String[] { "title", "text" }, analyzer, boostMap);
	}

	private static BooleanQuery createQuery(SearchTopic topic, MultiFieldQueryParser queryParser)
			throws ParseException {
		String queryStringTitle = "";
		String queryStringDescription = "";
		
		if(topic.title.length()>0)
			queryStringTitle = Constants.WORD_SEPARATOR + topic.title.trim();
		if(topic.description.length()>0)
			queryStringDescription = topic.description.trim();
		//queryString = QueryParser.escape(queryString);
		if(queryStringTitle.length()==0 || queryStringDescription.length()==0)
		{
			System.out.println("Null rcvd");
			return null;
		}
			
		//String[] filteredRelevance = filterNarrativeRelevance(QueryParser.escape(topic.narrative.trim()));
		List<String> filteredRelevance = filteredWords(QueryParser.escape(topic.narrative.trim()));
		System.out.println(filteredRelevance.size());
		for(String sen : filteredRelevance)
			queryStringDescription += sen + Constants.WORD_SEPARATOR;
		
		queryStringTitle = QueryParser.escape(queryStringTitle);
		queryStringDescription = QueryParser.escape(queryStringDescription);
		
		Query titleQuery = queryParser.parse(queryStringTitle);
		System.out.println(queryStringDescription);
		Query descriptionQuery = queryParser.parse(queryStringDescription);
		BoostQuery dq = new BoostQuery(descriptionQuery, (float) 3.5);
		BoostQuery tq = new BoostQuery(titleQuery, (float) 1.2);
		BooleanQuery.Builder query = new BooleanQuery.Builder();
		query.add(tq, Occur.SHOULD); // or Occur.SHOULD if this clause is optional
		query.add(dq, Occur.MUST); // or Occur.MUST if this clause is required
		return query.build();
	}
	
//	private static TokenStream getTokenStream(String input) throws IOException {
//        Tokenizer inputStream = new WhitespaceTokenizer();
//        inputStream.setReader(new StringReader(input));
//        SynonymMap.Builder builder = new SynonymMap.Builder(true);
//        return new SynonymGraphFilter(inputStream, builder.build(), true);
//    }
	
//	private static MultiPhraseQuery createQuery2(SearchTopic topic, Query queryParser)
//			throws ParseException {
//		String queryStringTitle = Constants.WORD_SEPARATOR + topic.title.trim();
//		String queryStringDescription = topic.description.trim();
//		List<String> filteredRelevance = filteredWords(QueryParser.escape(topic.narrative.trim()));
//		for(String sen : filteredRelevance)
//			queryStringDescription += Constants.WORD_SEPARATOR + sen;
//		
//		queryStringTitle = QueryParser.escape(queryStringTitle);
//		queryStringDescription = queryStringTitle + Constants.WORD_SEPARATOR + QueryParser.escape(queryStringDescription);
//		
////		Query descriptionQuery = queryParser.parse(queryStringDescription);
//		StringTokenizer tknzr = new StringTokenizer(queryStringDescription);
//		
//		MultiPhraseQuery.Builder query = new MultiPhraseQuery.Builder();
//		query.setSlop(2);
//		MultiPhraseQuery pq = (MultiPhraseQuery) queryParser;
//		Term[][] terms = pq.getTermArrays();
//		int[] positions = pq.getPositions();
//		for (int i = 0; i < terms.length; ++i) {
//			query.add(terms[i], positions[i]);
//		}
//		return query.build();
//	}

	private static void initialise(Indexer.Analyzers analyserChoice, Indexer.Similarities similarityChoice)
			throws IOException {
		createSearcher(similarityChoice);
		analyzer = Indexer.getAnalyzer(analyserChoice);
		createWriter();
	}

	private static void createSearcher(Indexer.Similarities similarityChoice) throws IOException {
		directory = FSDirectory.open(Paths.get(Constants.INDEX_PATH));
		indexReader = DirectoryReader.open(directory);
		indexSearcher = new IndexSearcher(indexReader);
		indexSearcher.setSimilarity(Indexer.getSimilarity(similarityChoice));
	}

	private static void createWriter() throws IOException {
		File resultFile = new File(Constants.OUTPUT_FILE);
		resultFile.getParentFile().mkdirs();
		writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());
	}

	private static void close() throws IOException {
		indexReader.close();
		writer.close();
		directory.close();
	}

	/**
	 * This method searches for hits for every query and writes the releance score
	 * along with the queryID,docId in the results file .
	 */
	public static void search(IndexSearcher indexSearcher, Query query, PrintWriter writer, String queryID,
			int queryIndex, String analyzerAndSimilarity) throws IOException {
		ScoreDoc[] hits = indexSearcher.search(query, Constants.NUM_RESULTS).scoreDocs;
		writeResults(hits, queryID, queryIndex, analyzerAndSimilarity);
	}

	public static void writeResults(ScoreDoc[] hits, String queryID, int queryIndex, String analyzerAndSimilarity)
			throws IOException {
		for (int i = 0; i < hits.length; i++) {
			Document hitDocument = indexSearcher.doc(hits[i].doc);
			writer.println(queryID + " Q" + queryIndex + " " + hitDocument.get("docNo") + " " + (i + 1) + " "
					+ hits[i].score + " " + analyzerAndSimilarity);
		}
	}

	public static List<String> filteredWords(String narrative) {
    	String[] sentences = narrative.split("[.;]");
    	List<String> impWords = new ArrayList<String>();
    	for(String sen : sentences) {
    		if(sen.contains("relevant") || sen.contains("significant"))
    		{
    			sen = sen.replace("document", "");
    			sen = sen.replace("documents", "");
    			sen = sen.replace("relating", "");
    			sen = sen.replace("pertaining", "");
    			sen = sen.replace("discussion", "");
    			impWords.add(sen);
    		}
    			
    	}
    	return impWords;
    }
	
    private String createExpandedQueryString(Query query) {
        String[] expandedQueryString = query.toString().split("contents:");
        StringBuilder sb = new StringBuilder();
        Vector<String> sbTerms = new Vector<String>();
        int count = 0;
        for (int i = 0; i < expandedQueryString.length; i++) {
            if (!sbTerms.contains(expandedQueryString[i]) &&
                    !expandedQueryString[i].chars().anyMatch(Character::isDigit) &&
                    count < 20) {
                count++;
                sbTerms.add(expandedQueryString[i]);
                sb.append(expandedQueryString[i]);
            }
        }
        return sb.toString();
    }
    
}
