// 작성자 : 방대혁
package org.example.stayd.domain.discount.dto;

import lombok.Data;

/**
 * 할인 설정 정보를 담는 DTO 클래스
 */
@Data
public class DiscountDTO {

    /**
     * 할인 설정을 위한 생성자
     *
     * @param dayOfWeek     할인 적용 요일 (MON, TUE 등)
     * @param discountStart 할인 시작 시간 (0~23)
     * @param discountEnd   할인 종료 시간 (0~23)
     * @param discountRate  할인율 (0~100)
     */
    public DiscountDTO(String dayOfWeek, int discountStart, int discountEnd, double discountRate) {
        this.dayOfWeek = dayOfWeek;
        this.discountStart = discountStart;
        this.discountEnd = discountEnd;
        this.discountRate = discountRate;
    }

    private String dayOfWeek;   // 할인 적용 요일 (MON, TUE 등)
    private int discountStart;  // 할인 시작 시간 (0~23)
    private int discountEnd;    // 할인 종료 시간 (0~23)
    private double discountRate;  // 할인율 (0~100)
}
