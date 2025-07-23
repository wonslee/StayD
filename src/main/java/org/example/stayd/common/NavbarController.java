package org.example.stayd.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;

import java.io.IOException;

import static org.example.stayd.common.FXUtils.showAlert;
import static org.example.stayd.common.FXUtils.switchScene;

public class NavbarController {
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, SceneConfig.CAFE_Modify_Delete_FXML);  // 카페 수정 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
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
