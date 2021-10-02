package com.tripbuddyc.config.db;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Integer> integerList) {
        if(integerList == null) {
            return null;
        }
        if(integerList.size() == 0) {
            return null;
        }

        List<String> stringList = integerList.stream().map(Object::toString).collect(Collectors.toList());

        return String.join(SPLIT_CHAR, stringList);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String string) {
        if(string == null) {
            return new ArrayList<>();
        }
        if(string == "") {
            return new ArrayList<>();
        }

        List<String> stringList = Arrays.asList(string.split(SPLIT_CHAR));

        List<Integer> integerList = stringList.stream().map(Integer::parseInt).collect(Collectors.toList());

        return integerList;
    }
}
