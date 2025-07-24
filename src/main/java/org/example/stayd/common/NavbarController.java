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
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.cafe.controller.CafeModifyDeleteController;
import org.example.stayd.domain.cafe.service.CafeService;

import java.io.IOException;

import static org.example.stayd.common.FXUtils.*;

/**
 * 네브바(Navigation Bar)에서 각 버튼을 클릭했을 때 화면 전환을 처리하는 컨트롤러 클래스입니다.
 * 각 버튼에 해당하는 화면으로 전환하는 기능을 제공합니다.
 */
public class NavbarController {

    /**
     * 카페 생성 버튼 클릭 시 호출되는 메서드
     * @param event ActionEvent
     */
    private CafeService cafeService = new CafeService();
    // 카페 생성 버튼 클릭 시
    @FXML
    private void goToCafeCreate(ActionEvent event) {
        navigateToPage(SceneConfig.CAFE_CREATE_FXML, event); // 카페 생성 화면으로 이동
    }

    /**
     * 카페 수정 버튼 클릭 시 호출되는 메서드
     * @param event ActionEvent
     */
    @FXML
    private void goToCafeModifyDelete(ActionEvent event) {
        try {
            // 1. 먼저 권한 및 카페 존재 여부 확인
            Long ownerId = (long) SessionManager.getInstance().getLoggedInUser().getUser_id();
            Long cafeId = cafeService.getLatestCafeIdByOwnerId(ownerId);

            if (cafeId == null) {
                showAlert(((Node) event.getSource()).getScene().getWindow(),
                    Alert.AlertType.WARNING, "카페 없음", "등록된 카페가 없습니다. 먼저 카페를 생성해주세요.");
                return;
            }

            // 2. FXML 로더 생성 및 컨트롤러 설정
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneConfig.CAFE_Modify_Delete_FXML));
            Parent root = loader.load();

            // 3. 컨트롤러 가져와서 카페 ID 설정 (화면 이동 전에!)
            CafeModifyDeleteController controller = loader.getController();
            controller.setCafeId(cafeId);

            // 4. 마지막에 화면 전환
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(((Node) event.getSource()).getScene().getWindow(),
                Alert.AlertType.ERROR, "화면 전환 오류", "오류가 발생했습니다.");
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

    /**
     * 날짜별 현황 버튼 클릭 시 호출되는 메서드
     * @param event ActionEvent
     */
    @FXML
    private void goToReservationStatus(ActionEvent event) {
        navigateToPage(SceneConfig.RESERVATION_STATUS_FXML, event); // 날짜별 예약 현황 화면으로 이동
    }

    /**
     * 요일별 현황 버튼 클릭 시 호출되는 메서드
     * @param event ActionEvent
     */
    @FXML
    private void goToReservationStatusDay(ActionEvent event) {
        navigateToPage(SceneConfig.RESERVATION_STATUS_DAY_FXML, event); // 요일별 예약 현황 화면으로 이동
    }

    /**
     * 할인 설정 버튼 클릭 시 호출되는 메서드
     * @param event ActionEvent
     */
    @FXML
    private void goToDiscountSettings(ActionEvent event) {
        navigateToPage(SceneConfig.DISCOUNT_SETTINGS_FXML, event); // 할인 설정 화면으로 이동
    }

    /**
     * 마우스가 버튼 위에 올라갔을 때 색상 변경
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        FXUtils.handleMouseEnter(event); // FXUtils에서 처리
    }

    /**
     * 마우스가 버튼을 벗어났을 때 원래 색상으로 복원
     * @param event MouseEvent
     */
    @FXML
    private void handleMouseExit(MouseEvent event) {
        FXUtils.handleMouseExit(event); // FXUtils에서 처리
    }
}