package com.github.oogasawa.utility.cli.line;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;



public class ColumnSelector {

    public static List<String> select(List<String> columns, String[] columnNumbers) {

        List<Integer> columnNumberList = toIntegerList(columnNumbers);
        List<String> selected = new ArrayList<>();
        for (Integer columnNumber : columnNumberList) {
            selected.add(columns.get(columnNumber));
        }

        return selected;
    }

    public static List<String> select(String[] columns, String[] columnNumbers) {

        List<Integer> columnNumberList = toIntegerList(columnNumbers);
        List<String> selected = new ArrayList<>();
        for (Integer columnNumber : columnNumberList) {
            selected.add(columns[columnNumber]);
        }

        return selected;
    }

    

    public static void println(List<String> selected) {
        StringJoiner joiner = new StringJoiner("\t");
        for (String item : selected) {
            joiner.add(item);
        }
        System.out.println(joiner.toString());
    }
    

    public static List<Integer> toIntegerList(String[] list) {
        List<Integer> result = new ArrayList<Integer>();
        for (String item : list) {
            result.add(Integer.parseInt(item));
        }
        return result;
    }

    
}
