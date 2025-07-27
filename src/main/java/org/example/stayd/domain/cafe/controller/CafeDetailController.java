// 작성자 : 최영준, 방대혁, 이원석
package org.example.stayd.domain.cafe.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.stayd.domain.cafe.dto.CafeDto.DetailResponse;
import org.example.stayd.domain.cafe.service.CafeService;
import org.example.stayd.domain.reservation.controller.ReservationCreateController;
import org.example.stayd.domain.review.controller.ReviewListController;

public class CafeDetailController implements Initializable {

    // 상단 정보
    @FXML
    private Label cafeNameLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Button favoriteButton;
    @FXML
    private ImageView favoriteIcon;

    // 네비게이션 버튼들
    @FXML
    private Button detailButton;
    @FXML
    private Button reservationButton;
    @FXML
    private Button reviewButton;

    // 목록 페이지 버튼
    @FXML
    private Button backButton;

    // 상세 정보 요소들
    @FXML
    private ImageView mainImageView;
    @FXML
    private Label locationLabel;
    @FXML
    private Label businessDaysLabel;
    @FXML
    private Label operatingHoursLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label descriptionLabel;

    // 탭 컨텐츠
    @FXML
    private VBox detailTabContent;
    @FXML
    private VBox reservationTabContent;
    @FXML
    private VBox reviewTabContent;
    private boolean reviewLoaded = false;
    // 상태 변수들
    private boolean isFavorite = false;
    /**
     * -- GETTER -- 현재 표시된 탭 반환
     */
//    @Getter
    private String currentTab = "detail";
    private boolean reservationLoaded = false;

    // 카페 데이터
    private int cafeId;
    private String cafeName;
    private String location;
    private String businessDays;
    private String operatingHours;
    private int hourlyPrice;
    private String phoneNumber;
    private String description;
    private String imageUrl;
    private double rating;
    /* … */
    private final CafeService cafeService;

    // 이미지 캐시 추가
    private final Map<String, Image> imageCache = new HashMap<>();

    // 🔹 기본 생성자 (FXML용 - 필수!)
    public CafeDetailController() {
        this.cafeService = new CafeService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 기본적으로 상세 탭이 활성화
        showDetailTab(null);
    }

    /**
     * 목록 페이지로 돌아가기 (최적화됨)
     */
    @FXML
    private void goBackToList(ActionEvent event) {
//        PerformanceMonitor.measureTime("Navigation - Back to List", () -> {
        try {
            // 현재 Stage 가져오기
            Stage currentStage = (Stage) backButton.getScene().getWindow();

            // 목록 페이지 FXML 로드
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/stayd/cafe/cafeListView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            // 목록 페이지로 화면 전환
            currentStage.setTitle("StayD - Cafe List");
            currentStage.setScene(scene);

            System.out.println("Back to list completed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error going back to list: " + e.getMessage());
        }
//        });
    }


    /**
     * 새로운 메서드: UI 업데이트만 담당
     */
    private void updateUI() {
        // 텍스트 정보 즉시 설정
        cafeNameLabel.setText(cafeName);
        ratingLabel.setText(String.valueOf(rating));
        locationLabel.setText(location);
        businessDaysLabel.setText(businessDays);
        operatingHoursLabel.setText(operatingHours);
        priceLabel.setText(String.format("%,d원", hourlyPrice));
        phoneLabel.setText(phoneNumber);
        descriptionLabel.setText(description);

        // 비동기 이미지 로딩
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            loadImageAsync(mainImageView, imageUrl);
        }
    }

    /**
     * 🚀 비동기 이미지 로딩 (캐시 적용)
     */
    private void loadImageAsync(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        // 캐시에서 확인
        if (imageCache.containsKey(imageUrl)) {
//            System.out.println("Detail image cache hit");
            imageView.setImage(imageCache.get(imageUrl));
            return;
        }

        Task<Image> imageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                long startTime = System.currentTimeMillis();
                Image image = new Image(imageUrl, true); // 백그라운드에서 로딩
                long duration = System.currentTimeMillis() - startTime;

                System.out.println("[ASYNC] Detail image loaded in " + duration + "ms");
                return image;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Image loadedImage = getValue();
                    imageCache.put(imageUrl, loadedImage); // 캐시에 저장
                    imageView.setImage(loadedImage);
                    System.out.println("Detail image set successfully");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.out.println("Detail image load failed: " + getException().getMessage());
                    // 기본 이미지 설정
                    try {
                        imageView.setImage(new Image("https://via.placeholder.com/400x300?text=No+Image"));
                    } catch (Exception e) {
                        System.out.println("Default detail image also failed");
                    }
                });
            }
        };

        Thread imageThread = new Thread(imageTask);
        imageThread.setDaemon(true);
        imageThread.start();
    }

    /**
     * 찜하기 버튼 토글
     */
    @FXML
    private void toggleFavorite(ActionEvent event) {
        isFavorite = !isFavorite;

        if (isFavorite) {
            favoriteButton.setStyle(
                    "-fx-background-color: #4CAF4F; -fx-border-color: #4CAF4F; -fx-border-radius: 5; -fx-background-radius: 5;");
            // 찜한 상태의 아이콘으로 변경 (하트 채움)
            try {
                favoriteIcon.setImage(new Image("@../../../../images/cafeImages/HeartFilled.png"));
            } catch (Exception e) {
                // 아이콘 로드 실패 시 텍스트로 대체
                System.out.println("찜 아이콘 로드 실패");
            }
        } else {
            favoriteButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: #4CAF4F; -fx-border-radius: 5; -fx-background-radius: 5;");
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
            reservationButton.setStyle(
                    "-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
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
            reservationButton.setStyle(
                    "-fx-background-color: #4CAF4F; -fx-background-radius: 0; -fx-font-weight: bold;");
            reviewButton.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");

            // 탭 컨텐츠 표시/숨김
            detailTabContent.setVisible(false);
            detailTabContent.setManaged(false);

            reservationTabContent.setVisible(true);
            reservationTabContent.setManaged(true);

            reviewTabContent.setVisible(false);
            reviewTabContent.setManaged(false);
            reservationTabContent.setVisible(true);
            reservationTabContent.setManaged(true);

            if (!reservationLoaded) {
                try {
                    // CHANGED: Use classic FXML loading with fx:controller
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/org/example/stayd/reservation/reservation-view.fxml"));
                    Node reservationView = loader.load();

                    // CHANGED: Get controller and inject cafe data after loading
                    ReservationCreateController controller = loader.getController();
                    DetailResponse cafeDto = cafeService.getCafeDetail((long) cafeId); // or actual selected cafe
                    controller.setCafe(cafeDto);

                    reservationTabContent.getChildren().setAll(reservationView);
                    reservationLoaded = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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
            reservationButton.setStyle(
                    "-fx-background-color: #A8D6AA; -fx-background-radius: 0; -fx-font-weight: bold;");
            reviewButton.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 0; -fx-font-weight: bold;");

            // 탭 컨텐츠 표시/숨김
            detailTabContent.setVisible(false);
            detailTabContent.setManaged(false);

            reservationTabContent.setVisible(false);
            reservationTabContent.setManaged(false);

            reviewTabContent.setVisible(true);
            reviewTabContent.setManaged(true);

            // 최초 로드 여부 체크
            if (!reviewLoaded) {
                try {
                    // FXML 파일 로딩
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/org/example/stayd/review/review-list.fxml"));
                    Node reviewView = loader.load();

                    // 컨트롤러 가져와서 cafeId 주입
                    ReviewListController controller = loader.getController();
                    controller.setCafeId(cafeId); // cafeId는 현재 상세페이지 카페 ID

                    // 탭 영역에 뷰 삽입
                    reviewTabContent.getChildren().setAll(reviewView);

                    reviewLoaded = true; // 로드 완료 표시
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 외부에서 카페 데이터를 설정하는 메소드 (최적화됨) (다른 페이지에서 카페 상세 정보를 전달받을 때 사용)
     */
    public void setCafeData(int cafeId, String cafeName, String location, String businessDays,
                            String operatingHours, int hourlyPrice, String phoneNumber,
                            String description, String imageUrl, double rating) {

//        PerformanceMonitor.measureTime("Detail - Set Cafe Data", () -> {
        // 데이터 설정
        this.cafeId = cafeId;
        this.cafeName = cafeName;
        this.location = location;
        this.businessDays = businessDays;
        this.operatingHours = operatingHours;
        this.hourlyPrice = hourlyPrice;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;

        // 즉시 UI 업데이트 (DB 호출 없이)
        updateUI();
//        });
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

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     *
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        backButton.setStyle(
                "-fx-background-color: #4caf4f; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setTextFill(javafx.scene.paint.Color.WHITE);
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     *
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        backButton.setStyle(
                "-fx-background-color: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setTextFill(javafx.scene.paint.Color.web("#4caf4f"));
    }
}