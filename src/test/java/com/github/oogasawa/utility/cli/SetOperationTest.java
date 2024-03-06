package com.github.oogasawa.utility.cli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;


@DisplayName("SetOperation utility methods test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SetOperationTest {

    private static final Logger logger = Logger.getLogger(SetOperationTest.class.getName());

    @DisplayName("Should be able to read the file and load each line of the file into a TreeSet")
    @Test
    @Order(1)
    public void testReadMethod() {

        SetOperation setOperation = new SetOperation();

        TreeSet<String> result = null;
        try (InputStream in = getClass().getResourceAsStream("/SetOperationTest/input_data.txt")) {
             result = setOperation.read(in);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while reading the file: " + e.getMessage());
        }
        

        String[] expectedArray = { "", "1", "2", "3"}; // Empty lines are also read.
        assertArrayEquals(expectedArray, result.toArray());
    }

}
