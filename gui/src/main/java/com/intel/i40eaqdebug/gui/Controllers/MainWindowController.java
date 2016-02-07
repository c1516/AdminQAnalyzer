package com.intel.i40eaqdebug.gui.Controllers;


import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainWindowController {
    @FXML
    private TabPane TabElement;
    @FXML
    private ToolBar SearchBar;
    @FXML
    private TextField SearchField;
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
        return APIEntryPoint.getCommandLogQueue(filePath, 0, Integer.MAX_VALUE);
    }

    @FXML
    public void OpenOptions() {

//        TODO Give this some dummy options and hook them up to the filters or other corresponding parameters
        Stage tempStage = new Stage();
        tempStage.initModality(Modality.APPLICATION_MODAL);
        tempStage.initOwner(Application.getMainStage());

        FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/OptionsWindow.fxml"));
        Scene tempScene = null;
        try {
            tempScene = new Scene(tabFXML.load(), 400, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempStage.setScene(tempScene);
        tempStage.show();

    }

    public void Search() {

        String term = SearchField.getText();

        try {
//            TODO pick the current tab once this information is available (instead of the first one)
            controllers.get(0).Search(term);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public void Clear() {

        SearchField.setText(null);

        try {
            controllers.get(0).Search("");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }


}

