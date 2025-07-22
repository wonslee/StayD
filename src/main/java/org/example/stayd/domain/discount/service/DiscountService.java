package org.example.stayd.domain.discount.service;

import org.example.stayd.domain.discount.dao.DiscountDAO;

import java.sql.Date;

public class DiscountService {

    private DiscountDAO discountDAO;

    public DiscountService() {
        this.discountDAO = new DiscountDAO();
    }

    // 할인 설정 저장
    public boolean saveDiscountSettings(Date selectedDate, int startTime, int endTime, double discountRate) {
        return discountDAO.saveDiscountSettings(selectedDate, startTime, endTime, discountRate);
    }
}
