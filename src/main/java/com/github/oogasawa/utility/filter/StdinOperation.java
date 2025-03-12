package com.github.oogasawa.utility.filter;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;



public class StdinOperation {


    public static void filterLines(String pattern) {

        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            if (line.contains(pattern)) {
                System.out.println(line);
            }
        }

        stdin.close();
    }


    public static void filterLines(String pattern, int column) {

        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            String[] columns = line.split("\t");
            if (columns.length > column && columns[column].contains(pattern)) {
                System.out.println(line);
            }
        }

        stdin.close();
    }
    
    
    public static void regexFilterLines(String pattern) {

        Pattern pFilter = Pattern.compile(pattern);
        Scanner stdin = new Scanner(System.in);
        
        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            if (pFilter.matcher(line).find()) {
                System.out.println(line);
            }
        }

        stdin.close();
    }


    public static void regexFilterLines(String pattern, int column) {

        Pattern pFilter = Pattern.compile(pattern);
        Scanner stdin = new Scanner(System.in);
        
        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();
            String[] columns = line.split("\t");
            if (columns.length > column && pFilter.matcher(columns[column]).find()) {
                System.out.println(line);
            }
        }

        stdin.close();
    }

    
    
    public static void getColumns(String columnsStr) {

        ArrayList<Integer> chosenColumns = parseColumns(columnsStr);
        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();

            ArrayList<String> origColumns = new ArrayList<>(Arrays.asList(line.split("\t")));
            ArrayList<String> resultColumns = new ArrayList<>();

            for (int i = 0; i < chosenColumns.size(); i++) {
                int index = chosenColumns.get(i);
                if (index < origColumns.size()) {
                    resultColumns.add(origColumns.get(index));
                }
            }

            System.out.println(joinColumns(resultColumns));
        }

        stdin.close();
    }

    
    
    private static String joinColumns(ArrayList<String> columns) {
        return columns.stream().collect(Collectors.joining("\t"));
    }
    

    private static ArrayList<Integer> parseColumns(String columnsStr) {
        ArrayList<Integer> result = new ArrayList<>();
        String[] columns = columnsStr.split(",");
        for (String column : columns) {
            if (column.contains("-")) {
                String[] range = column.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) {
                    result.add(i);
                }
            } else {
                result.add(Integer.parseInt(column));
            }
        }
        return result;
    }
    

    
    public static void splitLines(String delimiter) {

        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()) {
            String line = stdin.nextLine();

            // Split the line and join with tabs
            String tabSeparatedFields = Arrays.stream(line.split(delimiter))
                                              .collect(Collectors.joining("\t"));

            // Print the tab-separated fields
            System.out.println(tabSeparatedFields);
        }

        stdin.close();
    }
}

  
