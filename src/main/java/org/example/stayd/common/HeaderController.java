package org.example.stayd.common;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HeaderController {

    @FXML
    private ImageView notificationIcon;
    @FXML
    private ImageView userIcon;

    public void initialize() {
        // 이미지를 경로로 설정 (절대 경로 또는 상대 경로 사용)
        notificationIcon.setImage(new Image(getClass().getResource("/org/example/stayd/common/images/icon_notification.png").toExternalForm()));
        userIcon.setImage(new Image(getClass().getResource("/org/example/stayd/common/images/icon_user_profile.png").toExternalForm()));
    }
}