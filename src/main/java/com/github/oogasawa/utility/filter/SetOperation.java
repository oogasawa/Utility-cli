package com.github.oogasawa.utility.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The {@code SetOperation} class provides utility methods for reading sets of strings
 * from files or input streams and performing set operations such as difference.
 * 
 * The class is designed to handle operations on text-based datasets where each line
 * in the input represents an individual element in the set.
 */
public class SetOperation {

    /**
     * Reads a set of strings from a file.
     * 
     * @param filePath The path to the file containing the set of strings.
     * @return A {@code TreeSet<String>} containing the unique lines from the file.
     */
    public TreeSet<String> read(Path filePath) {
        TreeSet<String> result = null;
        try {
            InputStream in = Files.newInputStream(filePath);
            result = read(in);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return result;
    }

    /**
     * Reads a set of strings from an input stream.
     * 
     * @param in The input stream containing the set of strings.
     * @return A {@code TreeSet<String>} containing the unique lines from the input stream.
     */
    public TreeSet<String> read(InputStream in) {
        TreeSet<String> result = new TreeSet<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // Load each line from the input stream into a TreeSet to ensure uniqueness
        result = reader.lines().collect(Collectors.toCollection(TreeSet::new));

        return result;
    }

    /**
     * Computes the difference between two sets.
     * 
     * @param set1 The first set.
     * @param set2 The second set.
     * @return A new {@code TreeSet<String>} containing elements that exist in {@code set1} but not in {@code set2}.
     */
    public TreeSet<String> difference(TreeSet<String> set1, TreeSet<String> set2) {
        TreeSet<String> result = new TreeSet<>();
        for (String s : set1) {
            if (!set2.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Prints the elements of the given set in alphanumeric order.
     * 
     * @param set The set of strings to print.
     */
    public void print(TreeSet<String> set) {
        for (String s : set) {
            System.out.println(s);
        }
    }
}
