module org.example.stayd {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires static lombok;
    requires jakarta.validation;
    requires jbcrypt;
    requires java.mail;
    requires jakarta.el; // ← 여기에 추가!!!
    requires org.hibernate.validator; // 이미 있었겠죠
    requires com.fasterxml.classmate; // ← 추가 (혹은 requires classmate;)

    opens org.example.stayd.common to javafx.fxml;  // ← 이 줄 추가!
    opens org.example.stayd.domain.user.controller to javafx.fxml;
    opens org.example.stayd.domain.cafe.controller to javafx.fxml;
    opens org.example.stayd.domain.reservation.controller to javafx.fxml;
    opens org.example.stayd.domain.review.controller to javafx.fxml;
    opens org.example.stayd.domain.user.dto to org.hibernate.validator;
    opens org.example.stayd.domain.reservation.model to org.hibernate.validator;

    exports org.example.stayd;


}