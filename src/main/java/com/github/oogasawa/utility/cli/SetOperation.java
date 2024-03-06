package com.github.oogasawa.utility.cli;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;
import java.util.stream.Collectors;



public class SetOperation {

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

    
    public TreeSet<String> read(InputStream in) {
        TreeSet<String> result = new TreeSet<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // Load each line of the file into a TreeSet
        result = reader.lines()
                .collect(Collectors.toCollection(TreeSet::new));

        return result;
    }

    
    
    public TreeSet<String> difference(TreeSet<String> set1, TreeSet<String> set2) {
        TreeSet<String> result = new TreeSet<>();
        for (String s : set1) {
            if (!set2.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    /** Prints elements of the given set in alphanumerical order of strings. */
    public void print(TreeSet<String> set) {
        for (String s : set) {
            System.out.println(s);
        }
    }
    
}
