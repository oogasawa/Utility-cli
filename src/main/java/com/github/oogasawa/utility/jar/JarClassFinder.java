package com.github.oogasawa.utility.jar;

import java.io.IOException;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * The {@code JarClassFinder} class provides functionality to search for a specific class
 * within JAR files located under a given root directory. It helps in identifying which
 * JAR file contains a specific fully qualified class name.
 */
public class JarClassFinder {

    /** Logger instance for logging debug and error messages. */
    private static final Logger logger = Logger.getLogger(JarClassFinder.class.getName());

    /**
     * Main method to execute the search operation.
     * 
     * @param args Command-line arguments. Expected arguments:
     *             <ul>
     *             <li>args[0]: Fully qualified class name to search for (e.g., "javafx.application.Platform").</li>
     *             <li>args[1]: Root directory where JAR files should be searched.</li>
     *             </ul>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java JarClassFinder <class-name> <root-dir>");
            System.exit(1);
        }

        String className = args[0];
        Path rootDir = Paths.get(args[1]);

        findJarContainingClass(className, rootDir);
    }

    /**
     * Searches for a JAR file containing the specified class under the given root directory.
     * 
     * @param className The fully qualified class name to search for (e.g., "javafx.application.Platform").
     * @param rootDir   The root directory where JAR files should be searched.
     */
    public static void findJarContainingClass(String className, Path rootDir) {
        String classFilePath = className.replace('.', '/') + ".class";

        logger.fine("Searching for class file: " + classFilePath);
        
        try (Stream<Path> paths = Files.walk(rootDir)) {
            paths.filter(path -> path.toString().endsWith(".jar"))
                .map(Path::toFile)
                .map(file -> checkJarForClass(file, classFilePath))
                .filter(result -> result != null)
                .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error while searching for JAR files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if a JAR file contains the specified class and retrieves its module information.
     * 
     * @param jarFile The JAR file to check.
     * @param classFilePath The class file path (e.g., "javafx/application/Platform.class").
     * @return A formatted string containing the JAR file path, module type, module name, and found class file path;
     *         returns {@code null} if the class is not found in the JAR file.
     */
    public static String checkJarForClass(java.io.File jarFile, String classFilePath) {
        try (JarFile jar = new JarFile(jarFile)) {
            if (jar.stream().map(JarEntry::getName).anyMatch(name -> name.equals(classFilePath))) {
                String moduleType = JarModuleScanner.getModuleType(jar);
                String moduleName = JarModuleScanner.getModuleName(jar);
                return String.format("JAR: %s\n  Type: %s\n  Module Name: %s\n  Found: %s\n",
                        jarFile.getAbsolutePath(), moduleType, moduleName, classFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading JAR file: " + jarFile.getAbsolutePath());
            e.printStackTrace();
        }
        return null;
    }
}

