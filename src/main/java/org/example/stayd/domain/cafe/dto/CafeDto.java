package org.example.stayd.domain.cafe.dto;

import java.util.List;

/**
 * 스터디 카페 생성을 위한 DTO 클래스
 */
public class CafeDto {

    /**
     * 카페 생성 요청 DTO
     */
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

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getPricePerHour() {
            return pricePerHour;
        }

        public void setPricePerHour(Integer pricePerHour) {
            this.pricePerHour = pricePerHour;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public List<String> getOperatingDays() {
            return operatingDays;
        }

        public void setOperatingDays(List<String> operatingDays) {
            this.operatingDays = operatingDays;
        }

        public Integer getOperatingStartHour() {
            return operatingStartHour;
        }

        public void setOperatingStartHour(Integer operatingStartHour) {
            this.operatingStartHour = operatingStartHour;
        }

        public Integer getOperatingEndHour() {
            return operatingEndHour;
        }

        public void setOperatingEndHour(Integer operatingEndHour) {
            this.operatingEndHour = operatingEndHour;
        }

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

        // Getters and Setters
        public Long getCafeId() {
            return cafeId;
        }

        public void setCafeId(Long cafeId) {
            this.cafeId = cafeId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
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

        // Getters and Setters
        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public Integer getOperationStart() {
            return operationStart;
        }

        public void setOperationStart(Integer operationStart) {
            this.operationStart = operationStart;
        }

        public Integer getOperationEnd() {
            return operationEnd;
        }

        public void setOperationEnd(Integer operationEnd) {
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
}