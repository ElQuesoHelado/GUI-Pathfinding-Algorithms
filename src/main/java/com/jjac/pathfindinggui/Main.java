package com.jjac.pathfindinggui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/ShowcaseView.fxml"));
        loader.setLocation(getClass().getResource("/com/jjac/pathfindinggui/ShowcaseView.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Showcase");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}