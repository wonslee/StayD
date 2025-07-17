module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.test to javafx.fxml;
    exports org.example.test;
}