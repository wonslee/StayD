package org.example.stayd.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.stayd.config.SceneConfig;

import java.io.IOException;

public class FXUtils {

    /**
     * 씬을 변경하는 메서드.
     * 지정된 FXML 파일을 로드하고, 새로운 Scene을 설정하여 화면 전환을 처리합니다.
     *
     * @param stage    변경할 Stage (윈도우)
     * @param fxmlPath 변경할 FXML 파일 경로
     * @throws IOException FXML 파일을 로드하는 중 발생할 수 있는 예외
     */
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {

        // FXML 파일을 로드
        Parent root = FXMLLoader.load(FXUtils.class.getResource(fxmlPath));

        // 로드한 FXML을 새로운 Scene에 적용
        Scene scene = new Scene(root, SceneConfig.WIDTH, SceneConfig.HEIGHT);

        // 기존 Stage에 새로운 Scene을 설정하고 창 크기를 고정
        stage.setScene(scene);
        stage.setResizable(false);
    }

    /**
     * 알림창을 공용으로 띄우는 메서드.
     * 주어진 타입과 메시지를 기반으로 알림창을 생성하고, 해당 알림을 화면에 띄웁니다.
     *
     * @param owner    알림창의 소유자 (null일 경우 현재 화면에서 알림을 띄움)
     * @param type     알림창의 타입 (예: 정보, 오류, 경고)
     * @param title    알림창의 제목
     * @param message  알림창에 표시될 내용
     */
    public static void showAlert(Window owner, Alert.AlertType type, String title, String message) {

        // 알림창 객체 생성
        Alert alert = new Alert(type);

        // 알림창의 제목과 내용을 설정
        alert.setTitle(title);
        alert.setHeaderText(null);  // 헤더를 비워서 표시하지 않음
        alert.setContentText(message);  // 내용 설정

        // 만약 owner가 null이 아니라면 해당 owner에 종속된 창에서 알림 표시
        if (owner != null) {
            alert.initOwner(owner);
        }

        // 알림창을 화면에 띄우고 사용자가 확인할 때까지 기다림
        alert.showAndWait();
    }

    public static void navigateToPage(Stage stage, String loginFxml, String message) {
        try {
            switchScene(stage, loginFxml);  // 로그인 페이지로 이동
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(stage, Alert.AlertType.ERROR, "화면 전환 오류", message);
        }
    }

    /**
     * ActionEvent를 처리할 수 있도록 오버로드된 navigateToPage 메서드
     * @param fxmlPath 이동할 FXML 파일 경로
     * @param event    ActionEvent (버튼 클릭 등)
     */
    public static void navigateToPage(String fxmlPath, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, fxmlPath);  // 해당 FXML 파일로 씬 전환
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }

    /**
     * MouseEvent를 처리할 수 있도록 화면 전환을 수행하는 메서드.
     * 마우스 클릭 이벤트 시 해당 페이지로 이동합니다.
     *
     * @param fxmlPath 이동할 FXML 파일 경로
     * @param event    MouseEvent (마우스 클릭 등)
     */
    public static void navigateToPage(String fxmlPath, MouseEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            switchScene(stage, fxmlPath);  // 해당 FXML 파일로 씬 전환
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(), Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
        }
    }

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     * @param event MouseEvent
     */
    @FXML
    public static void handleMouseEnter(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: #4CAF4F; -fx-background-radius: 10;");
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     * @param event MouseEvent
     */
    @FXML
    public static void handleMouseExit(MouseEvent event) {
        Node node = (Node) event.getSource();
        node.setStyle("-fx-background-color: #A8D6AA; -fx-background-radius: 10;");
    }
}