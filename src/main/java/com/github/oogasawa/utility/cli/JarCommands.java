package com.github.oogasawa.utility.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.github.oogasawa.utility.jar.JarClassFinder;
import com.github.oogasawa.utility.jar.JarClassLister;
import com.github.oogasawa.utility.jar.JarFileFinder;
import com.github.oogasawa.utility.jar.JarModuleScanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class JarCommands {

    CliCommands cmds = null;
    
    public void setupCommands(CliCommands cmds) {

        this.cmds = cmds;
        
        jarListClassesCommand();
        jarListJarsCommand();
        jarScanModulesCommand();
        jarSearchClassesCommand();

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



    public void jarListClassesCommand() {
        Options opts = new Options();

        opts.addOption(Option.builder("jar")
                       .option("j")
                       .longOpt("jar")
                       .hasArg(true)
                       .argName("jar")
                       .desc("The root directory where JAR files should be searched.")
                       .required(true)
                       .build());

        this.cmds.addCommand("jar commands",
                             "jar:listClasses", opts,
                "Lists all Classes in a given Jar file.",
                 (CommandLine cl) -> {
                    String jarFile = cl.getOptionValue("jar");

                    JarClassLister.listClasses(Path.of(jarFile));
                });
        
        }

    
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

        this.cmds.addCommand("jar commands",
                             "jar:listJars", opts,
                "Finds and lists all JAR files in a given directory recursively.",
                 (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");

                    JarFileFinder.listJarFiles(Path.of(baseDir));
                });
        
        }

    
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

        this.cmds.addCommand("jar commands", "jar:scanModules", opts,
                "Return information about the JPMS module (type, name, etc.) for JAR files under the base directory.",
                 (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");

                    JarModuleScanner.scan(Paths.get(baseDir));
                });
        
        }

    
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
                       .desc("The fully qualified class name to search for (e.g., \"javafx.application.Platform\")")
                       .required(true)
                       .build());

        
        this.cmds.addCommand("jar commands", "jar:searchClasses", opts,
                "Searches for a JAR file containing the specified class under the given root directory.",
                 (CommandLine cl) -> {
                    String baseDir = cl.getOptionValue("baseDir");
                    String className = cl.getOptionValue("className");

                    JarClassFinder.findJarContainingClass(className, Path.of(baseDir));
                });


        
        }



    
    
}
