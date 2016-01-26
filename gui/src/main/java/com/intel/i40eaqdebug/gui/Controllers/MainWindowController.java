package com.intel.i40eaqdebug.gui.Controllers;


import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {
    @FXML
    private TabPane TabElement;
    @FXML
    private ToolBar SearchBar;
    @FXML
    private Parent RootPanel;

    private List<SingleTabController> controllers = new LinkedList<SingleTabController>();


    public MainWindowController() {
    }

    @FXML
    public void initialize(){

    }

    @FXML
    public void filterClicked(MouseEvent event) {

    }


    @FXML
    public void OpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        chooser.setTitle("Select Log File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File theFile = chooser.showOpenDialog(RootPanel.getScene().getWindow());
        if (theFile != null) {
            //TODO: Call API functions here, and load data.
            //Queue<LogEntry> logs = APIEntryPoint.getCommandLogQueue(theFile);

            try {
                FXMLLoader test = new FXMLLoader(getClass().getResource("/TabBase.fxml"));
                SingleTabController temp = new SingleTabController();
                controllers.add(temp);
                test.setController(temp);
                Tab newTab = new Tab(theFile.getName());
                newTab.setContent(test.load());

                TabElement.getTabs().add(newTab);
            } catch (IOException Ex) {
                DialogController.CreateDialog("An error occured!", Ex.getMessage() + "\n" + Ex.getStackTrace(), true);
                //Platform.exit();
            }

            LoadFakeData();
        }
    }

    private void LoadFakeData() {

    }

    @FXML
    public void OpenOptions() {
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }
}

