package com.github.oogasawa.utility.cli;

import java.nio.file.Path;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



public class App
{

    String      synopsis = "java -jar Utility-cli-VERSION-fat.jar <command> <options>";
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


    
    /** Calls all command definition methods
     * to register the command name, its command line options
     * and its action in the CliCommands object.
     */
    public void setupCommands() {
    
        differenceCommand();
        filterCommand();
        getColumnsCommand();
        splitCommand();

    }


    // ------------------------------------------------------------
    // Command definitions (consisting of a name definition,
    // a command line option definition, and a command behavior definition)
    // can be written together using the command definition method,
    // which facilitates structurization and readability.
    // 
    // The command definition method registers
    // the command name, its command line options
    // and its action in the CliCommands object.
    // ------------------------------------------------------------


    
    public void differenceCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("file1")
                        .option("f1")
                        .longOpt("file1")
                        .hasArg(true)
                        .argName("file1")
                        .desc("f1 of the set difference f1 - f2")
                        .required(true)
                        .build());

        opts.addOption(Option.builder("file2")
                        .option("f2")
                        .longOpt("file2")
                        .hasArg(true)
                        .argName("file2")
                        .desc("f2 of the set difference f1 - f2")
                        .required(true)
                        .build());

    
        this.cmds.addCommand("difference", opts,
                       "Calculate the difference between two sets.",
                       (CommandLine cl)-> {
                            String file1 = cl.getOptionValue("file1");
                            String file2 = cl.getOptionValue("file2");
                            SetOperation s = new SetOperation();
                            TreeSet<String> set1 = s.read(Path.of(file1));
                            TreeSet<String> set2 = s.read(Path.of(file2));
                            TreeSet<String> result = s.difference(set1, set2);
                            s.print(result);
                       });

    }



    
    public void filterCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("column")
                       .option("c")
                       .longOpt("column")
                       .hasArg(true)
                       .argName("column")
                       .desc("")
                       .required(false)
                       .build());

        
        opts.addOption(Option.builder("regex")
                       .option("r")
                       .longOpt("regex")
                       .hasArg(true)
                       .argName("regex")
                       .desc("")
                       .required(true)
                       .build());
        
        
        this.cmds.addCommand("filter", opts,
                       "Filter lines using given conditions.",
                       (CommandLine cl)-> {
                            String columnsStr = cl.getOptionValue("columns");
                            StdinOperation.getColumns(columnsStr);
                       });

    }




    
    
    
    public void getColumnsCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("columns")
                       .option("c")
                       .longOpt("columns")
                       .hasArg(true)
                       .argName("columns")
                       .desc("Comma-separated list of columns to retrieve. (e.g. \"3,0,5-10\")")
                       .required(true)
                       .build());

        
        this.cmds.addCommand("getColumns", opts,
                       "Choose columns from each line (tab delimited).",
                       (CommandLine cl)-> {
                            String columnsStr = cl.getOptionValue("columns");
                            StdinOperation.getColumns(columnsStr);
                       });

    }



    
    
    
    public void splitCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("delimiter")
                        .option("d")
                        .longOpt("delimiter")
                        .hasArg(true)
                        .argName("file1")
                        .desc("Delimiter of fields. (e.g. \" +\")")
                        .required(false)
                        .build());

        
        this.cmds.addCommand("split", opts,
                       "Split each line into fields.",
                       (CommandLine cl)-> {
                            String delimiter = cl.getOptionValue("delimiter", "\\t" );
                            StdinOperation.splitLines(delimiter);
                       });

    }
    

}
