package org.example.stayd.domain.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.example.stayd.domain.cafe.dto.CafeDto;
import org.example.stayd.domain.cafe.service.CafeService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.example.stayd.common.PerformanceMonitor;

public class CafeListController implements Initializable {

    // 검색 관련
    @FXML private TextField searchField;
    @FXML private Button sortButton;

    // 카페 카드들 (8개)
    @FXML private VBox cafeCard0, cafeCard1, cafeCard2, cafeCard3;
    @FXML private VBox cafeCard4, cafeCard5, cafeCard6, cafeCard7;

    // 카페 이미지들
    @FXML private ImageView cafeImage0, cafeImage1, cafeImage2, cafeImage3;
    @FXML private ImageView cafeImage4, cafeImage5, cafeImage6, cafeImage7;

    // 카페 정보 레이블들
    @FXML private Label cafeName0, cafeName1, cafeName2, cafeName3;
    @FXML private Label cafeName4, cafeName5, cafeName6, cafeName7;
    @FXML private Label cafeRating0, cafeRating1, cafeRating2, cafeRating3;
    @FXML private Label cafeRating4, cafeRating5, cafeRating6, cafeRating7;
    @FXML private Label cafeReviewCount0, cafeReviewCount1, cafeReviewCount2, cafeReviewCount3;
    @FXML private Label cafeReviewCount4, cafeReviewCount5, cafeReviewCount6, cafeReviewCount7;

    // 찜하기 버튼들
    @FXML private Button favoriteBtn0, favoriteBtn1, favoriteBtn2, favoriteBtn3;
    @FXML private Button favoriteBtn4, favoriteBtn5, favoriteBtn6, favoriteBtn7;

    // 페이지네이션 버튼들
    @FXML private Button page1Button, page2Button, page3Button, page4Button, page5Button;

    // 상태 변수들
    private boolean isSortByLatest = true; // true: 최신순, false: 평점순
    /**
     * -- GETTER --
     *  현재 페이지 반환
     */

    @Getter
    private int currentPage = 1;
    private int totalPages = 5;
    // 현재 검색 키워드 반환
    @Getter
    private String currentSearchKeyword = "";
    private List<CafeData> allCafes = new ArrayList<>();
    private List<CafeData> filteredCafes = new ArrayList<>();

    // 카페 데이터 클래스
    @Getter
    @Setter
    public static class CafeData {
        private String name;
        private double rating;
        private int reviewCount;
        private String imageUrl;
        private boolean isFavorite;
        private int cafeId;

        public CafeData(int cafeId, String name, double rating, int reviewCount, String imageUrl, boolean isFavorite) {
            this.cafeId = cafeId;
            this.name = name;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.imageUrl = imageUrl;
            this.isFavorite = isFavorite;
        }

        public boolean isFavorite() { return isFavorite; }
        public void setFavorite(boolean favorite) { isFavorite = favorite; }

    }

    private CafeService cafeService;
    // 이미지 캐시 추가
    private final Map<String, Image> imageCache = new HashMap<>();

    // 🔹 기본 생성자 (FXML용 - 필수!)
    public CafeListController() {
        this.cafeService = new CafeService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadCafesFromDB();

        // 나머지는 동일
        displayCurrentPage();
        updatePaginationButtons();
    }

    /**
     * DB에서 카페 데이터 로드 (정렬 적용)
     */
    private void loadCafesFromDB() {
        try {
            // 현재 정렬 상태에 따라 데이터 가져오기
            boolean sortByRating = !isSortByLatest; // 최신순이 아니면 평점순
            List<CafeDto.SimpleCafeDto> cafeList = cafeService.getAllCafes(sortByRating);

            allCafes.clear();

            // DTO를 내부 CafeData로 변환
            for (CafeDto.SimpleCafeDto dto : cafeList) {
                allCafes.add(new CafeData(
                        dto.getId(),
                        dto.getName(),
                        dto.getRating(),
                        dto.getReviewCount(),
                        dto.getImageUrl(),
                        dto.isFavorite()
                ));
            }

            filteredCafes = new ArrayList<>(allCafes);
            totalPages = (int) Math.ceil(filteredCafes.size() / 8.0);

            System.out.println("Cafe data loaded successfully: " + allCafes.size() + " cafes (Sort by rating: " + sortByRating + ")");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load cafe data: " + e.getMessage());
            // 실패 시 빈 리스트로 초기화
            allCafes.clear();
            filteredCafes.clear();
            totalPages = 1;
        }
    }


    /**
     * 검색 메서드 수정 (정렬 적용)
     */
    @FXML
    private void searchCafes(ActionEvent event) {
        currentSearchKeyword = searchField.getText().trim();
        currentPage = 1;

        try {
            boolean sortByRating = !isSortByLatest; // 최신순이 아니면 평점순

            if (currentSearchKeyword.isEmpty()) {
                // 전체 카페 다시 로드 (정렬 적용)
                List<CafeDto.SimpleCafeDto> cafeList = cafeService.getAllCafes(sortByRating);
                filteredCafes.clear();
                for (CafeDto.SimpleCafeDto dto : cafeList) {
                    filteredCafes.add(convertToCafeData(dto));
                }
            } else {
                // DB에서 검색어로 필터링된 결과 가져오기 (정렬 적용)
                List<CafeDto.SimpleCafeDto> searchResults = cafeService.searchCafesByName(currentSearchKeyword, sortByRating);
                filteredCafes.clear();
                for (CafeDto.SimpleCafeDto dto : searchResults) {
                    filteredCafes.add(convertToCafeData(dto));
                }
            }

            // 프론트엔드 정렬 제거 (DB에서 이미 정렬되어 옴)
            // applySorting(); // 이 줄 주석처리 또는 삭제

            // 총 페이지 수 업데이트
            totalPages = Math.max(1, (int) Math.ceil(filteredCafes.size() / 8.0));

            // 결과 표시
            displayCurrentPage();
            updatePaginationButtons();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchCafes(null);
        }
    }

    /**
     * 정렬 순서 토글 (DB에서 정렬하도록 수정)
     */
    @FXML
    private void toggleSortOrder(ActionEvent event) {
        isSortByLatest = !isSortByLatest; // 클릭할 때마다 true ↔ false로 반전시켜 정렬 기준을 변경

        if (isSortByLatest) {
            sortButton.setText("최신순 ▼");
        } else {
            sortButton.setText("평점순 ▼");
        }

        // DB에서 새로운 정렬로 데이터 다시 로드
        if (currentSearchKeyword.isEmpty()) {
            loadCafesFromDB(); // 전체 데이터 다시 로드
        } else {
            searchCafes(null); // 검색 결과 다시 로드
        }

        // 현재 페이지 다시 표시
        displayCurrentPage();
    }


    /**
     * 현재 페이지의 카페들을 화면에 표시
     */
    private void displayCurrentPage() {
//        PerformanceMonitor.measureTime("UI - Display Cafe Cards", () -> {
            VBox[] cafeCards = {cafeCard0, cafeCard1, cafeCard2, cafeCard3, cafeCard4, cafeCard5, cafeCard6, cafeCard7};
            ImageView[] cafeImages = {cafeImage0, cafeImage1, cafeImage2, cafeImage3, cafeImage4, cafeImage5, cafeImage6, cafeImage7};
            Label[] cafeNames = {cafeName0, cafeName1, cafeName2, cafeName3, cafeName4, cafeName5, cafeName6, cafeName7};
            Label[] cafeRatings = {cafeRating0, cafeRating1, cafeRating2, cafeRating3, cafeRating4, cafeRating5, cafeRating6, cafeRating7};
            Label[] cafeReviewCounts = {cafeReviewCount0, cafeReviewCount1, cafeReviewCount2, cafeReviewCount3, cafeReviewCount4, cafeReviewCount5, cafeReviewCount6, cafeReviewCount7};
            Button[] favoriteBtns = {favoriteBtn0, favoriteBtn1, favoriteBtn2, favoriteBtn3, favoriteBtn4, favoriteBtn5, favoriteBtn6, favoriteBtn7};

            int startIndex = (currentPage - 1) * 8;

            for (int i = 0; i < 8; i++) {
                int dataIndex = startIndex + i;

                if (dataIndex < filteredCafes.size()) {
                    CafeData cafe = filteredCafes.get(dataIndex);

                    // 카페 정보 표시 (즉시)
                    cafeCards[i].setVisible(true);
                    cafeCards[i].setManaged(true);
                    cafeNames[i].setText(cafe.getName());
                    cafeRatings[i].setText(String.format("%.1f", cafe.getRating()));
                    cafeReviewCounts[i].setText("(" + cafe.getReviewCount() + ")");

                    // 비동기 이미지 로딩으로 변경
                    loadImageAsync(cafeImages[i], cafe.getImageUrl(), i);

                    // 찜하기 버튼 상태 설정
                    updateFavoriteButton(favoriteBtns[i], cafe.isFavorite());

                } else {
                    // 데이터가 없는 카드는 숨김
                    cafeCards[i].setVisible(false);
                    cafeCards[i].setManaged(false);
                }
            }
//        });
    }
    /**
     * 비동기 이미지 로딩 (캐시 적용)
     */
    private void loadImageAsync(ImageView imageView, String imageUrl, int cardIndex) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        // 캐시에서 확인
        if (imageCache.containsKey(imageUrl)) {
//            System.out.println("Image cache hit for card " + cardIndex);
            imageView.setImage(imageCache.get(imageUrl));
            return;
        }

        Task<Image> imageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                long startTime = System.currentTimeMillis();
                Image image = new Image(imageUrl, true); // 백그라운드에서 로딩
                long duration = System.currentTimeMillis() - startTime;

//                System.out.println("[ASYNC] Image loaded for card " + cardIndex + " in " + duration + "ms");
                return image;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Image loadedImage = getValue();
                    imageCache.put(imageUrl, loadedImage); // 캐시에 저장
                    imageView.setImage(loadedImage);
//                    System.out.println("Image set for card " + cardIndex);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.out.println("Image load failed for card " + cardIndex + ": " + getException().getMessage());
                    // 기본 이미지 설정
                    try {
                        imageView.setImage(new Image("https://via.placeholder.com/200x150?text=No+Image"));
                    } catch (Exception e) {
                        System.out.println("Default image also failed for card " + cardIndex);
                    }
                });
            }
        };

        // 데몬 스레드로 실행
        Thread imageThread = new Thread(imageTask);
        imageThread.setDaemon(true);
        imageThread.start();
    }


    /**
     * 찜하기 버튼 상태 업데이트
     */
    private void updateFavoriteButton(Button favoriteBtn, boolean isFavorite) {
        if (isFavorite) {
            favoriteBtn.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 15; -fx-border-radius: 15;");
            // 찜한 상태 아이콘으로 변경
            try {
                ImageView icon = (ImageView) favoriteBtn.getGraphic();
                icon.setImage(new Image("https://cdn-icons-png.flaticon.com/512/803/803087.png")); // 채워진 하트
            } catch (Exception e) {
                System.out.println("찜 아이콘 변경 실패");
            }
        } else {
            favoriteBtn.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15;");
            try {
                ImageView icon = (ImageView) favoriteBtn.getGraphic();
                icon.setImage(new Image("https://m.yconcepts.co.kr/web/upload/icon_202302260647299800.png")); // 빈 하트
            } catch (Exception e) {
                System.out.println("찜 아이콘 변경 실패");
            }
        }
    }

    /**
     * 찜하기 버튼 클릭
     */
    @FXML
    private void toggleFavorite(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        // 어떤 카페 카드의 찜하기 버튼인지 찾기
        int cardIndex = getCardIndexFromFavoriteButton(clickedButton);

        if (cardIndex >= 0) {
            int dataIndex = (currentPage - 1) * 8 + cardIndex;

            if (dataIndex < filteredCafes.size()) {
                CafeData cafe = filteredCafes.get(dataIndex);
                cafe.setFavorite(!cafe.isFavorite());

                // 버튼 상태 업데이트
                updateFavoriteButton(clickedButton, cafe.isFavorite());

                // 데이터베이스에 찜하기 상태 저장
                saveFavoriteStatus(cafe.getCafeId(), cafe.isFavorite());
            }
        }
    }

    /**
     * 찜하기 버튼으로부터 카드 인덱스 찾기
     */
    private int getCardIndexFromFavoriteButton(Button favoriteBtn) {
        Button[] favoriteBtns = {favoriteBtn0, favoriteBtn1, favoriteBtn2, favoriteBtn3, favoriteBtn4, favoriteBtn5, favoriteBtn6, favoriteBtn7};

        for (int i = 0; i < favoriteBtns.length; i++) {
            if (favoriteBtns[i] == favoriteBtn) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 카페 카드 클릭 (상세 페이지로 이동)
     */
    @FXML
    private void onCafeCardClick(MouseEvent event) {
        VBox clickedCard = (VBox) event.getSource();
        int cardIndex = getCardIndexFromVBox(clickedCard);

        if (cardIndex >= 0) {
            int dataIndex = (currentPage - 1) * 8 + cardIndex;

            if (dataIndex < filteredCafes.size()) {
                CafeData cafe = filteredCafes.get(dataIndex);

                // 🔍 디버깅 로그 추가
                System.out.println("=== 카페 클릭 디버깅 ===");
                System.out.println("클릭한 카드 인덱스: " + cardIndex);
                System.out.println("데이터 인덱스: " + dataIndex);
                System.out.println("전달할 카페 ID: " + cafe.getCafeId());
                System.out.println("카페 이름: " + cafe.getName());
                System.out.println("========================");

                navigateToCafeDetail(cafe.getCafeId());
            }
        }
    }

    /**
     * VBox로부터 카드 인덱스 찾기
     */
    private int getCardIndexFromVBox(VBox cafeCard) {
        VBox[] cafeCards = {cafeCard0, cafeCard1, cafeCard2, cafeCard3, cafeCard4, cafeCard5, cafeCard6, cafeCard7};

        for (int i = 0; i < cafeCards.length; i++) {
            if (cafeCards[i] == cafeCard) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 페이지 이동
     */
    @FXML
    private void goToPage(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        int targetPage = Integer.parseInt(clickedButton.getText());

        if (targetPage != currentPage && targetPage >= 1 && targetPage <= totalPages) {
            currentPage = targetPage;
            displayCurrentPage();
            updatePaginationButtons();
        }
    }

    /**
     * 페이지네이션 버튼 상태 업데이트
     */
    private void updatePaginationButtons() {
        Button[] pageButtons = {page1Button, page2Button, page3Button, page4Button, page5Button};

        for (int i = 0; i < pageButtons.length; i++) {
            int pageNumber = i + 1;

            if (pageNumber <= totalPages) {
                pageButtons[i].setVisible(true);
                pageButtons[i].setManaged(true);
                pageButtons[i].setText(String.valueOf(pageNumber));

                if (pageNumber == currentPage) {
                    // 현재 페이지 스타일
                    pageButtons[i].setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 15; -fx-font-weight: bold;");
                    pageButtons[i].setTextFill(javafx.scene.paint.Color.WHITE);
                } else {
                    // 일반 페이지 스타일
                    pageButtons[i].setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-background-radius: 15; -fx-border-radius: 15;");
                    pageButtons[i].setTextFill(javafx.scene.paint.Color.BLACK);
                }
            } else {
                pageButtons[i].setVisible(false);
                pageButtons[i].setManaged(false);
            }
        }
    }

    /**
     * 카페 데이터 로드 (실제로는 데이터베이스에서)
     */
    private void loadCafesData() {
        // TODO: 실제 데이터베이스에서 카페 데이터 로드
        // 현재는 더미 데이터 사용
        System.out.println("카페 데이터 로드 완료: " + allCafes.size() + "개");
    }

    /**
     * 찜하기 상태 저장
     */
    private void saveFavoriteStatus(int cafeId, boolean isFavorite) {
        try {
            // 🔸 DB에 찜하기 상태 저장
            cafeService.updateFavoriteStatus(cafeId, isFavorite);
            System.out.println("카페 ID " + cafeId + " 찜하기 상태 저장 완료: " + isFavorite);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("찜하기 상태 저장 실패: " + e.getMessage());
        }
    }
    /**
     * CafeDto를 CafeData로 변환
     */
    private CafeData convertToCafeData(CafeDto.SimpleCafeDto dto) { // 타입 변경
        return new CafeData(
                dto.getId(),
                dto.getName(),
                dto.getRating(),
                dto.getReviewCount(),
                dto.getImageUrl(),
                dto.isFavorite()
        );
    }
    /**
     * 카페 상세 페이지로 이동
     */
    private void navigateToCafeDetail(int cafeId) {
//        PerformanceMonitor.measureTime("Navigation - Cafe Detail (ID: " + cafeId + ")", () -> {
            try {
                // DB에서 상세 정보 가져오기
                CafeDto.SimpleCafeDto cafeDetail = cafeService.getCafeById(cafeId);

                if (cafeDetail == null) {
                    System.err.println("Cafe not found with ID: " + cafeId);
                    return;
                }

                Stage currentStage = (Stage) searchField.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/stayd/cafe/cafeDetailView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

                CafeDetailController detailController = fxmlLoader.getController();

                // DB에서 가져온 실제 데이터 전달
                detailController.setCafeData(
                        cafeId,
                        cafeDetail.getName(),
                        cafeDetail.getAddress(),
                        cafeDetail.getOperatingDays(),
                        cafeDetail.getOperatingHours(),
                        cafeDetail.getPricePerHour(),
                        cafeDetail.getPhoneNumber(),
                        cafeDetail.getDescription(),
                        cafeDetail.getImageUrl(),
                        cafeDetail.getRating()
                );

                detailController.setFavorite(cafeDetail.isFavorite());

                currentStage.setTitle("StayD - Cafe Detail");
                currentStage.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error navigating to detail page: " + e.getMessage());
            }
//        });
    }

    /**
     * 현재 정렬 방식 반환
     */
    public boolean isSortByLatest() {
        return isSortByLatest;
    }

    /**
     * 외부에서 검색 키워드 설정
     */
    public void setSearchKeyword(String keyword) {
        searchField.setText(keyword);
        searchCafes(null);
    }

    /**
     * 외부에서 정렬 방식 설정
     */
    public void setSortByLatest(boolean sortByLatest) {
        if (this.isSortByLatest != sortByLatest) {
            toggleSortOrder(null);
        }
    }
}