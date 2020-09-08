package covidTracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("covidTrackerUI.fxml"));
        primaryStage.setTitle("JavaFx-CovidTracker(India)");
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
