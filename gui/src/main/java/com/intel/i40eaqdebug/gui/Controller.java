package com.intel.i40eaqdebug.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    @FXML
    public TabPane Tab1;

    @FXML
    public Button OpenFile;

    @FXML
    public Parent rootPane;

    @FXML
    public void OpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Log File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

    }
}