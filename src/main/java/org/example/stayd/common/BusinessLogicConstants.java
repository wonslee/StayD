package org.example.stayd.common;

public class BusinessLogicConstants {
    /**
     * 공통
     */

    /**
     * 유저 인증
     */
    public static final int MAX_LOGIN_ID_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PASSWORD_LENGTH = 255;

    /**
     * 스터디카페
     */
    public static final int MIN_OPERATION_HOUR = 0;
    public static final int MAX_OPERATION_HOUR = 23;
    public static final int MAX_CAFE_PHONE_NUMBER_LENGTH = 255;


    // TODO: 스케줄러 시간대 & 주기

    /**
     * 예약 & 좌석
     */
    public static final int DEFAULT_SEAT_COLS = 4;
    public static final int DEFAULT_SEAT_ROWS = 5;

    public static final int MAX_BAD_RESERVATION_CANCEL_COUNT = 3;
    // 허위 예약 취소 기간 설정 (N 개월 내)
    public static final int BAD_RESERVATION_CANCEL_PERIOD_MONTH = 1;

    /**
     * 리뷰 평점은 최대 5
     */
    public static final int MIN_REVIEW_RATE = 1;
    public static final int MAX_REVIEW_RATE = 5;
}
