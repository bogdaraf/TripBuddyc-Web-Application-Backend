package com.tripbuddyc.config.db;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringListConverterTest {

    @Test
    void convertToDatabaseColumn_filledList() {
        StringListConverter converter = new StringListConverter();

        List<String> stringList = new ArrayList<>();
        stringList.add("elem1");
        stringList.add("elem2");
        String result = converter.convertToDatabaseColumn(stringList);

        assertEquals("elem1;elem2", result);
    }

    @Test
    void convertToDatabaseColumn_emptyList() {
        StringListConverter converter = new StringListConverter();

        List<String> stringList = new ArrayList<>();
        String result = converter.convertToDatabaseColumn(stringList);

        assertEquals(null, result);
    }

    @Test
    void convertToDatabaseColumn_nullList() {
        StringListConverter converter = new StringListConverter();

        List<String> stringList = null;
        String result = converter.convertToDatabaseColumn(stringList);

        assertEquals(null, result);
    }

    @Test
    void convertToEntityAttribute_filledString() {
        StringListConverter converter = new StringListConverter();

        List<String> result = converter.convertToEntityAttribute("elem1;elem2");

        List<String> stringList= new ArrayList<>();
        stringList.add("elem1");
        stringList.add("elem2");
        assertEquals(stringList, result);
    }

    @Test
    void convertToEntityAttribute_emptyString() {
        StringListConverter converter = new StringListConverter();

        List<String> result = converter.convertToEntityAttribute("");

        List<String> stringList= new ArrayList<>();
        assertEquals(stringList, result);
    }

    @Test
    void convertToEntityAttribute_nullString() {
        StringListConverter converter = new StringListConverter();

        List<String> result = converter.convertToEntityAttribute(null);

        List<String> stringList= new ArrayList<>();
        assertEquals(stringList, result);
    }
}