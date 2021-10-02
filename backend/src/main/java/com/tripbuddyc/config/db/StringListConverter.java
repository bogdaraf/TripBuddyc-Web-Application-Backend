package com.tripbuddyc.config.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if(stringList == null) {
            return null;
        }
        if(stringList.size() == 0) {
            return null;
        }

        return String.join(SPLIT_CHAR, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        if(string == null) {
            return new ArrayList<>();
        }
        if(string == "") {
            return new ArrayList<>();
        }

        return Arrays.asList(string.split(SPLIT_CHAR));
    }
}
