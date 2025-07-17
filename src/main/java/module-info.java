module org.example.stayd {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.stayd to javafx.fxml;
    exports org.example.stayd;
}