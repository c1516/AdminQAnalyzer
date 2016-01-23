package com.intel.i40eaqdebug.gui.Controllers;


import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Controller {
    @FXML
    private TabPane TabElement;
    @FXML
    private Parent RootPanel;

    private List<TableController> controllers = new LinkedList<TableController>();


    public Controller() {
    }

    @FXML
    public void initialize(){

    }

    @FXML
    public void filterClicked(MouseEvent event) {
        int selected = TabElement.getSelectionModel().getSelectedIndex();
        Button temp = ((Button)event.getSource());
        if (!temp.getStyle().contains("gray")) {
            temp.setStyle("-fx-background-color: gray");

            if (temp.getId().equals("ErrorFilter")){
                controllers.get(selected).UpdateTable(1);
            } else if (temp.getId().equals("SuccessFilter")){
                controllers.get(selected).UpdateTable(2);
            } else if (temp.getId().equals("SometingFilter")){
                controllers.get(selected).UpdateTable(3);
            } else if (temp.getId().equals("Something2Filter")){
                controllers.get(selected).UpdateTable(4);
            }
        } else {
            controllers.get(selected).UpdateTable(0);

            if (temp.getId().equals("ErrorFilter")){
                temp.setStyle("-fx-background-color: red");
            } else if (temp.getId().equals("SuccessFilter")){
                temp.setStyle("-fx-background-color: limegreen");
            } else if (temp.getId().equals("SometingFilter")){
                temp.setStyle("-fx-background-color: cyan");
            } else if (temp.getId().equals("Something2Filter")){
                temp.setStyle("-fx-background-color: yellow");
            }
        }
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
                FXMLLoader test = new FXMLLoader(getClass().getResource("/TabBase.fxml"));
                TableController temp = new TableController();
                controllers.add(temp);
                test.setController(temp);
                Tab newTab = new Tab(theFile.getName());
                newTab.setContent(test.load());

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

