package com.duop.analyzer.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class EducationTypeConverter implements AttributeConverter<StudentEducationType, String> {
    @Override
    public String convertToDatabaseColumn(StudentEducationType studentEducationType) {
        if (studentEducationType == null) {
            return null;
        }
        return studentEducationType.getType();
    }

    @Override
    public StudentEducationType convertToEntityAttribute(String type) {
        if (type == null) {
            return null;
        }

        return Stream.of(StudentEducationType.values())
                .filter(c -> c.getType().equals(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }
}
