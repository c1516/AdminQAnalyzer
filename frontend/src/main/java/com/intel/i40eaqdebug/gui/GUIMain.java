package com.intel.i40eaqdebug.gui;

import com.intel.i40eaqdebug.gui.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point to starting the GUI: API Calls should be called via the APIEntryPoint static methods
 */

public class GUIMain extends Application {

    private Stage MainStage;

    public static void main(String... args) {
        launch(args);
    }

    public Stage getMainStage() {
        return MainStage;
    }

    @Override public void start(Stage primaryStage) throws Exception {
        MainStage = primaryStage;

        FXMLLoader root = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        root.setController(new MainWindowController(this));
        primaryStage.setTitle("AdminQ Viewer");
        primaryStage.setScene(new Scene((Parent) root.load(), 1024, 768));
        primaryStage.setMaximized(false);
        primaryStage.setResizable(true);
        //primaryStage.getIcons().add(new Image(GUIMain.class.getResourceAsStream("icon.ico")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        //How to load External css files.
        //We should load all of them here.
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/PrimaryStyle.css").toExternalForm());

        primaryStage.show();
    }
}
