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

}