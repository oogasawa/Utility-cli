package com.github.oogasawa.utility.cli;

import java.nio.file.Path;
import java.util.TreeSet;

import com.github.oogasawa.utility.jar.JarCommands;
import com.github.oogasawa.utility.stats.StatsCommands;
import com.github.oogasawa.utility.filter.SetOperation;
import com.github.oogasawa.utility.filter.StdinOperation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The {@code App} class demonstrates the usage of the Utility-cli command-line parser.
 * Utility-cli is a command-line parser designed for applications that contain multiple commands
 * within a single program, similar to Docker.
 * This class initializes the command repository, parses command-line arguments,
 * and executes the corresponding commands.
 */
public class App {

    /**
     * The command-line usage synopsis.
     */
    String synopsis = "java -jar Utility-cli-VERSION-fat.jar <command> <options>";
    
    /**
     * The repository that holds command definitions and executes them.
     */
    CommandRepository cmds = new CommandRepository();

    /**
     * The main method initializes the application and processes command-line input.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        App app = new App();
        // Load the command definitions.
        app.setupCommands();

        try {
            CommandLine cl = app.cmds.parse(args);
            String command = app.cmds.getGivenCommand();

            if (app.cmds.isHelpRequested()) {
                if (app.cmds.hasCommand(command)) {
                    app.cmds.printCommandHelp(command);
                } else {
                    System.err.println("Error: Unknown command: " + app.cmds.getGivenCommand());
                    app.cmds.printCommandList(app.synopsis);
                }
            } else if (command == null) {
                app.cmds.printCommandList(app.synopsis);
            } else if (app.cmds.hasCommand(command)) {
                app.cmds.execute(command, cl);
            } else {
                System.err.println("Error: Unknown command: " + app.cmds.getGivenCommand());
                System.err.println("Use one of the available commands listed below:");
                app.cmds.printCommandList(app.synopsis);
            }
        } catch (ParseException e) {
            System.err.println("Error: Failed to parse the command. Reason: " + e.getMessage());
            System.err.println("See the help below for correct usage:");
            app.cmds.printCommandHelp(app.cmds.getGivenCommand());
        }
    }

    /**
     * Registers all available commands by invoking their respective setup methods.
     */
    public void setupCommands() {
        differenceCommand();
        filterCommand();
        getColumnsCommand();
        splitCommand();

        // Register additional commands from another class.
        JarCommands jarCommands = new JarCommands();
        jarCommands.setupCommands(this.cmds);

        StatsCommands statsCommands = new StatsCommands();
        statsCommands.setupCommands(this.cmds);
        
    }

    /**
     * Defines the "difference" command, which calculates the difference between two sets.
     */
    public void differenceCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("file1")
                .option("f1")
                .longOpt("file1")
                .hasArg(true)
                .argName("file1")
                .desc("First file for set difference calculation (f1 - f2).")
                .required(true)
                .build());

        opts.addOption(Option.builder("file2")
                .option("f2")
                .longOpt("file2")
                .hasArg(true)
                .argName("file2")
                .desc("Second file for set difference calculation (f1 - f2).")
                .required(true)
                .build());

        this.cmds.addCommand("difference", opts,
                "Computes the difference between two sets of data.",
                (CommandLine cl) -> {
                    String file1 = cl.getOptionValue("file1");
                    String file2 = cl.getOptionValue("file2");
                    SetOperation s = new SetOperation();
                    TreeSet<String> set1 = s.read(Path.of(file1));
                    TreeSet<String> set2 = s.read(Path.of(file2));
                    TreeSet<String> result = s.difference(set1, set2);
                    s.print(result);
                });
    }


    /**
     * Defines the "filter" command, which filters lines based on given conditions.
     */
    public void filterCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("column")
                .option("c")
                .longOpt("column")
                .hasArg(true)
                .argName("column")
                .desc("Column to filter (optional).")
                .required(false)
                .build());

        opts.addOption(Option.builder("regex")
                .option("r")
                .longOpt("regex")
                .hasArg(true)
                .argName("regex")
                .desc("Regular expression pattern for filtering.")
                .required(true)
                .build());

        this.cmds.addCommand("filter", opts,
                "Filters lines based on specified conditions.",
                (CommandLine cl) -> {
                    String columnsStr = cl.getOptionValue("column");
                    StdinOperation.getColumns(columnsStr);
                });
    }

    /**
     * Defines the "getColumns" command, which extracts specific columns from tab-delimited input.
     */
    public void getColumnsCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("c")
                .longOpt("columns")
                .hasArg(true)
                .argName("columns")
                .desc("Comma-separated list of columns to retrieve (e.g., \"3,0,5-10\").")
                .required(true)
                .build());

        this.cmds.addCommand("getColumns", opts,
                "Extracts specific columns from tab-delimited lines.",
                (CommandLine cl) -> {
                    String columnsStr = cl.getOptionValue("columns");
                    StdinOperation.getColumns(columnsStr);
                });
    }

    
    /**
     * Defines the "split" command, which splits each line into fields based on a delimiter.
     */
    public void splitCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("delimiter")
                .option("d")
                .longOpt("delimiter")
                .hasArg(true)
                .argName("delimiter")
                .desc("Field delimiter (default: tab character).")
                .required(false)
                .build());

        String description = """
            Splits each line into separate fields.

            For example, given the following data file
            
            $ cat taxonomy.dump 
            1|all||synonym|
            1|root||scientific name|
            2|Bacteria|Bacteria <bacteria>|scientific name|
            2|bacteria||blast name|
            2|"Bacteria" Cavalier-Smith 1987||authority|
            2|Bacteria (ex Cavalier-Smith 1987)||synonym|
            2|Bacteria Woese et al. 2024||synonym|
            2|"Bacteriobiota" Luketa 2012||authority|
            2|Bacteriobiota||synonym|
            2|eubacteria||genbank common name|

            calling it as shown below will split it into tab-separated format at the "|" character.

            $ cat taxonomy.dump | java -jar target/Utility-cli-4.2.0-fat.jar split -d "\\|"
            1	all		synonym
            1	root		scientific name
            2	Bacteria	Bacteria <bacteria>	scientific name
            2	bacteria		blast name
            2	"Bacteria" Cavalier-Smith 1987		authority
            2	Bacteria (ex Cavalier-Smith 1987)		synonym
            2	Bacteria Woese et al. 2024		synonym
            2	"Bacteriobiota" Luketa 2012		authority
            2	Bacteriobiota		synonym
            2	eubacteria		genbank common name

            """;
        
        this.cmds.addCommand("split", opts,
                             description,
                (CommandLine cl) -> {
                    String delimiter = cl.getOptionValue("delimiter", "\\t");
                    StdinOperation.splitLines(delimiter);
                });
    }
}
