package com.github.oogasawa.utility.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

class CommandRepositoryHelpTest {

    @Test
    void helpFlagSkipsExecutionAndPrintsExample() throws Exception {
        CommandRepository repository = new CommandRepository();

        Options options = new Options();
        options.addOption(Option.builder("s")
                .longOpt("source")
                .hasArg(true)
                .argName("source")
                .desc("Source path")
                .required(false)
                .build());

        String description = """
                Deploy a site.

                ### Example
                $ tool deploy --source ./docs
                """;

        repository.addCommand("deploy", options, description, cl -> {
            throw new IllegalStateException("Command should not execute when help is requested");
        });

        CommandLine commandLine = repository.parse(new String[] {"deploy", "-h"});

        assertNull(commandLine);
        assertEquals("deploy", repository.getGivenCommand());
        assertTrue(repository.isHelpRequested());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            repository.printCommandHelp("deploy");
        } finally {
            System.setOut(originalOut);
        }

        String helpOutput = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(helpOutput.contains("Usage:"));
        assertTrue(helpOutput.contains("usage: deploy"));
        assertTrue(helpOutput.contains("Description:"));
        assertTrue(helpOutput.contains("### Example"));
        assertTrue(helpOutput.contains("Options:"));
        assertTrue(helpOutput.contains("$ tool deploy --source ./docs"));
    }

    @Test
    void builderAllowsCustomSectionOrdering() {
        CommandRepository repository = new CommandRepository();

        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("profile")
                .hasArg(true)
                .desc("Deployment profile")
                .required(false)
                .build());

        repository.addCommand("deploy", options, "Deploy static content.", cl -> { });

        UtilityCliHelpFormatterBuilder builder = new UtilityCliHelpFormatterBuilder()
                .clearSections()
                .addCustomSection("Overview", List.of("Custom introduction"))
                .addUsageSection("How to Run")
                .addOptionsSection("Flags")
                .addCustomSection("Examples", List.of("deploy --profile production"));

        repository.configureCommandHelpFormatter("deploy", builder);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            repository.printCommandHelp("deploy");
        } finally {
            System.setOut(originalOut);
        }

        String helpOutput = buffer.toString(StandardCharsets.UTF_8);
        int overviewIndex = helpOutput.indexOf("Overview:");
        int usageIndex = helpOutput.indexOf("How to Run:");
        int flagsIndex = helpOutput.indexOf("Flags:");
        int examplesIndex = helpOutput.indexOf("Examples:");

        assertTrue(overviewIndex >= 0, "Overview section missing");
        assertTrue(usageIndex > overviewIndex, "Usage should appear after overview");
        assertTrue(flagsIndex > usageIndex, "Options should appear after usage");
        assertTrue(examplesIndex > flagsIndex, "Examples should appear last");
        assertTrue(helpOutput.contains("deploy --profile production"));
    }
}
