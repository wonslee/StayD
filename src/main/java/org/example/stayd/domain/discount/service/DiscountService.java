// 작성자 : 방대혁
package org.example.stayd.domain.discount.service;

import org.example.stayd.domain.discount.dao.DiscountDAO;
import org.example.stayd.domain.discount.dto.DiscountDTO;

/**
 * 할인 설정을 처리하는 서비스 클래스
 */
public class DiscountService {

    private final DiscountDAO discountDAO;

    /**
     * DiscountService 생성자 DiscountDAO 객체를 생성하여 데이터베이스 연동 준비
     */
    public DiscountService() {
        this.discountDAO = new DiscountDAO();
    }

    /**
     * 할인 설정 정보를 저장하는 메서드
     *
     * @param discountDTO 할인 설정 정보 (할인 날짜, 시간, 할인율)
     * @return 저장 성공 여부
     */
    public boolean saveDiscountSettings(DiscountDTO discountDTO) {
        // DiscountDAO의 saveDiscountSettings 메서드를 호출하여 할인 설정 정보를 DB에 저장
        return discountDAO.saveDiscountSettings(discountDTO);
    }
}