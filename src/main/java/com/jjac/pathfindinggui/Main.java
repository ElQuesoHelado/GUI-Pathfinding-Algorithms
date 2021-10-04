package com.jjac.pathfindinggui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private ShowcaseController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/jjac/pathfindinggui/ShowcaseView.fxml"));
        Parent root = loader.load();

        controller = (ShowcaseController) loader.getController();

        primaryStage.setTitle("PathfindingGUI");
        primaryStage.setScene(new Scene(root, 800, 700));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        controller.terminate();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
