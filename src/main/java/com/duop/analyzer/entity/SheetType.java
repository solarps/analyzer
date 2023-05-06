package com.duop.analyzer.entity;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum SheetType {
    ІСП(1),
    ЗАЛ(2),
    КР(3),
    КП(4);

    public final int code;

    SheetType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name().toUpperCase();
    }

    public static SheetType of(int code) {
        return Stream.of(SheetType.values())
                .filter(p -> p.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
