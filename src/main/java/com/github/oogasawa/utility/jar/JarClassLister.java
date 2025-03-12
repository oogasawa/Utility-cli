package com.github.oogasawa.utility.jar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code JarClassLister} class provides functionality to list all class files
 * within a given JAR file. It extracts fully qualified class names from JAR entries
 * and prints them to standard output.
 */
public class JarClassLister {

    /** Logger instance for logging errors and debugging information. */
    private static final Logger logger = LoggerFactory.getLogger(JarClassLister.class);
    
    /**
     * Lists all class files contained within a specified JAR file.
     * 
     * @param jarFilePath The path to the JAR file.
     */
    public static void listClasses(Path jarFilePath) {
        File jarFile = jarFilePath.toFile();

        if (!jarFile.exists() || !jarFile.isFile()) {
            logger.error("The specified JAR file does not exist: " + jarFilePath);
            return;
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.endsWith(".class")) {
                    // Convert path notation to fully qualified class name
                    String className = name.replace('/', '.').replace(".class", "");
                    System.out.println(className);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read the JAR file: " + e.getMessage());
        }
    }
}
