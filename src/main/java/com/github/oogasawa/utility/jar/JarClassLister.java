package com.github.oogasawa.utility.jar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarClassLister {

    private static final Logger logger = LoggerFactory.getLogger(JarClassLister.class);
    
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
                    // クラス名を . 区切りに変換して表示
                    String className = name.replace('/', '.').replace(".class", "");
                    System.out.println(className);
                }
            }

        } catch (IOException e) {
            logger.error("Failed to read the JAR file: " + e.getMessage());
        }
    }
}
