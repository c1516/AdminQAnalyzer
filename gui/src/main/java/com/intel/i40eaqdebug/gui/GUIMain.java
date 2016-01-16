package com.intel.i40eaqdebug.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Entry point to starting the GUI: API Calls should be called via the APIEntryPoint static methods
 */

public class GUIMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(getClass().getResource("/sample.fxml"));

        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setResizable(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
