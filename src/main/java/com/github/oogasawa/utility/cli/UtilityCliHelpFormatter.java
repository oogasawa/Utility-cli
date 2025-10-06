package com.github.oogasawa.utility.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Custom {@link HelpFormatter} that provides a structured help layout with configurable
 * section headings, descriptions, and example blocks without modifying Commons CLI itself.
 */
public class UtilityCliHelpFormatter extends HelpFormatter {

    private String usageHeading = "Usage";
    private String descriptionHeading = "Description";
    private String optionsHeading = "Options";
    private String examplesHeading = "Examples";

    private String description;
    private List<String> examples = List.of();

    public UtilityCliHelpFormatter() {
        setWidth(100);
        setLeftPadding(4);
        setDescPadding(2);
    }

    public UtilityCliHelpFormatter usageHeading(String heading) {
        this.usageHeading = heading != null ? heading : "Usage";
        return this;
    }

    public UtilityCliHelpFormatter descriptionHeading(String heading) {
        this.descriptionHeading = heading != null ? heading : "Description";
        return this;
    }

    public UtilityCliHelpFormatter optionsHeading(String heading) {
        this.optionsHeading = heading != null ? heading : "Options";
        return this;
    }

    public UtilityCliHelpFormatter examplesHeading(String heading) {
        this.examplesHeading = heading != null ? heading : "Examples";
        return this;
    }

    public UtilityCliHelpFormatter description(String value) {
        this.description = value;
        return this;
    }

    public UtilityCliHelpFormatter examples(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            this.examples = List.of();
        } else {
            this.examples = List.copyOf(values);
        }
        return this;
    }

    public void printCommandHelp(PrintWriter out, String command, Options options) {
        Objects.requireNonNull(out, "out");

        Options safeOptions = options != null ? options : new Options();

        if (command != null && !command.isBlank()) {
            out.println(usageHeading + ":");
            StringWriter usageWriter = new StringWriter();
            PrintWriter usageOut = new PrintWriter(usageWriter);
            super.printUsage(usageOut, getWidth(), command, safeOptions);
            usageOut.flush();
            out.print(indentBlock(usageWriter.toString()));
            out.println();
        }

        if (description != null && !description.isBlank()) {
            out.println(descriptionHeading + ":");
            printWrapped(out, getWidth(), "  " + description.trim());
            out.println();
        }

        if (!safeOptions.getOptions().isEmpty()) {
            out.println(optionsHeading + ":");
            super.printOptions(out, getWidth(), safeOptions, getLeftPadding(), getDescPadding());
            out.println();
        }

        if (!examples.isEmpty()) {
            out.println(examplesHeading + ":");
            for (String example : examples) {
                out.println("  " + example);
            }
            out.println();
        }

        out.flush();
    }

    private String indentBlock(String text) {
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.isEmpty()) {
                sb.append("  ").append(line);
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
