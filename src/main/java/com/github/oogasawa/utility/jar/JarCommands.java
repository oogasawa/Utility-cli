package com.github.oogasawa.utility.jar;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.github.oogasawa.utility.cli.CommandRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * The {@code JarCommands} class demonstrates how to organize related commands in Utility-cli.
 * Utility-cli is a command-line parser designed for applications that include multiple commands within a single program, similar to Docker.
 * 
 * As the number of commands increases, categorizing commands into logical groups helps both users and developers maintain organization.
 * This class serves as an example of grouping commands related to JAR file operations into a single class.
 */
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
        jarListJarsCommand();
        jarScanModulesCommand();
        jarSearchClassesCommand();
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

    /**
     * Registers the "jar:listJars" command, which finds and lists all JAR files in a given directory recursively.
     */
    public void jarListJarsCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("baseDir")
                .option("d")
                .longOpt("baseDir")
                .hasArg(true)
                .argName("baseDir")
                .desc("The root directory where JAR files should be searched.")
                .required(true)
                .build());

        this.cmdRepos.addCommand("jar commands", "jar:listJars", opts,
                "Recursively searches for and lists all JAR files in the given directory.",
                (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");
                    JarFileFinder.listJarFiles(Path.of(baseDir));
                });
    }

    /**
     * Registers the "jar:scanModules" command, which scans JAR files to retrieve Java Platform Module System (JPMS) module information.
     */
    public void jarScanModulesCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("baseDir")
                .option("d")
                .longOpt("baseDir")
                .hasArg(true)
                .argName("baseDir")
                .desc("The root directory where JAR files should be searched.")
                .required(true)
                .build());

        this.cmdRepos.addCommand("jar commands", "jar:scanModules", opts,
                "Scans JAR files under the specified base directory and retrieves information about their JPMS modules (type, name, etc.).",
                (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");
                    JarModuleScanner.scan(Paths.get(baseDir));
                });
    }

    /**
     * Registers the "jar:searchClasses" command, which searches for a JAR file containing the specified class within a given directory.
     */
    public void jarSearchClassesCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("baseDir")
                .option("d")
                .longOpt("baseDir")
                .hasArg(true)
                .argName("baseDir")
                .desc("The root directory where JAR files should be searched.")
                .required(true)
                .build());

        opts.addOption(Option.builder("className")
                .option("n")
                .longOpt("className")
                .hasArg(true)
                .argName("className")
                .desc("The fully qualified class name to search for (e.g., \"javafx.application.Platform\").")
                .required(true)
                .build());

        this.cmdRepos.addCommand("jar commands", "jar:searchClasses", opts,
                "Searches for a JAR file containing the specified class within the given root directory.",
                (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");
                    String className = cl.getOptionValue("className");
                    JarClassFinder.findJarContainingClass(className, Path.of(baseDir));
                });
    }
}
