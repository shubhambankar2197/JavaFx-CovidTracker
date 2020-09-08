module covidTracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.json;
    requires okhttp3;
    requires okio;
    requires kotlin.stdlib;
    opens covidTracker to javafx.fxml;
    exports covidTracker;

}