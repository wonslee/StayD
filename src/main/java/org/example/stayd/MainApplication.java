// 작성자 : 방대혁, 이해든, 최영준
package org.example.stayd;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/stayd/user/login.fxml"));
//      FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/stayd/reservation/reservationStatus.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setTitle("stayD");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}