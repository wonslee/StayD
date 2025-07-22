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
import org.example.stayd.domain.cafe.service.CafeService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CafeListController implements Initializable {

    // 검색 관련
    @FXML private TextField searchField;
    @FXML private Button searchButton;
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
    private int currentPage = 1;
    private int totalPages = 5;
    private String currentSearchKeyword = "";
    private List<CafeData> allCafes = new ArrayList<>();
    private List<CafeData> filteredCafes = new ArrayList<>();

    // 카페 데이터 클래스
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

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
        public int getReviewCount() { return reviewCount; }
        public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public boolean isFavorite() { return isFavorite; }
        public void setFavorite(boolean favorite) { isFavorite = favorite; }
        public int getCafeId() { return cafeId; }
        public void setCafeId(int cafeId) { this.cafeId = cafeId; }
    }

    private CafeService cafeService;

    // 🔹 기본 생성자 (FXML용 - 필수!)
    public CafeListController() {
        this.cafeService = new CafeService();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 더미 데이터 초기화
        initializeDummyData();

        // 초기 데이터 로드
        loadCafesData();

        // 첫 페이지 표시
        displayCurrentPage();

        // 페이지네이션 버튼 초기화
        updatePaginationButtons();
    }

    /**
     * 더미 데이터 초기화
     */
    private void initializeDummyData() {
        allCafes.clear();

        // 더미 카페 데이터 생성 (40개 - 5페이지 분량)
        String[] cafeNames = {
                "작심 스터디카페 해치점", "조용한 공부방", "집중 스터디룸", "편안한 카페",
                "북카페 스터디", "24시간 스터디", "깔끔한 독서실", "모던 스터디카페"
        };

        for (int i = 1; i <= 40; i++) {
            String cafeName = cafeNames[(i-1) % cafeNames.length] + " " + i;
            allCafes.add(new CafeData(
                    i,
                    cafeName,
                    4.0 + (Math.random() * 1.0), // 4.0 ~ 5.0 사이 랜덤 평점
                    (int)(50 + Math.random() * 200), // 50 ~ 250 사이 랜덤 리뷰 수
                    "https://pimg.daara.co.kr/kidd/photo/2018/01/09/1515480765_11.jpg",
                    false
            ));
        }

        filteredCafes = new ArrayList<>(allCafes);
        totalPages = (int) Math.ceil(filteredCafes.size() / 8.0);
    }

    /**
     * 검색창에서 Enter 키 처리
     */
    @FXML
    private void handleSearchKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchCafes(null);
        }
    }

    /**
     * 카페 검색
     */
    @FXML
    private void searchCafes(ActionEvent event) {
        currentSearchKeyword = searchField.getText().trim();
        currentPage = 1; // 검색 시 첫 페이지로 이동

        // 검색 필터링
        if (currentSearchKeyword.isEmpty()) {
            filteredCafes = new ArrayList<>(allCafes);
        } else {
            filteredCafes.clear();
            for (CafeData cafe : allCafes) {
                if (cafe.getName().toLowerCase().contains(currentSearchKeyword.toLowerCase())) {
                    filteredCafes.add(cafe);
                }
            }
        }

        // 정렬 적용
        applySorting();

        // 총 페이지 수 업데이트
        totalPages = Math.max(1, (int) Math.ceil(filteredCafes.size() / 8.0));

        // 결과 표시
        displayCurrentPage();
        updatePaginationButtons();
    }

    /**
     * 정렬 순서 토글 (최신순 ↔ 평점순)
     */
    @FXML
    private void toggleSortOrder(ActionEvent event) {
        isSortByLatest = !isSortByLatest;

        if (isSortByLatest) {
            sortButton.setText("최신순 ▼");
        } else {
            sortButton.setText("평점순 ▼");
        }

        // 정렬 적용
        applySorting();

        // 현재 페이지 다시 표시
        displayCurrentPage();
    }

    /**
     * 정렬 적용
     */
    private void applySorting() {
        if (isSortByLatest) {
            // 최신순 정렬 (ID 역순)
            filteredCafes.sort((a, b) -> Integer.compare(b.getCafeId(), a.getCafeId()));
        } else {
            // 평점순 정렬 (평점 높은 순, 같으면 리뷰 수 많은 순)
            filteredCafes.sort((a, b) -> {
                int ratingCompare = Double.compare(b.getRating(), a.getRating());
                if (ratingCompare == 0) {
                    return Integer.compare(b.getReviewCount(), a.getReviewCount());
                }
                return ratingCompare;
            });
        }
    }

    /**
     * 현재 페이지의 카페들을 화면에 표시
     */
    private void displayCurrentPage() {
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

                // 카페 정보 표시
                cafeCards[i].setVisible(true);
                cafeCards[i].setManaged(true);
                cafeNames[i].setText(cafe.getName());
                cafeRatings[i].setText(String.format("%.1f", cafe.getRating()));
                cafeReviewCounts[i].setText("(" + cafe.getReviewCount() + ")");

                // 이미지 로드
                try {
                    cafeImages[i].setImage(new Image(cafe.getImageUrl()));
                } catch (Exception e) {
                    // 기본 이미지 설정
                    System.out.println("이미지 로드 실패: " + e.getMessage());
                }

                // 찜하기 버튼 상태 설정
                updateFavoriteButton(favoriteBtns[i], cafe.isFavorite());

            } else {
                // 데이터가 없는 카드는 숨김
                cafeCards[i].setVisible(false);
                cafeCards[i].setManaged(false);
            }
        }
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
        // TODO: 데이터베이스에 찜하기 상태 저장
        System.out.println("카페 ID " + cafeId + " 찜하기 상태: " + isFavorite);
    }

    /**
     * 카페 상세 페이지로 이동
     */
    private void navigateToCafeDetail(int cafeId) {
        try {
            // 현재 Stage 가져오기
            Stage currentStage = (Stage) searchField.getScene().getWindow();

            // 상세 페이지 FXML 로드
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/stayd/cafe/cafeDetailView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

            // Controller 가져와서 카페 데이터 설정
            CafeDetailController detailController = fxmlLoader.getController();

            // 선택된 카페 데이터 찾기
            CafeData selectedCafe = null;
            for (CafeData cafe : filteredCafes) {
                if (cafe.getCafeId() == cafeId) {
                    selectedCafe = cafe;
                    break;
                }
            }

            if (selectedCafe != null) {
                // 카페 데이터를 상세 페이지에 전달
                detailController.setCafeData(
                        selectedCafe.getName(),
                        "서울특별시 종로구 창경궁로 254", // 위치 (실제로는 DB에서)
                        "월, 화, 수, 목, 금", // 영업일 (실제로는 DB에서)
                        "09:00 - 18:00", // 운영시간 (실제로는 DB에서)
                        10000, // 시간당 가격 (실제로는 DB에서)
                        "02-123-4567", // 전화번호 (실제로는 DB에서)
                        "조용하고 깨끗한 환경에서 집중해서 공부할 수 있는 스터디 카페입니다.", // 설명 (실제로는 DB에서)
                        selectedCafe.getImageUrl(),
                        selectedCafe.getRating()
                );

                // 찜하기 상태 설정
                detailController.setFavorite(selectedCafe.isFavorite());
            }

            // 화면 전환
            currentStage.setTitle("StayD - 카페 상세");
            currentStage.setScene(scene);

            System.out.println("카페 ID " + cafeId + " 상세 페이지로 이동 완료");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("상세 페이지 이동 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 현재 검색 키워드 반환
     */
    public String getCurrentSearchKeyword() {
        return currentSearchKeyword;
    }

    /**
     * 현재 정렬 방식 반환
     */
    public boolean isSortByLatest() {
        return isSortByLatest;
    }

    /**
     * 현재 페이지 반환
     */
    public int getCurrentPage() {
        return currentPage;
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