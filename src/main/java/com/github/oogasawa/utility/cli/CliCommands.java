package com.github.oogasawa.utility.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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


    /** 
     * A map that associates each command name with its options.
     * This map contains all available command names.
     */
    private TreeMap<String, Options> commands = new TreeMap<>();

    /** 
     * A map that stores command names and their descriptions.
     * This map only contains entries for commands that have descriptions.
     * Commands without descriptions are not included in this map.
     */
    private TreeMap<String, String> commandDescMap = new TreeMap<>();

    /** 
     * A map that associates a command name with its corresponding action.
     * This map only includes commands that have an associated action.
     * Commands without an action are not stored in this map.
     */
    private TreeMap<String, Consumer<CommandLine>> commandActionMap = new TreeMap<>();


    /**
     * A map that associates a command name with its category. Every command name in the
     * {@code commands} map is present in this map. If a command does not have a specific category,
     * it is assigned the default category {@code String the.defaultCategoryName = "zz_Other"},
     * ensuring that it appears at the end of the list when category names are sorted.
     *
     * @see #addCommand(String, Options)
     * @see #addCommand(String, Options, Consumer)
     * @see #addCommand(String, Options, String)
     * @see #addCommand(String, Options, String, Consumer)
     */
    private TreeMap<String, String> commandCategoryMap = new TreeMap<>();

    private String defaultCategoryName = "zz_Other";
    
    /** 
     * A map that stores category names and their descriptions.
     * This map only includes categories that have a description.
     * Categories without descriptions are not included.
     */
    private TreeMap<String, String> categoryDescriptionMap = new TreeMap<>();


    /** Options not related to a specific command. */
    Options universalOptions = null;

    /** Command name which is specified in the given command line. */
    String givenCommand = null;

    /** Default constructor. */
    public CliCommands() {

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


    /**
     * Adds a command and its associated options. The command is assigned the default category.
     *
     * @param command The name of the command.
     * @param options The options associated with the command.
     */
    public void addCommand(String command, Options options) {
        commands.put(command, options);
        commandCategoryMap.put(command, defaultCategoryName);
    }

    /**
     * Adds a command with its associated options and execution action. The command is assigned the
     * default category.
     *
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param action The action to be executed when the command is invoked.
     */
    public void addCommand(String command, Options options, Consumer<CommandLine> action) {
        commands.put(command, options);
        commandActionMap.put(command, action);
        commandCategoryMap.put(command, defaultCategoryName);
    }

    /**
     * Adds a command with its associated options and description. The command is assigned the
     * default category.
     *
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param description The description of the command.
     */
    public void addCommand(String command, Options options, String description) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandCategoryMap.put(command, defaultCategoryName);
    }

    /**
     * Adds a command with its associated options, description, and execution action. The command is
     * assigned the default category.
     *
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param description The description of the command.
     * @param action The action to be executed when the command is invoked.
     */
    public void addCommand(String command, Options options, String description,
            Consumer<CommandLine> action) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandActionMap.put(command, action);
        commandCategoryMap.put(command, defaultCategoryName);
    }

    /**
     * Adds a command with its associated options under a specific category.
     *
     * @param category The category to which the command belongs.
     * @param command The name of the command.
     * @param options The options associated with the command.
     */
    public void addCommand(String category, String command, Options options) {
        commands.put(command, options);
        commandCategoryMap.put(command, category);
    }

    /**
     * Adds a command with its associated options and execution action under a specific category.
     *
     * @param category The category to which the command belongs.
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param action The action to be executed when the command is invoked.
     */
    public void addCommand(String category, String command, Options options,
            Consumer<CommandLine> action) {
        commands.put(command, options);
        commandActionMap.put(command, action);
        commandCategoryMap.put(command, category);
    }

    /**
     * Adds a command with its associated options and description under a specific category.
     *
     * @param category The category to which the command belongs.
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param description The description of the command.
     */
    public void addCommand(String category, String command, Options options, String description) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandCategoryMap.put(command, category);
    }

    /**
     * Adds a command with its associated options, description, and execution action under a
     * specific category.
     *
     * @param category The category to which the command belongs.
     * @param command The name of the command.
     * @param options The options associated with the command.
     * @param description The description of the command.
     * @param action The action to be executed when the command is invoked.
     */
    public void addCommand(String category, String command, Options options, String description,
            Consumer<CommandLine> action) {
        commands.put(command, options);
        commandDescMap.put(command, description);
        commandActionMap.put(command, action);
        commandCategoryMap.put(command, category);
    }


    
    /** Add options that can be used independently of the cli commands. */
    public void addUniversalOption(Option option) {
        this.universalOptions.addOption(option);
    }


    /**
     * Executes the action associated with the given command. If the command has a registered
     * action, it is executed with the provided {@code CommandLine} instance. If no action is
     * associated with the command, this method does nothing.
     *
     * @param givenCommand The command whose action should be executed.
     * @param cl The {@code CommandLine} instance passed to the action.
     */
    public void execute(String givenCommand, CommandLine cl) {
        Consumer<CommandLine> action = this.commandActionMap.get(givenCommand);
        if (action != null) {
            action.accept(cl);
        }
    }


    /**
     * Returns the command name provided via command-line arguments. This method requires that the
     * {@code parse} method has been executed beforehand; otherwise, it will return {@code null}.
     *
     * @return The command name extracted from the command-line arguments.
     */
    public String getGivenCommand() {
        return this.givenCommand;
    }


    /**
     * Checks whether the program recognizes the given command. This method requires that the
     * {@code parse} method has been executed beforehand; otherwise, the result may be unreliable.
     *
     * @param command The command name to check.
     * @return {@code true} if the program recognizes the given command, {@code false} otherwise.
     */
    public boolean hasCommand(String command) {
        return this.commands.containsKey(command);
    }

    

    /**
     * Parses the given command-line arguments.
     *
     * @param args The command-line arguments provided as a {@code String[]} array.
     */
    public CommandLine parse(String[] args) throws ParseException {

        CommandLine cl = null;

        if (args.length > 0) {

            String command = args[0];
            this.givenCommand = command;

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


    /**
     * Displays the description for the given command, including a list of its command-line options.
     *
     * @param command The name of the command whose description and options should be displayed.
     */
    public void printCommandHelp(String command) {

        Options options = this.commands.get(command);

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp(command, options, false);
        System.out.println();

        if (this.commandDescMap.containsKey(command)) {
            System.out.println("\n## Description\n");
            System.out.println(this.commandDescMap.get(command));
        }

    }


    
    
    private TreeMap<String, List<String>> calcCategoryToCommand() {
        TreeMap<String, List<String>> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : this.commandCategoryMap.entrySet()) {
            String command = entry.getKey();
            String category = entry.getValue();

            // categoryToCommand にカテゴリーがなければ新しいリストを作成
            result.computeIfAbsent(category, k -> new ArrayList<>()).add(command);
        }
        return result;
    }


    private String extractFirstLine(String str) {
        String multiLineString = "Hello, World!\nThis is a test.\nAnother line.";

        String firstLine = multiLineString.split("\n", 2)[0];

        return firstLine;
    }


    /**
     * Displays a brief description for each command in the given list.
     *
     * @param commands The list of command names whose brief descriptions should be displayed.
     */
    public void printCommandList(List<String> commands) {

        for (String command: commands) {
            String description = commandDescMap.get(command);
                if (command.length()<16) {
                    command = String.format("%-16s", command);
                }
                else {
                    int width = ((command.length() + 4)/4)*4;
                    String f = "%-" + String.valueOf(width) + "s";
                    command = String.format(f, command);
                }
                System.out.println(command + extractFirstLine(description));
            };
    }
    

    public void printCommandList(String synopsis) {

        System.out.println("\n## Usage\n");
        System.out.println(synopsis);
        System.out.println("");

        boolean hasCategory = false;
        TreeMap<String, List<String>> categoryToCommand = calcCategoryToCommand();

        for (Map.Entry<String, List<String>> entry : categoryToCommand.entrySet()) {
            String category = entry.getKey();
            List<String> commands = entry.getValue();
            Collections.sort(commands);

            if (category.equals(this.defaultCategoryName)) {

                if (hasCategory == true) {
                    System.out.println("\n## Other Commands");
                }
                else {
                    System.out.println("\n## Commands");
                }
                
            }
            else {
                hasCategory = true;
                System.out.println("\n## " + category);
            }

            System.out.println();
            // print description of the category.
            if (categoryDescriptionMap.containsKey(category)) {
                System.out.println(categoryDescriptionMap.get(category));
            }
            // print commands of the category.
            printCommandList(commands);
            System.out.println();
        }
        
    }


}


