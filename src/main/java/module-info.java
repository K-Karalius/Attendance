module com.karalius.attendance {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires itextpdf;

    opens com.karalius.attendance to javafx.fxml;
    exports com.karalius.attendance;
}