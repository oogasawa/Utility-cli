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

    String      synopsis = "java -jar Utility-cli-fat.jar <command> <options>";
    CliCommands cmds     = new CliCommands();

    
    public static void main( String[] args )
    {
        App app = new App();
        
        app.setupCommands();

        try {

            CommandLine cl = app.cmds.parse(args);
            String command = app.cmds.getCommand();
            
            if (command == null) {
                app.cmds.printCommandList(app.synopsis);
            }
            else if (app.cmds.hasCommand(command)) {
                app.cmds.execute(command, cl);
            }
            else {
                System.err.println("The specified command is not available: " + app.cmds.getCommand());
                app.cmds.printCommandList(app.synopsis);
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage() + "\n");
            app.cmds.printCommandHelp(app.cmds.getCommand());
        } 
            
    
    }


    
    public void setupCommands() {
    
        lineGetColumnsCommand();
        tsvGetColumnsCommand();

    }


    public void lineGetColumnsCommand() {
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


    
        this.cmds.addCommand("line:get_columns", opts,
                       "Get columns from each line.",
                       (CommandLine cl)-> {
                            String[] columnNumbers = cl.getOptionValues("column");
                            String delimiter = cl.getOptionValue("delimiter", "\\s+");

                            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                            reader.lines()
                                .map((String line) -> line.split(delimiter)) // line to columns
                                .map((String[] columns) -> ColumnSelector.select(columns, columnNumbers))
                                .forEach((List<String> selected) -> ColumnSelector.println(selected));
                       });

    }


    
    public void tsvGetColumnsCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("column")
                        .option("c")
                        .longOpt("column")
                        .hasArg(true)
                        .argName("column")
                        .desc("Column number (0, 1, 2, ...)")
                        .required(true)
                        .build());



        this.cmds.addCommand("tsv:get_columns", opts,
                       "Get columns from TSV file.",
                        (CommandLine cl)-> {
                            String[] columnNumbers = cl.getOptionValues("column");
                
                            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                            reader.lines()
                                .map((String line)->StringUtil.splitByTab(line))
                                .map((List<String> columns)->ColumnSelector.select(columns, columnNumbers))
                                .forEach((List<String> selected)->ColumnSelector.println(selected));
                        });

    }


}

