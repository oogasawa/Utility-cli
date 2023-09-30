package com.github.oogasawa.utility.cli;

import java.io.BufferedReader;
import java.util.List;
import java.util.logging.Logger;
import java.io.InputStreamReader;

import com.github.oogasawa.utility.cli.line.ColumnSelector;
import com.github.oogasawa.utility.types.string.StringUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



public class App
{

    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    public static void main( String[] args )
    {

        var helpStr = "java -jar Utility-cli-fat.jar <command> <options>";
        var cli = new CliCommands();

        cli.addCommand("line:get_columns", tsvGetColumnsOptions(),
                       "Get columns from each line.");

        
        cli.addCommand("tsv:get_columns", tsvGetColumnsOptions(),
                       "Get columns from TSV file.");


        try {

            CommandLine cmd = cli.parse(args);

            if (cli.getCommand() == null) {
                // check universal options.
                if (cmd.hasOption("h") || cmd.hasOption("help")) {
                    cli.printHelp(helpStr);
                }

            }
            else if (cli.getCommand().equals("line:get_columns")) {
                
                String[] columnNumbers = cmd.getOptionValues("column");
                String delimiter = cmd.getOptionValue("delimiter", "\\s+");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                reader.lines()
                    .map((String line)->line.split(delimiter)) // line to columns
                    .map((String[] columns)->ColumnSelector.select(columns, columnNumbers))
                    .forEach((List<String> selected)->ColumnSelector.println(selected));
            }

            else if (cli.getCommand().equals("tsv:get_columns")) {

                String[] columnNumbers = cmd.getOptionValues("column");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                reader.lines()
                    .map((String line)->StringUtil.splitByTab(line))
                    .map((List<String> columns)->ColumnSelector.select(columns, columnNumbers))
                    .forEach((List<String> selected)->ColumnSelector.println(selected));

            }
            else if (cli.getCommand().equals("dummy")) {

            }
            else {
                cli.printHelp(helpStr);
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            cli.printHelp(helpStr);
        } 
            
    
    }



    public static Options lineGetColumnsOptions() {
        Options opts = new Options();

        opts.addOption(Option.builder("column")
                        .option("c")
                        .longOpt("column")
                        .hasArg(true)
                        .argName("column")
                        .desc("Column number (0, 1, 2, ...)")
                        .required(true)
                        .build());

        opts.addOption(Option.builder("delimiter")
                        .option("d")
                        .longOpt("delimiter")
                        .hasArg(true)
                        .argName("delimiter")
                        .desc("Delimiter of the columns. (default: \"\\s+\")")
                        .required(false)
                        .build());


        return opts;
    }


    
    public static Options tsvGetColumnsOptions() {
        Options opts = new Options();

        opts.addOption(Option.builder("column")
                        .option("c")
                        .longOpt("column")
                        .hasArg(true)
                        .argName("column")
                        .desc("Column number (0, 1, 2, ...)")
                        .required(true)
                        .build());

        return opts;
    }


}

