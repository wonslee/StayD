module org.example.stayd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.stayd to javafx.fxml;
    exports org.example.stayd;
}