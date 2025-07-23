package org.example.stayd.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 요일 변환 유틸리티 클래스
 * 한글 요일 ↔ 영어 요일 변환
 */
public class DayOfWeekConverter {

    // 한글 → 영어 변환 맵
    private static final Map<String, String> KOREAN_TO_ENGLISH = new HashMap<>();

    // 영어 → 한글 변환 맵
    private static final Map<String, String> ENGLISH_TO_KOREAN = new HashMap<>();

    static {
        // 한글 → 영어 매핑
        KOREAN_TO_ENGLISH.put("월", "MON");
        KOREAN_TO_ENGLISH.put("화", "TUE");
        KOREAN_TO_ENGLISH.put("수", "WED");
        KOREAN_TO_ENGLISH.put("목", "THU");
        KOREAN_TO_ENGLISH.put("금", "FRI");
        KOREAN_TO_ENGLISH.put("토", "SAT");
        KOREAN_TO_ENGLISH.put("일", "SUN");

        // 영어 → 한글 매핑 (역방향)
        ENGLISH_TO_KOREAN.put("MON", "월");
        ENGLISH_TO_KOREAN.put("TUE", "화");
        ENGLISH_TO_KOREAN.put("WED", "수");
        ENGLISH_TO_KOREAN.put("THU", "목");
        ENGLISH_TO_KOREAN.put("FRI", "금");
        ENGLISH_TO_KOREAN.put("SAT", "토");
        ENGLISH_TO_KOREAN.put("SUN", "일");
    }

    /**
     * 한글 요일을 영어 요일로 변환
     * @param koreanDay 한글 요일 (월, 화, 수, 목, 금, 토, 일)
     * @return 영어 요일 (MON, TUE, WED, THU, FRI, SAT, SUN)
     */
    public static String toEnglish(String koreanDay) {
        if (koreanDay == null || koreanDay.trim().isEmpty()) {
            return null;
        }

        String result = KOREAN_TO_ENGLISH.get(koreanDay.trim());
        if (result == null) {
            System.out.println("Warning: Unknown Korean day: " + koreanDay);
            return koreanDay; // 변환 실패 시 원본 반환
        }

        return result;
    }

    /**
     * 영어 요일을 한글 요일로 변환
     * @param englishDay 영어 요일 (MON, TUE, WED, THU, FRI, SAT, SUN)
     * @return 한글 요일 (월, 화, 수, 목, 금, 토, 일)
     */
    public static String toKorean(String englishDay) {
        if (englishDay == null || englishDay.trim().isEmpty()) {
            return null;
        }

        String result = ENGLISH_TO_KOREAN.get(englishDay.trim().toUpperCase());
        if (result == null) {
            System.out.println("Warning: Unknown English day: " + englishDay);
            return englishDay; // 변환 실패 시 원본 반환
        }

        return result;
    }

    /**
     * 한글 요일 문자열을 영어 요일 문자열로 변환
     * @param koreanDaysString "월,화,수,목,금" 형태
     * @return "MON,TUE,WED,THU,FRI" 형태
     */
    public static String convertKoreanStringToEnglish(String koreanDaysString) {
        if (koreanDaysString == null || koreanDaysString.trim().isEmpty()) {
            return koreanDaysString;
        }

        String[] koreanDays = koreanDaysString.split(",");
        StringBuilder englishDays = new StringBuilder();

        for (int i = 0; i < koreanDays.length; i++) {
            if (i > 0) {
                englishDays.append(",");
            }
            englishDays.append(toEnglish(koreanDays[i].trim()));
        }

        return englishDays.toString();
    }

    /**
     * 영어 요일 문자열을 한글 요일 문자열로 변환
     * @param englishDaysString "MON,TUE,WED,THU,FRI" 형태
     * @return "월,화,수,목,금" 형태
     */
    public static String convertEnglishStringToKorean(String englishDaysString) {
        if (englishDaysString == null || englishDaysString.trim().isEmpty()) {
            return englishDaysString;
        }

        String[] englishDays = englishDaysString.split(",");
        StringBuilder koreanDays = new StringBuilder();

        for (int i = 0; i < englishDays.length; i++) {
            if (i > 0) {
                koreanDays.append(",");
            }
            koreanDays.append(toKorean(englishDays[i].trim()));
        }

        return koreanDays.toString();
    }

    /**
     * 디버깅용 메서드
     */
    public static void printConversion(String input, boolean toEnglish) {
        if (toEnglish) {
            System.out.println("Korean to English: " + input + " → " + convertKoreanStringToEnglish(input));
        } else {
            System.out.println("English to Korean: " + input + " → " + convertEnglishStringToKorean(input));
        }
    }
}