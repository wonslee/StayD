//package org.example.stayd.domain.discount.service;
//
//import org.example.stayd.domain.discount.dao.DiscountDAO;
//import org.example.stayd.domain.reservation.dao.ReservationDao;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class DiscountNotificationService {
//
//    private ReservationDao reservationDao;
//    private DiscountDAO discountDao;
//    private NotificationService notificationService;
//
//    public DiscountNotificationService() {
//        this.reservationDao = new ReservationDao();
//        this.discountDao = new DiscountDAO();
//        this.notificationService = new NotificationService();
//    }
//
//    public void sendDiscountNotification(int cafeId, String dayOfWeek) {
//        try {
//            // 카페 이용 기록이 있는 이용자 추출
//            List<Integer> userIds = reservationDao.getUserIdsByCafe(cafeId, dayOfWeek);
//
//            // 카페 할인 정보 추출
//            DiscountInfo discountInfo = discountDao.getDiscountInfo(cafeId, dayOfWeek);
//
//            // 3. 각 이용자에게 알림 보내기
//            for (Integer userId : userIds) {
//                // 알림 내용 구성
//                String message = "할인 안내: " + discountInfo.getDiscountRate() + "% 할인! "
//                        + "기간: " + discountInfo.getDiscountStart() + ":00 ~ " + discountInfo.getDiscountEnd() + ":00";
//
//                // 알림 전송 (이메일, 푸시 알림 등)
//                notificationService.sendNotification(userId, message);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // 예외 처리
//        }
//    }
//}
