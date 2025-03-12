package com.github.oogasawa.utility.cli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Logger;
import com.github.oogasawa.utility.filter.SetOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("difference command test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifferenceTest {

    private static final Logger logger = Logger.getLogger(DifferenceTest.class.getName());

    @DisplayName("Should calculate difference of two sets: {1,2,3}-{2,4}={1,3}")
    @Test
    @Order(1)
    public void testDifference() {

        String[] array1 = { "2", "3", "1" }; // Since it is a set, order is ignored.
        String[] array2 = { "2", "4" };

        TreeSet<String> set1 = new TreeSet<>(Arrays.asList(array1));
        TreeSet<String> set2 = new TreeSet<>(Arrays.asList(array2));

        SetOperation setOperation = new SetOperation();
        TreeSet<String> result = setOperation.difference(set1, set2);

        String[] expectedArray = { "1", "3" };
        assertArrayEquals(expectedArray, result.toArray());

    }

    
    @DisplayName("Should calculate difference from an empty set: {}-{2,4}={}")
    @Test
    @Order(2)
    public void testEmptySetDifference() {

        String[] array1 = {}; // Since it is a set, order is ignored.
        String[] array2 = { "2", "4" };

        TreeSet<String> set1 = new TreeSet<>(Arrays.asList(array1));
        TreeSet<String> set2 = new TreeSet<>(Arrays.asList(array2));

        SetOperation setOperation = new SetOperation();
        TreeSet<String> result = setOperation.difference(set1, set2);

        String[] expectedArray = {};
        assertArrayEquals(expectedArray, result.toArray());

    }
    

        
    @DisplayName("Should calculate difference of an empty set: {1,2,3}-{}={1,2,3}")
    @Test
    @Order(3)
    public void testEmptySetDifference2() {

        String[] array1 = {"1", "2", "3"};
        String[] array2 = {};

        TreeSet<String> set1 = new TreeSet<>(Arrays.asList(array1));
        TreeSet<String> set2 = new TreeSet<>(Arrays.asList(array2));

        SetOperation setOperation = new SetOperation();
        TreeSet<String> result = setOperation.difference(set1, set2);

        String[] expectedArray = {"1", "2", "3"};
        assertArrayEquals(expectedArray, result.toArray());

    }

        
    @DisplayName("Should calculate difference of two empty sets: {}-{}={}")
    @Test
    @Order(4)
    public void testEmptySetDifference3() {

        String[] array1 = {};
        String[] array2 = {};

        TreeSet<String> set1 = new TreeSet<>(Arrays.asList(array1));
        TreeSet<String> set2 = new TreeSet<>(Arrays.asList(array2));

        SetOperation setOperation = new SetOperation();
        TreeSet<String> result = setOperation.difference(set1, set2);

        String[] expectedArray = {};
        assertArrayEquals(expectedArray, result.toArray());

    }


    
}
