package com.github.oogasawa.utility.jar;

import java.io.IOException;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarClassFinder {

    private static final Logger logger = LoggerFactory.getLogger(JarClassFinder.class);

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
     * @return A string containing the matching JAR file path, module type, and module name, or null if not found.
     */
    public static void findJarContainingClass(String className, Path rootDir) {
        String classFilePath = className.replace('.', '/') + ".class";

        logger.debug("Searching for class file: " + classFilePath);
        
        try (Stream<Path> paths = Files.walk(rootDir)) {
            paths.filter(path -> path.toString().endsWith(".jar"))
                .map(Path::toFile)
                .map(file -> checkJarForClass(file, classFilePath))
                .filter(result -> result != null)
                .forEach(result -> System.out.println(result));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if a JAR file contains the specified class and retrieves its module information.
     *
     * @param jarFile The JAR file to check.
     * @param classFilePath The class file path (e.g., "javafx/application/Platform.class").
     * @return A string containing the JAR file path, module type, and module name if the class is found; otherwise null.
     */
    public static String checkJarForClass(java.io.File jarFile, String classFilePath) {
        
        try (JarFile jar = new JarFile(jarFile)) {
            if (jar.stream().map(JarEntry::getName).anyMatch(name -> name.equals(classFilePath))) {
                String moduleType = JarModuleScanner.getModuleType(jar);
                String moduleName = JarModuleScanner.getModuleName(jar);
                return String.format("JAR: %s\n  Type: %s\n  Module Name: %s\n Found: %s\n",
                        jarFile.getAbsolutePath(), moduleType, moduleName, classFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error reading JAR file: " + jarFile);
            e.printStackTrace();
        }
        return null;
    }

    
}
