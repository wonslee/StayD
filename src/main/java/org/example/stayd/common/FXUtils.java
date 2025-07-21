package org.example.stayd.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.stayd.config.SceneConfig;

import java.io.IOException;

public class FXUtils {

    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(FXUtils.class.getResource(fxmlPath));
        Scene scene = new Scene(root, SceneConfig.WIDTH, SceneConfig.HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    /**
     * Alert 공용 유틸
     * @param owner    소유 Window (null 가능)
     * @param type     Alert.AlertType
     * @param title    타이틀
     * @param message  내용
     */
    public static void showAlert(Window owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.showAndWait();
    }
}
