package com.github.oogasawa.utility.cli;

import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



public class CliCommands {

    /** Map of command name and its options. */
    TreeMap<String, Options> commands;

    /** Map of command name and its description. */
    TreeMap<String, String> commandDescMap = new TreeMap<>();

    TreeMap<String, String> commandExampleMap = new TreeMap<>();

    
    Options universalOptions;

    /** Command name which is specified in the given command line. */
    String command;

    public CliCommands() {
        commands = new TreeMap<String, Options>();


        this.universalOptions = new Options();
        this.universalOptions.addOption(Option.builder()
                                        .option("h")
                                        .longOpt("help")
                                        .hasArg(false)
                                        .argName("help")
                                        .desc("print help message")
                                        .required(false)
                                        .build());
    }

    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options) {
        commands.put(command, options);
    }


    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description) {
        commands.put(command, options);
        commandDescMap.put(command, description);
    }

    
    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description, String example) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandExampleMap.put(command, example);
    }

    

    /** Add options that can be used independently of the cli commands. */
    public void addUniversalOption(Option option) {
        this.universalOptions.addOption(option);
    }


    public String getCommand() {
        return this.command;
    }



    public CommandLine parse(String[] args) throws ParseException {

        CommandLine cmd = null;

        if (args.length == 0) {
            throw new ParseException("ERROR: No arguments.");
        }

        String command = args[0];
        this.command = command;

        var options = this.commands.get(command);
        if (options == null) {// not matched
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(this.universalOptions, args);
        }
        else {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        }


        return cmd;
    }




    public void printHelp(String programName) {
        System.out.println("\n## Synopsis\n    " + programName + "\n");

        System.out.println("\n## Commands\n");

        for (String command : this.commands.keySet()) {

            if (this.commandDescMap.containsKey(command)) {
                System.out.println(command + "\n    " + this.commandDescMap.get(command));
            }
            else {
                System.out.println(command + "\n    " + "(No description)");
            }
            
        }

        System.out.println("\n## Options\n");
        
        for (String command : this.commands.keySet()) {
            Options options = this.commands.get(command);

            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(command, options, true);
            System.out.println("");
        }


        if (this.commandExampleMap.size() > 0) {
            System.out.println("\n## Examples\n");
            for (String command : this.commandExampleMap.keySet()) {
                System.out.println("\n\n###" + command);
                System.out.println(this.commandExampleMap.get(command));
            }
        }

        
    }


    
    public void setCommandExample(String command, String example) {
        this.commandExampleMap.put(command, example);
    }



}


