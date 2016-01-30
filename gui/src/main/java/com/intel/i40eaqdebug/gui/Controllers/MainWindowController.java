package com.intel.i40eaqdebug.gui.Controllers;


import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.FakeAPIInitilizer;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainWindowController {
    @FXML
    private TabPane TabElement;
    @FXML
    private ToolBar SearchBar;
    @FXML
    private Parent RootPanel;

    private List<SingleTabController> controllers = new LinkedList<SingleTabController>();
    private GUIMain Application;

    public MainWindowController(GUIMain app) {
        Application = app;
    }

    public MainWindowController() { }

    @FXML
    public void initialize(){

    }

    @FXML
    public void filterClicked(MouseEvent event) {

    }


    @FXML
    public void OpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.setTitle("Select Log File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File theFile = chooser.showOpenDialog(RootPanel.getScene().getWindow());


        if (theFile != null) {
            Queue<LogEntry> data = LoadData(theFile);

            try {
                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/TabBase.fxml"));

                SingleTabController newTabController = new SingleTabController(Application, data);
                controllers.add(newTabController);

                tabFXML.setController(newTabController);
                Tab newTab = new Tab(theFile.getName());
                newTab.setContent(tabFXML.load());

                TabElement.getTabs().add(newTab);
            } catch (IOException Ex) {
                DialogController.CreateDialog("An error occured!", Ex.getMessage() + "\n" + Ex.getStackTrace().toString(), true);
                //Platform.exit();
            }


        }
    }

    private Queue<LogEntry> LoadData(File filePath) {
        FakeAPIInitilizer.InitApi();
        return APIEntryPoint.getCommandLogQueue(filePath);
    }

    @FXML
    public void OpenOptions() {
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }


}

