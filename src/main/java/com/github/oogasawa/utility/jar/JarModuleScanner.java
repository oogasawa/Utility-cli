package com.github.oogasawa.utility.jar;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;


/**
 * A utility class to scan JAR files in a directory and determine their JPMS module type
 * (explicit or automatic) along with their module name.
 */
public class JarModuleScanner {

    /**
     * The entry point of the program.
     * 
     * @param args Command-line arguments. The first argument should be the directory path
     *             containing JAR files to be scanned.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java JarModuleScanner <directory_path>");
            System.exit(1);
        }

        scan(Paths.get(args[0]));
    }


    /**
     * Scans all JAR files under the specified root directory recursively
     * and determines their JPMS module type (explicit or automatic) along with their module name.
     *
     * @param rootDir The root directory to scan for JAR files.
     */
    public static void scan(Path rootDir) {

        try (Stream<Path> paths = Files.walk(rootDir)) {
            paths.filter(path -> path.toString().endsWith(".jar"))
                 .forEach(JarModuleScanner::processJarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Processes a single JAR file to determine its module type and module name.
     *
     * @param jarPath The path to the JAR file.
     */
    public static void processJarFile(Path jarPath) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            String moduleType = getModuleType(jarFile);
            String moduleName = getModuleName(jarFile);

            System.out.printf("JAR: %s%n  Type: %s%n  Module Name: %s%n%n",
                              jarPath, moduleType, moduleName);
        } catch (IOException e) {
            System.err.println("Error reading JAR file: " + jarPath);
            e.printStackTrace();
        }
    }

    /**
     * Determines whether a JAR file is an explicit or automatic module.
     *
     * @param jarFile The JAR file to check.
     * @return "explicit" if the JAR contains a module-info.class, otherwise "automatic".
     */
    public static String getModuleType(JarFile jarFile) {
        return containsModuleInfo(jarFile) ? "explicit" : "automatic";
    }

    /**
     * Retrieves the module name of the given JAR file.
     * 
     * - If it's an explicit module, it attempts to get the module name from the manifest.
     * - If it's an automatic module, it checks for "Automatic-Module-Name" in the manifest.
     * - If no module name is found, it derives the name from the JAR filename.
     *
     * @param jarFile The JAR file to extract the module name from.
     * @return The module name of the JAR.
     * @throws IOException If an error occurs while reading the JAR file.
     */
    public static String getModuleName(JarFile jarFile) throws IOException {

        Path jarPath = Path.of(jarFile.getName());
        
        if (containsModuleInfo(jarFile)) {
            String explicitName = getExplicitModuleName(jarFile);
            return (explicitName != null) ? explicitName : deriveModuleNameFromFilename(jarPath.getFileName().toString());
        } else {
            String automaticName = getAutomaticModuleName(jarFile);
            return (automaticName != null) ? automaticName : deriveModuleNameFromFilename(jarPath.getFileName().toString());
        }
    }

    /**
     * Checks if a JAR file contains a module-info.class, indicating it is an explicit module.
     *
     * @param jarFile The JAR file to check.
     * @return true if the JAR contains module-info.class, false otherwise.
     */
    private static boolean containsModuleInfo(JarFile jarFile) {
        Enumeration<? extends ZipEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            if (entries.nextElement().getName().equals("module-info.class")) {
                return true;
            }
        }
        return false;
    }



    /**
     * Retrieves the module name for an explicit module using `jar --describe-module`.
     * It looks for the line that contains "jar:file", then extracts the first word before
     * the first space as the module name.
     *
     * @param jarFile The JAR file object.
     * @return The module name if found, or null if not available.
     */
    public static String getExplicitModuleName(JarFile jarFile) {
        Path jarPath = jarFile.getName() != null ? Path.of(jarFile.getName()) : null;
        if (jarPath == null) {
            return null;
        }

        try {
            Process process = new ProcessBuilder("jar", "--describe-module", "--file=" + jarPath.toString())
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("jar:file")) { // "jar:file" を含む行を探す
                        return line.split("\\s+", 2)[0].trim(); // 最初の単語（スペースが現れるまで）を取得
                    }
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running jar --describe-module for " + jarPath);
            e.printStackTrace();
        }
        return null;
    }


    

    /**
     * Retrieves the module name for an automatic module from the manifest.
     * This looks for the "Automatic-Module-Name" entry in META-INF/MANIFEST.MF.
     *
     * @param jarFile The JAR file to extract the automatic module name from.
     * @return The automatic module name if found, or null if not present.
     * @throws IOException If an error occurs while reading the manifest.
     */
    private static String getAutomaticModuleName(JarFile jarFile) throws IOException {
        Manifest manifest = jarFile.getManifest();
        if (manifest != null) {
            return manifest.getMainAttributes().getValue("Automatic-Module-Name");
        }
        return null;
    }


    /**
     * Derives a module name from the JAR filename if no explicit or automatic module name is found.
     * 
     * - Removes the ".jar" extension.
     * - Removes version numbers (e.g., "-1.0.0", "-2.3").
     * - Replaces invalid characters with dots (following JPMS rules).
     *
     * @param filename The JAR filename.
     * @return The derived module name.
     */
    private static String deriveModuleNameFromFilename(String filename) {
        if (filename.endsWith(".jar")) {
            filename = filename.substring(0, filename.length() - 4);
        }

        // Remove version numbers (e.g., "-1.0.0", "-2.3", "-20240219")
        filename = filename.replaceAll("-[0-9]+(\\.[0-9]+)*$", "");

        // Replace invalid characters with dots
        filename = filename.replaceAll("[^a-zA-Z0-9]", ".");

        // Remove leading and trailing dots
        return filename.replaceAll("^\\.+|\\.+$", "");
    }

    
}

