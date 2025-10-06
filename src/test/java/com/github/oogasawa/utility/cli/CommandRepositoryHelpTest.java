package com.github.oogasawa.utility.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
        assertTrue(helpOutput.contains("usage: deploy"));
        assertTrue(helpOutput.contains("### Example"));
        assertTrue(helpOutput.contains("$ tool deploy --source ./docs"));
    }
}

