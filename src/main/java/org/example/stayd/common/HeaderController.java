// 작성자 : 방대혁
package org.example.stayd.common;

import static org.example.stayd.common.FXUtils.navigateToPage;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;
import org.example.stayd.domain.user.service.UserService;

/**
 * 헤더에 위치한 버튼 클릭 시 해당 페이지로 전환을 담당하는 컨트롤러 클래스입니다. 주로 홈 페이지, 마이 페이지, 로그인 페이지로 이동하는 기능을 제공합니다.
 */
public class HeaderController {

    // 유저 관련 서비스 인스턴스
    private final UserService userService = new UserService();

    /**
     * 홈 페이지로 이동하는 메서드
     *
     * @param event MouseEvent
     */
    @FXML
    private void goToHome(MouseEvent event) {
        // 홈 페이지로 전환
        navigateToPage(SceneConfig.HOME_FXML, event);  // 홈 페이지로 이동
    }

    /**
     * 유저 프로필 클릭 시 동작하는 메서드 로그인 여부에 따라 마이 페이지 또는 로그인 페이지로 이동
     *
     * @param event MouseEvent
     */
    @FXML
    private void goToUserPage(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  // 현재 윈도우 (stage) 가져오기

        // 로그인 여부 확인
        if (userService.isUserLoggedIn()) {
            // 로그인 되어있으면 마이페이지로 이동
            navigateToPage(SceneConfig.MY_PAGE_FXML, event);  // 마이페이지로 이동
        } else {
            // 로그인 되어있지 않으면 로그인 페이지로 이동
            navigateToPage(SceneConfig.LOGIN_FXML, event);  // 로그인 페이지로 이동
        }
    }
}