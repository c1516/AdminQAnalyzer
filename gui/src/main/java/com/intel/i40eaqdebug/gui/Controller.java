package com.intel.i40eaqdebug.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private TabPane TabElement;
    @FXML
    private Parent RootPanel;

    public Controller() {
    }

    public void initialize(){

    }

    @FXML
    public void OpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Log File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File theFile = chooser.showOpenDialog(RootPanel.getScene().getWindow());
        if (theFile != null) {
            //TODO: API File Load call.

            try {
                ScrollPane test = FXMLLoader.load(getClass().getResource("/TabBase.fxml"));
                Tab newTab = new Tab(theFile.getName());
                newTab.setContent(test);

                TabElement.getTabs().add(newTab);
            } catch (IOException Ex) {

            }
        }
    }

    @FXML
    public void OpenOptions() {
        System.out.println("Options - NOT IMPLIMENTED");
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }
}
