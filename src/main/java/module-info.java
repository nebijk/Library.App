module com.example.SQLbibliotek {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.Library.Application to javafx.base;
    opens com.Library.Application.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports com.Library.Application;
}