
# Utility-cli: A Command Line Parser for Subcommand-based Interfaces

## About

Utility-cli is a command-line parser for subcommand-based interfaces, built on top of Apache Commons CLI. It is designed to keep the parsing logic structured and manageable, preventing the parsing methods from becoming excessively long even as the number of subcommands increases.


## Example Usage

When the program is launched without specifying a command name, it displays a list of available commands.

(Utility-cli itself serves as an example of how this tool can be used.)


```
$ java -jar target/Utility-cli-4.2.0-fat.jar 

## Usage

java -jar Utility-cli-VERSION-fat.jar <command> <options>


## jar commands

jar:listClasses Lists all classes contained in the specified JAR file.
jar:listJars    Recursively searches for and lists all JAR files in the given directory.
jar:scanModules Scans JAR files under the specified base directory and retrieves information about their JPMS modules (type, name, etc.).
jar:searchClasses   Searches for a JAR file containing the specified class within the given root directory.


## Other Commands

difference      Computes the difference between two sets of data.
filter          Filters lines based on specified conditions.
getColumns      Extracts specific columns from tab-delimited lines.
split           Splits each line into separate fields.

```

When launched with a command name, the tool displays usage information for that command.

```
$ java -jar target/Utility-cli-4.2.0-fat.jar split -h

usage: split
 -d,--delimiter <delimiter>   Field delimiter (default: tab character).


## Description

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

$ cat taxonomy.dump | java -jar target/Utility-cli-4.2.0-fat.jar split -d "\|"
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

```


## Documenting Command Help

- Each subcommand automatically supports `-h`/`--help`, so contributors can preview usage without triggering parsing errors.
- Feed multi-line descriptions to `addCommand` to populate the help footer. Embed examples directly in that description.
- Java text blocks keep the description readable in source code while rendering cleanly in the console:

```java
String description = """
        Run a batch job across all workspaces.

        ### Example
        $ yourapp batch --conf projects.conf --dryRun
        Shows how to preview a batch job without applying changes.
        """;

cmdRepos.addCommand("batch", options, description, cl -> runBatch(cl));
```


## Installation Instructions  

This program has been tested in the following environments:  
- OpenJDK 21.0.2  
- Apache Maven 3.9.9  

The installation process follows the standard Apache Maven procedure.  
Currently, it is not available in the Maven Repository, so please obtain it from GitHub.

```
git clone https://github.com/oogasawa/Utility-cli
cd Utility-cli
./mvnw clean install
```


## How to Use the Command-Line Parser  

- The `main` method follows a fixed structure and remains the same in every implementation.  
- The `setupCommands()` method defines all subcommands.  
  It is also possible to define a group of subcommands in a separate class.

```java

import com.github.oogasawa.utility.cli.CommandRepository;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class App {

    /**
     * The command-line usage synopsis.
     */
    String synopsis = "java -jar your_program-<VERSION>-fat.jar <command> <options>";
    
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
        getColumnsCommand();
        splitCommand();
        // ... 

        // Register additional commands from another class.
        JarCommands jarCommands = new JarCommands();
        jarCommands.setupCommands(this.cmds);
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
```


Subcommands and their command-line options are defined using methods like the example below:

- The `Options` class directly utilizes Apache Commons CLI, so please refer to its documentation for details.  
- The `description` field can span multiple lines, as shown in the example. In such cases, the first line is used as the short description of the subcommand.  
- When a subcommand is executed, the lambda function passed as an argument to the `this.cmds.addCommand()` method is invoked.


```java
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

```


To split a group of subcommands into a separate class, follow the approach below.


```
import com.github.oogasawa.utility.cli.CommandRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class JarCommands {

    /**
     * The command repository used to register commands.
     */
    CommandRepository cmdRepos = null;
    
    /**
     * Registers all JAR-related commands in the given command repository.
     * 
     * @param cmds The command repository to register commands with.
     */
    public void setupCommands(CommandRepository cmds) {
        this.cmdRepos = cmds;
        
        jarListClassesCommand();
    }

    /**
     * Registers the "jar:listClasses" command, which lists all classes in a given JAR file.
     */
    public void jarListClassesCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("jar")
                .option("j")
                .longOpt("jar")
                .hasArg(true)
                .argName("jar")
                .desc("The JAR file to list classes from.")
                .required(true)
                .build());

        this.cmdRepos.addCommand("jar commands", "jar:listClasses", opts,
                "Lists all classes contained in the specified JAR file.",
                (CommandLine cl) -> {
                    String jarFile = cl.getOptionValue("jar");
                    JarClassLister.listClasses(Path.of(jarFile));
                });
    }

  // ...
}
```
