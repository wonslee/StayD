package org.example.stayd.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.cafe.controller.CafeModifyDeleteController;
import org.example.stayd.domain.cafe.service.CafeService;

import java.io.IOException;

import static org.example.stayd.common.FXUtils.showAlert;
import static org.example.stayd.common.FXUtils.switchScene;

public class NavbarController {
    private CafeService cafeService = new CafeService();
    // 카페 생성 버튼 클릭 시
    @FXML
    private void goToCafeCreate(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, SceneConfig.CAFE_CREATE_FXML);  // 카페 생성 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }

    // 카페 수정 버튼 클릭 시
    @FXML
    private void goToCafeModifyDelete(ActionEvent event) {
        try {
            // 현재 로그인한 사용자의 ID 가져오기
            Long ownerId = (long) SessionManager.getInstance().getLoggedInUser().getUser_id();

            // 가장 최신에 생성된 카페 ID 조회
            Long cafeId = cafeService.getLatestCafeIdByOwnerId(ownerId);

            if (cafeId == null) {
                showAlert(((Node) event.getSource()).getScene().getWindow(),
                        Alert.AlertType.WARNING, "카페 없음", "등록된 카페가 없습니다. 먼저 카페를 생성해주세요.");
                return;
            }

            // FXML 로더 생성
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneConfig.CAFE_Modify_Delete_FXML));
            Parent root = loader.load();

            // 컨트롤러 가져와서 카페 ID 설정
            CafeModifyDeleteController controller = loader.getController();
            controller.setCafeId(cafeId);

            // 화면 전환
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();

            // InvocationTargetException의 실제 원인 출력
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                System.out.println("실제 예외 원인: " + cause.getClass().getName());
                System.out.println("실제 예외 메시지: " + cause.getMessage());
                cause.printStackTrace();
                showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "오류",
                        "카페 정보를 불러오는 중 오류가 발생했습니다: " + cause.getMessage());
            } else {
                showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "오류",
                        "카페 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    // 날짜별 현황 버튼 클릭 시
    @FXML
    private void goToReservationStatus(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, SceneConfig.RESERVATION_STATUS_FXML);  // 날짜별 예약 현황 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }

    // 요일별 현황 버튼 클릭 시
    @FXML
    private void goToReservationStatusDay(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, SceneConfig.RESERVATION_STATUS_DAY_FXML);  // 요일별 예약 현황 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }

    // 할인 설정 버튼 클릭 시
    @FXML
    private void goToDiscountSettings(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, SceneConfig.DISCOUNT_SETTINGS_FXML);  // 할인 설정 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }
}
