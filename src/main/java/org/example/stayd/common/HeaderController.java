package org.example.stayd.common;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.service.UserService;

import java.io.IOException;

import static org.example.stayd.common.FXUtils.showAlert;
import static org.example.stayd.common.FXUtils.switchScene;

public class HeaderController {

    private final UserService userService = new UserService();

    @FXML
    private void goToHome(MouseEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXUtils.switchScene(stage, SceneConfig.HOME_FXML);    // 홈 페이지로 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "홈 화면으로 이동하는 중 오류가 발생했습니다.");
        }
    }

    // 유저 프로필 클릭 시 동작
    @FXML
    private void goToUserPage(MouseEvent event) {
        // 로그인 상태 확인
        if (userService.isUserLoggedIn()) {
            // 로그인 되어있으면 마이페이지로 이동
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchScene(stage, SceneConfig.MY_PAGE_FXML);  // 마이페이지로 이동
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "마이페이지로 이동하는 중 오류가 발생했습니다.");
            }
        } else {
            // 로그인 되어있지 않으면 로그인 페이지로 이동
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                switchScene(stage, SceneConfig.LOGIN_FXML);  // 로그인 페이지로 이동
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "로그인 화면으로 이동하는 중 오류가 발생했습니다.");
            }
        }
    }
}
