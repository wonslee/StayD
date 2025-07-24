package org.example.stayd.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;

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
        navigateToPage(SceneConfig.CAFE_Modify_Delete_FXML, event); // 카페 수정/삭제 화면으로 이동
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
}