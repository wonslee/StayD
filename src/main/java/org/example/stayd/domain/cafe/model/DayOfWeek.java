package org.example.stayd.domain.cafe.model;

public enum DayOfWeek {
    MON, TUE, WED, THU, FRI, SAT, SUN;

    public static DayOfWeek from(String stringValue) {
        return DayOfWeek.valueOf(stringValue);
    }

    public String toStringValue() {
        return name();
    }
}