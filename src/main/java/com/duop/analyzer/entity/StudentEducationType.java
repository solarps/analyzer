package com.duop.analyzer.entity;

import java.util.Random;

public enum StudentEducationType {
    CONTRACT("к"), BUDGET("б");
    public final String type;

    StudentEducationType(String type) {
        this.type = type;
    }

    public static StudentEducationType generateType() {
        int random = new Random().nextInt(100);
        if (random < 75) {
            return BUDGET;
        } else return CONTRACT;
    }

    public String getType() {
        return type;
    }
}
