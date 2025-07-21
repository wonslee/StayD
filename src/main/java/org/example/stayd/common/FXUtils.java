package org.example.stayd.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.stayd.config.SceneConfig;

import java.io.IOException;

public class FXUtils {
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(FXUtils.class.getResource(fxmlPath));
        Scene scene = new Scene(root, SceneConfig.WIDTH, SceneConfig.HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
    }
}
