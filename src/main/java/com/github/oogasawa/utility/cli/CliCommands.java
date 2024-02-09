package com.github.oogasawa.utility.cli;

import java.util.TreeMap;
import java.util.function.Consumer;

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


    TreeMap<String, Consumer<CommandLine>> commandActionMap = new TreeMap<>();
    
    
    
    Options universalOptions;

    /** Command name which is specified in the given command line. */
    String command;

    /** Default constructor. */
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
    public void addCommand(String command, Options options, Consumer<CommandLine> action) {
        commands.put(command, options);
        commandActionMap.put(command, action);
        
    }

    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description) {
        commands.put(command, options);
        commandDescMap.put(command, description);
    }


    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description, Consumer<CommandLine> action) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandActionMap.put(command, action);
    }

    
    
    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description, String example) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandExampleMap.put(command, example);
    }

    
    /**  Add options that is associated with the command. */
    public void addCommand(String command, Options options, String description, String example, Consumer<CommandLine> action) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandExampleMap.put(command, example);
        commandActionMap.put(command, action);
    }


    
    /** Add options that can be used independently of the cli commands. */
    public void addUniversalOption(Option option) {
        this.universalOptions.addOption(option);
    }



    public void execute(String command, CommandLine cl) {

        Consumer<CommandLine> action = this.commandActionMap.get(command);
        if (action != null) {
            action.accept(cl);
        }
    }



    
    public String getCommand() {
        return this.command;
    }


    public boolean hasCommand(String command) {
        return this.commands.containsKey(command);
    }

    

    public CommandLine parse(String[] args) throws ParseException {

        CommandLine cl = null;

        if (args.length > 0) {

            String command = args[0];
            this.command = command;

            Options options = this.commands.get(command);
            if (options == null) {// not matched
                CommandLineParser parser = new DefaultParser();
                cl = parser.parse(this.universalOptions, args);
            } else {
                CommandLineParser parser = new DefaultParser();
                cl = parser.parse(options, args);
            }
        }

        return cl;
    }


    public void printCommandHelp(String command) {

        Options options = this.commands.get(command);

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(command, options, false);
        System.out.println();

        if (this.commandExampleMap.size() > 0) {
            System.out.println("\n## Examples\n");
            System.out.println(this.commandExampleMap.get(command));
        }

    }

    
    public void printCommandList(String synopsis) {

        System.out.println("\n## Usage\n");
        System.out.println(synopsis);
        System.out.println("");

        System.out.println("## Commands\n");
        commandDescMap.forEach((String command, String description)->{
                if (command.length()<16) {
                    command = String.format("%-16s", command);
                }
                else {
                    int width = ((command.length() + 4)/4)*4;
                    String f = "%-" + String.valueOf(width) + "s";
                    command = String.format(f, command);
                }
                System.out.println(command + description);
            });
        System.out.println();
    }

    
    
    public void setCommandExample(String command, String example) {
        this.commandExampleMap.put(command, example);
    }



}


