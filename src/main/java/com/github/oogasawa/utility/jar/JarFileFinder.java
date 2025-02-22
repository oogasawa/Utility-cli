package com.github.oogasawa.utility.jar;

/**
 * A utility class to find and list all JAR files in a given directory recursively.
 */
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class JarFileFinder {


    
    /**
     * The main method to execute the JAR file search utility.
     *
     * @param args command-line arguments, expecting a single argument which is the directory path to search
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JarFileFinder <directory>");
            return;
        }
        
        Path startDir = Paths.get(args[0]);
        
        if (!Files.isDirectory(startDir)) {
            System.out.println("Error: The provided path is not a directory.");
            return;
        }
        
        try {
            List<Path> jarFiles = findJarFiles(startDir);
            System.out.println("Found JAR files:");
            for (Path jarFile : jarFiles) {
                System.out.println(jarFile.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error while searching for JAR files: " + e.getMessage());
        }
    }


    public static void listJarFiles(Path startDir) {

        if (!Files.isDirectory(startDir)) {
            System.out.println("Error: The provided path is not a directory.");
            return;
        }
        
        try {
            List<Path> jarFiles = findJarFiles(startDir);
            //System.out.println("Found JAR files:");
            for (Path jarFile : jarFiles) {
                System.out.println(jarFile.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error while searching for JAR files: " + e.getMessage());
        }
        
    }

    
    /**
     * Recursively searches for JAR files starting from the given directory.
     *
     * @param startDir the root directory to start the search
     * @return a list of paths to the JAR files found
     * @throws IOException if an I/O error occurs while accessing the file system
     */
    public static List<Path> findJarFiles(Path startDir) throws IOException {
        List<Path> jarFiles = new ArrayList<>();
        
        Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".jar")) {
                    jarFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        
        return jarFiles;
    }
}

