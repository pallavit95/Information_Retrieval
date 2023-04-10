package com.ding.assignment_2.lucene_cranfield;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ding.assignment_2.utils.Constants;

public class App {

    public static Logger logger = LoggerFactory.getLogger(App.class);

    // Options for Command line run
    private static String HELP = "h";
    private static String HELP_LONG = "help";

    private static String ANALYZER = "a";
    private static String ANALYZER_LONG = "analyzer";

    private static String SIMILARITY = "s";
    private static String SIMILARITY_LONG = "similarity";

    private static String KEEP_INDEX = "k";
    private static String KEEP_INDEX_LONG = "keep-existing-index";

    public static void main(String[] args) throws Exception {

        // parse the arguments
        Options options = getOption();
        CommandLineParser parser = new DefaultParser();
        Indexer.Analyzers analyzer = Indexer.Analyzers.CUSTOM;
        Indexer.Similarities similarity = Indexer.Similarities.DFI;
        Boolean keepIndex = false;

        try {
            CommandLine commandLine = parser.parse(options, args);
            if (args.length == 0) {
                logger.warn("No command line arguments passed. Using Default Analyzer and Similarity function");
            } else {
                if (commandLine.hasOption(HELP) || commandLine.hasOption(HELP_LONG)) {
                    getHelp(options);
                    // end the program
                    return;
                }
                // Parse the analyzer from arguments and assign it to analyzer variable
                if (commandLine.hasOption(ANALYZER) || commandLine.hasOption(ANALYZER_LONG)) {
                    analyzer = Indexer.Analyzers.fromName(commandLine.getOptionValue(ANALYZER, analyzer.getType()));
                }
                // Parse the similarity from arguments and assign it to similarity variable
                if (commandLine.hasOption(SIMILARITY) || commandLine.hasOption(SIMILARITY_LONG)) {
                    similarity = Indexer.Similarities
                            .fromName(commandLine.getOptionValue(SIMILARITY, similarity.getType()));
                }
                if (commandLine.hasOption(KEEP_INDEX) || commandLine.hasOption(KEEP_INDEX_LONG)) {
                    keepIndex = true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception while parsing arguments...");
            System.out.println(e);
            getHelp(options);
        }
        if (true) {
            logger.info("------------------INDEXING DOCUMENTS----------------------");
            Indexer.createIndex(analyzer, similarity);
        }
        logger.info("----------------------RUNNING QUERIES ON INDEXER----------------------");
        Searcher.runQueries(Constants.queryPath, analyzer, similarity);
        logger.info("--------------------------COMPLETE--------------------------");
    }

    /**
     * get Options for Command line arguments
     * 
     * @return Options
     */
    private static Options getOption() {
        final Options options = new Options();
        options.addOption(HELP, HELP_LONG, false, "Help");
        options.addOption(ANALYZER, ANALYZER_LONG, true, "Analyzer you want to use, choose from " +
                "(whitespace | simple | standard | english | custom)");
        options.addOption(SIMILARITY, SIMILARITY_LONG, true, "Similarity you want to use, choose from" +
                "(classic | boolean | bm25 | lmds)");
        options.addOption(KEEP_INDEX, KEEP_INDEX_LONG, false,
                "Do not recreate index (for use in debugging and testing where the indexing isn't changed)");
        return options;
    }

    private static void getHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(100);
        helpFormatter.printHelp("Lucene Search App", options);
    }
}
