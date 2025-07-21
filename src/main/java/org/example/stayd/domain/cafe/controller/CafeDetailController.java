package org.example.stayd.domain.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CafeDetailController implements Initializable {

    // 상단 정보
    @FXML private Label cafeNameLabel;
    @FXML private Label ratingLabel;
    @FXML private Button favoriteButton;
    @FXML private ImageView favoriteIcon;

    // 네비게이션 버튼들
    @FXML private Button detailButton;
    @FXML private Button reservationButton;
    @FXML private Button reviewButton;

    // 목록 페이지 버튼
    @FXML private Button backButton;

    // 상세 정보 요소들
    @FXML private ImageView mainImageView;
    @FXML private Label locationLabel;
    @FXML private Label businessDaysLabel;
    @FXML private Label operatingHoursLabel;
    @FXML private Label priceLabel;
    @FXML private Label phoneLabel;
    @FXML private Label descriptionLabel;

    // 탭 컨텐츠
    @FXML private VBox detailTabContent;
    @FXML private VBox reservationTabContent;
    @FXML private VBox reviewTabContent;

    // 상태 변수들
    private boolean isFavorite = false;
    private String currentTab = "detail";

    // 카페 데이터 (실제로는 데이터베이스에서 받아올 데이터)
    private String cafeName;
    private String location;
    private String businessDays;
    private String operatingHours;
    private int hourlyPrice;
    private String phoneNumber;
    private String description;
    private String imageUrl;
    private double rating;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초기 데이터 설정
        loadCafeData(1L);

        // 기본적으로 상세 탭이 활성화
        showDetailTab(null);
    }

    /**
     * 목록 페이지로 돌아가기
     */
    @FXML
    private void goBackToList(ActionEvent event) {
        try {
            // 현재 Stage 가져오기
            Stage currentStage = (Stage) backButton.getScene().getWindow();

            // 목록 페이지 FXML 로드
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/stayd/cafe/cafeListView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            // 목록 페이지로 화면 전환
            currentStage.setTitle("StayD - 카페 목록");
            currentStage.setScene(scene);

            System.out.println("목록 페이지로 돌아가기 완료");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("목록 페이지로 돌아가기 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 카페 데이터를 UI에 로드
     */
    private void loadCafeData(Long cafeId) {
        cafeName = "작심 스터디카페 해치점";
        location = "서울특별시 종로구 창경궁로 254";
        businessDays = "월, 화, 수, 목, 금";
        operatingHours = "09:00 - 18:00";
        hourlyPrice = 10000;
        phoneNumber = "010-1234-4567";
        description = "조용하고 깨끗한 환경에서 집중해서 공부할 수 있는 스터디 카페입니다. 다양한 음료와 간단한 간식도 제공하며, 무료 Wi-Fi와 콘센트가 모든 좌석에 구비되어 있습니다.";
        imageUrl = "";
        rating = 4.5;

        cafeNameLabel.setText(cafeName);
        ratingLabel.setText(String.valueOf(rating));
        locationLabel.setText(location);
        businessDaysLabel.setText(businessDays);
        operatingHoursLabel.setText(operatingHours);
        priceLabel.setText(String.format("%,d원", hourlyPrice));
        phoneLabel.setText(phoneNumber);
        descriptionLabel.setText(description);

        // 이미지 로드 (URL이 있는 경우)
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                Image image = new Image(imageUrl);
                mainImageView.setImage(image);
            } catch (Exception e) {
                // 이미지 로드 실패 시 기본 이미지 유지
                System.out.println("이미지 로드 실패: " + e.getMessage());
            }
        }
    }

    /**
     * 찜하기 버튼 토글
     */
    @FXML
    private void toggleFavorite(ActionEvent event) {
        isFavorite = !isFavorite;

        if (isFavorite) {
            favoriteButton.setStyle("-fx-background-color: #4CAF4F; -fx-border-color: #4CAF4F; -fx-border-radius: 5; -fx-background-radius: 5;");
            // 찜한 상태의 아이콘으로 변경 (하트 채움)
            try {
                favoriteIcon.setImage(new Image("@../../../../images/cafeImages/HeartFilled.png"));
            } catch (Exception e) {
                // 아이콘 로드 실패 시 텍스트로 대체
                System.out.println("찜 아이콘 로드 실패");
            }
        } else {
            favoriteButton.setStyle("-fx-background-color: white; -fx-border-color: #4CAF4F; -fx-border-radius: 5; -fx-background-radius: 5;");
            // 찜하지 않은 상태의 아이콘으로 변경 (하트 비움)
            try {
                favoriteIcon.setImage(new Image("@../../../../images/cafeImages/Heart.png"));
            } catch (Exception e) {
                System.out.println("찜 아이콘 로드 실패");
            }
        }

        // 찜하기 상태를 데이터베이스에 저장하는 로직 추가
        saveFavoriteStatus();
    }

    /**
     * 상세 탭 표시
     */
    @FXML
    private void showDetailTab(ActionEvent event) {
        if (!currentTab.equals("detail")) {
            currentTab = "detail";

            // 버튼 스타일 변경
            detailButton.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 0; -fx-font-weight: bold;");
            reservationButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
            reviewButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");

            // 탭 컨텐츠 표시/숨김
            detailTabContent.setVisible(true);
            detailTabContent.setManaged(true);

            reservationTabContent.setVisible(false);
            reservationTabContent.setManaged(false);

            reviewTabContent.setVisible(false);
            reviewTabContent.setManaged(false);
        }
    }

    /**
     * 예약 탭 표시
     */
    @FXML
    private void showReservationTab(ActionEvent event) {
        if (!currentTab.equals("reservation")) {
            currentTab = "reservation";

            // 버튼 스타일 변경
            detailButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
            reservationButton.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 0; -fx-font-weight: bold;");
            reviewButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");

            // 탭 컨텐츠 표시/숨김
            detailTabContent.setVisible(false);
            detailTabContent.setManaged(false);

            reservationTabContent.setVisible(true);
            reservationTabContent.setManaged(true);

            reviewTabContent.setVisible(false);
            reviewTabContent.setManaged(false);
        }
    }

    /**
     * 리뷰 탭 표시
     */
    @FXML
    private void showReviewTab(ActionEvent event) {
        if (!currentTab.equals("review")) {
            currentTab = "review";

            // 버튼 스타일 변경
            detailButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
            reservationButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
            reviewButton.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 0; -fx-font-weight: bold;");

            // 탭 컨텐츠 표시/숨김
            detailTabContent.setVisible(false);
            detailTabContent.setManaged(false);

            reservationTabContent.setVisible(false);
            reservationTabContent.setManaged(false);

            reviewTabContent.setVisible(true);
            reviewTabContent.setManaged(true);
        }
    }

    /**
     * 외부에서 카페 데이터를 설정하는 메소드
     * (다른 페이지에서 카페 상세 정보를 전달받을 때 사용)
     */
    public void setCafeData(String cafeName, String location, String businessDays,
                            String operatingHours, int hourlyPrice, String phoneNumber,
                            String description, String imageUrl, double rating) {
        this.cafeName = cafeName;
        this.location = location;
        this.businessDays = businessDays;
        this.operatingHours = operatingHours;
        this.hourlyPrice = hourlyPrice;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;

        // UI 업데이트
        loadCafeData(1L);
    }

    /**
     * 찜하기 상태를 데이터베이스에 저장
     */
    private void saveFavoriteStatus() {
        // TODO: 데이터베이스에 찜하기 상태 저장 로직 구현
        System.out.println("찜하기 상태 저장: " + isFavorite);
    }

    /**
     * 현재 표시된 탭 반환
     */
    public String getCurrentTab() {
        return currentTab;
    }

    /**
     * 찜하기 상태 반환
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    /**
     * 찜하기 상태 설정 (외부에서 호출)
     */
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
        // UI 업데이트를 위해 toggleFavorite 로직 재사용
        if (this.isFavorite != favorite) {
            toggleFavorite(null);
        }
    }
}