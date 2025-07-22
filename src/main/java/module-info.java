module org.example.stayd {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires static lombok;
    requires jakarta.validation;
    requires jbcrypt;
    requires java.mail;

    opens org.example.stayd.domain.user.controller to javafx.fxml;
    opens org.example.stayd.domain.cafe.controller to javafx.fxml;
    opens org.example.stayd.domain.reservation.controller to javafx.fxml;
    opens org.example.stayd.domain.review.controller to javafx.fxml;

    exports org.example.stayd;
}