package org.example.stayd.domain.cafe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 스터디 카페 생성을 위한 DTO 클래스
 */
public class CafeDto {

    /**
     * 카페 생성 요청 DTO
     */
    @Setter
    @Getter
    public static class CreateRequest {
        // Getters and Setters
        private String name;
        private String address;
        private Integer pricePerHour;
        private String description;
        private String phoneNumber;
        private String imageUrl;
        private List<String> operatingDays; // 영업일 (월, 화, 수, 목, 금, 토, 일)
        private Integer operatingStartHour; // 운영 시작 시간 (0~23)
        private Integer operatingEndHour;   // 운영 종료 시간 (0~23)

        // 기본 생성자
        public CreateRequest() {}

        @Override
        public String toString() {
            return "CreateRequest{" +
                    "name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", pricePerHour=" + pricePerHour +
                    ", description='" + description + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", operatingDays=" + operatingDays +
                    ", operatingStartHour=" + operatingStartHour +
                    ", operatingEndHour=" + operatingEndHour +
                    '}';
        }
    }

    /**
     * 카페 생성 응답 DTO
     */
    @Setter
    @Getter
    public static class CreateResponse {
        // Getters and Setters
        private Long cafeId;
        private String message;
        private boolean success;

        public CreateResponse() {}

        public CreateResponse(Long cafeId, String message, boolean success) {
            this.cafeId = cafeId;
            this.message = message;
            this.success = success;
        }

        @Override
        public String toString() {
            return "CreateResponse{" +
                    "cafeId=" + cafeId +
                    ", message='" + message + '\'' +
                    ", success=" + success +
                    '}';
        }
    }

    /**
     * 운영시간 DTO
     */
    @Setter
    @Getter
    public static class OperatingHours {
        // Getters and Setters
        private String dayOfWeek; // MON, TUE, WED, THU, FRI, SAT, SUN
        private Integer operationStart;
        private Integer operationEnd;

        public OperatingHours() {}

        public OperatingHours(String dayOfWeek, Integer operationStart, Integer operationEnd) {
            this.dayOfWeek = dayOfWeek;
            this.operationStart = operationStart;
            this.operationEnd = operationEnd;
        }

        @Override
        public String toString() {
            return "OperatingHours{" +
                    "dayOfWeek='" + dayOfWeek + '\'' +
                    ", operationStart=" + operationStart +
                    ", operationEnd=" + operationEnd +
                    '}';
        }
    }

    @Setter
    @Getter
    public static class DetailResponse {
        // 🔹 모든 getter/setter 메소드들
        // 🔹 모든 필드를 맨 위에 선언
        private Long cafeId;
        private String name;
        private String address;
        private Integer pricePerHour;
        private String description;
        private String phoneNumber;
        private String imageUrl;
        private List<String> operatingDays;        // 이 3개 필드를 위로 이동
        private Integer operatingStartHour;
        private Integer operatingEndHour;

        // 🔹 기본 생성자
        public DetailResponse() {}

        // 🔹 전체 매개변수 생성자
        public DetailResponse(Long cafeId, String name, String address, Integer pricePerHour,
                              String description, String phoneNumber, String imageUrl,
                              List<String> operatingDays, Integer operatingStartHour, Integer operatingEndHour) {
            this.cafeId = cafeId;
            this.name = name;
            this.address = address;
            this.pricePerHour = pricePerHour;
            this.description = description;
            this.phoneNumber = phoneNumber;
            this.imageUrl = imageUrl;
            this.operatingDays = operatingDays;
            this.operatingStartHour = operatingStartHour;
            this.operatingEndHour = operatingEndHour;
        }

    }

}