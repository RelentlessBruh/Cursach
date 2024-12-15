module project.signature {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jdk.jdi;
    requires org.apache.logging.log4j;


    opens project.signature to javafx.fxml;
    exports project.signature;
}