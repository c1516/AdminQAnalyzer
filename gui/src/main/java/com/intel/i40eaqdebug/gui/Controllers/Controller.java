package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.util.Queue;

public class Controller {
    @FXML
    private TabPane TabElement;
    @FXML
    private Parent RootPanel;
    @FXML
    private TableView TabTable;
    @FXML
    private TreeView DataArea;
    @FXML
    private TextArea RawArea;

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
            //Queue<LogEntry> logs = APIEntryPoint.getCommandLogQueue(theFile);

            try {
                GridPane test = FXMLLoader.load(getClass().getResource("/TabBase.fxml"));
                Tab newTab = new Tab(theFile.getName());
                newTab.setContent(test);

                TabElement.getTabs().add(newTab);
            } catch (IOException Ex) {
                DialogController.CreateDialog("An error occured!", "Some program files got corrupted!", true);
                Platform.exit();
            }


            //newTab.

            /*for (LogEntry entry : logs) {

            }*/


        }
    }

    @FXML
    public void OpenOptions() {
        DialogController.CreateDialog("An error occured!", "Some program files got corrupted!", true);

        System.out.println("Options - NOT IMPLIMENTED");
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }
}
