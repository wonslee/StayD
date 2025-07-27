// 작성자 : 방대혁
package org.example.stayd.domain.reservation.model;

public enum DayOfWeek {
    MON, TUE, WED, THU, FRI, SAT, SUN;

    public static DayOfWeek from(String stringValue) {
        return DayOfWeek.valueOf(stringValue);
    }

    public String toValue() {
        return name();
    }
}