module com.example.solab5 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.solab5 to javafx.fxml;
    exports com.example.solab5;
}