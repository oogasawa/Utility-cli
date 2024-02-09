package com.github.oogasawa.utility.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetOperation {


    public TreeSet<String> read(String filePath) {
        TreeSet<String> result = new TreeSet<>();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            // Load each line of the file into a TreeSet
            result = stream.collect(Collectors.toCollection(TreeSet::new));

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        
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

    public void print(TreeSet<String> set) {
        for (String s : set) {
            System.out.println(s);
        }
    }
    
}
