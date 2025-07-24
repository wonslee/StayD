package org.example.stayd.domain.cafe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import org.example.stayd.domain.reservation.dto.ReservationWithCafeDTO;

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

        @Override
        public String toString() {
            return "DetailResponse{" +
                    "cafeId=" + cafeId +
                    ", name='" + name + '\'' +
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
     * 카페 리스트용 간단한 DTO (컨트롤러에서 사용)
     */
    @Getter
    @Setter
    public static class SimpleCafeDto {
        private int id;
        private String name;
        private String address;
        private double rating;
        private int reviewCount;
        private String imageUrl;
        private boolean isFavorite;
        private Integer pricePerHour;
        private String description;
        private String phoneNumber;
        private String operatingDays;   // "월,화,수,목,금" 형태
        private String operatingHours;  // "09:00-18:00" 형태

        // 기본 생성자
        public SimpleCafeDto() {}

        // 리스트용 생성자
        public SimpleCafeDto(int id, String name, double rating, int reviewCount, String imageUrl) {
            this.id = id;
            this.name = name;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.imageUrl = imageUrl;
            this.isFavorite = false; // 기본값
        }

        // 전체 정보 생성자
        public SimpleCafeDto(int id, String name, String address, double rating, int reviewCount,
                             String imageUrl, boolean isFavorite, Integer pricePerHour,
                             String description, String phoneNumber, String operatingDays, String operatingHours) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.imageUrl = imageUrl;
            this.isFavorite = isFavorite;
            this.pricePerHour = pricePerHour;
            this.description = description;
            this.phoneNumber = phoneNumber;
            this.operatingDays = operatingDays;
            this.operatingHours = operatingHours;
        }
    }
    /**
     * 카페 수정 요청 DTO
     */
    @Setter
    @Getter
    public static class UpdateRequest {
        private Long cafeId;
        private String name;
        private String address;
        private Integer pricePerHour;
        private String description;
        private String phoneNumber;
        private String imageUrl;
        private List<String> operatingDays;
        private Integer operatingStartHour;
        private Integer operatingEndHour;

        public UpdateRequest() {}

        @Override
        public String toString() {
            return "UpdateRequest{" +
                    "cafeId=" + cafeId +
                    ", name='" + name + '\'' +
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
     * 카페 수정 응답 DTO
     */
    @Setter
    @Getter
    public static class UpdateResponse {
        private boolean success;
        private String message;

        public UpdateResponse() {}

        public UpdateResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        @Override
        public String toString() {
            return "UpdateResponse{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * 카페 삭제 응답 DTO
     */
    @Setter
    @Getter
    public static class DeleteResponse {
        private boolean success;
        private String message;

        public DeleteResponse() {}

        public DeleteResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        @Override
        public String toString() {
            return "DeleteResponse{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

}