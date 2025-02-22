package com.github.oogasawa.utility.jar;

import java.io.IOException;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class JarClassFinder {


    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java JarClassFinder <class-name> <root-dir>");
            System.exit(1);
        }

        String className = args[0];
        Path rootDir = Paths.get(args[1]);

        String result = findJarContainingClass(className, rootDir);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Class not found in any JAR under " + rootDir);
        }
    }


    
    /**
     * Searches for a JAR file containing the specified class under the given root directory.
     *
     * @param className The fully qualified class name to search for (e.g., "javafx.application.Platform").
     * @param rootDir   The root directory where JAR files should be searched.
     * @return A string containing the matching JAR file path, module type, and module name, or null if not found.
     */
    public static String findJarContainingClass(String className, Path rootDir) {
        String classFilePath = className.replace('.', '/') + ".class";

        try (Stream<Path> paths = Files.walk(rootDir)) {
            return paths.filter(path -> path.toString().endsWith(".jar"))
                        .map(Path::toFile)
                        .map(file -> checkJarForClass(file, classFilePath))
                        .filter(result -> result != null)
                        .findFirst()
                        .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a JAR file contains the specified class and retrieves its module information.
     *
     * @param jarFile The JAR file to check.
     * @param classFilePath The class file path (e.g., "javafx/application/Platform.class").
     * @return A string containing the JAR file path, module type, and module name if the class is found; otherwise null.
     */
    private static String checkJarForClass(java.io.File jarFile, String classFilePath) {
        try (JarFile jar = new JarFile(jarFile)) {
            if (jar.stream().map(JarEntry::getName).anyMatch(name -> name.equals(classFilePath))) {
                String moduleType = JarModuleScanner.getModuleType(jar);
                String moduleName = JarModuleScanner.getModuleName(jar);
                return String.format("JAR: %s\n  Type: %s\n  Module Name: %s\n",
                        jarFile.getAbsolutePath(), moduleType, moduleName);
            }
        } catch (IOException e) {
            System.err.println("Error reading JAR file: " + jarFile);
            e.printStackTrace();
        }
        return null;
    }

    
}
