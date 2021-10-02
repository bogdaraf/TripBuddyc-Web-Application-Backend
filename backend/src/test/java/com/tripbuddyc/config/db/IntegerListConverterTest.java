package com.tripbuddyc.config.db;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegerListConverterTest {

    @Test
    void convertToDatabaseColumn_filledList() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> integerList = new ArrayList<>();
        integerList.add(12);
        integerList.add(24);
        integerList.add(36);
        String result = converter.convertToDatabaseColumn(integerList);

        assertEquals("12;24;36", result);
    }

    @Test
    void convertToDatabaseColumn_emptyList() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> integerList = new ArrayList<>();
        String result = converter.convertToDatabaseColumn(integerList);

        assertEquals(null, result);
    }

    @Test
    void convertToDatabaseColumn_nullList() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> integerList = null;
        String result = converter.convertToDatabaseColumn(integerList);

        assertEquals(null, result);
    }

    @Test
    void convertToEntityAttribute_filledString() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> result = converter.convertToEntityAttribute("12;24;36");

        List<Integer> integerList= new ArrayList<>();
        integerList.add(12);
        integerList.add(24);
        integerList.add(36);
        assertEquals(integerList, result);
    }

    @Test
    void convertToEntityAttribute_emptyString() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> result = converter.convertToEntityAttribute("");

        List<Integer> integerList= new ArrayList<>();
        assertEquals(integerList, result);
    }

    @Test
    void convertToEntityAttribute_nullString() {
        IntegerListConverter converter = new IntegerListConverter();

        List<Integer> result = converter.convertToEntityAttribute(null);

        List<Integer> integerList= new ArrayList<>();
        assertEquals(integerList, result);
    }
}