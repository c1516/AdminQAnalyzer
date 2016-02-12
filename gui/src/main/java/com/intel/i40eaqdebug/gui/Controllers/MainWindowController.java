package com.intel.i40eaqdebug.gui.Controllers;


import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Button ClearButton;
    @FXML
    private TextField SearchField;
    @FXML
    private StackPane LoadingScreen;
    @FXML
    private Parent RootPanel;
    @FXML
    private Label EventTotalText;

    private List<SingleTabController> controllers = new LinkedList<SingleTabController>();
    private ArrayList<String> searchTerms = new ArrayList<String>();
    private GUIMain Application;

    public MainWindowController(GUIMain app) {
        Application = app;
    }

    public MainWindowController() { }

    @FXML
    public void initialize(){
        //Disable search bar when we start
        SearchBar.setDisable(true);
        //When we switch tabs, load whatever it is we where searching for in that one (this will alos eventually work with filtering)
        TabElement.getSelectionModel().selectedIndexProperty().addListener((obj, prev, next) -> {
            int index = (int)next;
            if (controllers.size() > 0) {
                controllers.get(index).Search(searchTerms.get(index), true);
                SearchField.setText(searchTerms.get(index));
                UpdateTotal(controllers.get(index).getEventTotal());
            }
        });
    }


    @FXML
    public void filterClicked(MouseEvent event) {
        if (TabElement.getTabs().size() > 0) {
            String term = SearchField.getText();
            ClearButton.setVisible(term.length() > 0);
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search(term, true);
            searchTerms.set(selectedTab, term);
        }
    }


    @FXML
    public void errorFilterClicked(MouseEvent event) {
        if (TabElement.getTabs().size() > 0) {
            String term = "I40E_AQ_RC_OK";
            ClearButton.setVisible(term.length() > 0);
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search(term, false);
            searchTerms.set(selectedTab, term);
        }
    }


    @FXML
    public void successFilterClicked(MouseEvent event) {
        if (TabElement.getTabs().size() > 0) {
            String term = "I40E_AQ_RC_OK";
            ClearButton.setVisible(term.length() > 0);
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search(term, true);
            searchTerms.set(selectedTab, term);
        }
    }


    @FXML
    public void OpenFile() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.setTitle("Select Log File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File theFile = chooser.showOpenDialog(RootPanel.getScene().getWindow());

        LogRetriver runner = new LogRetriver();
        runner.theFile = theFile;
        runner.LoadingScreen = LoadingScreen;

        LoadingScreen.setVisible(true);

        Platform.runLater(runner);
    }


    class LogRetriver implements Runnable {
        public File theFile = null;
        public StackPane LoadingScreen = null;

        @Override
        public void run() {
            if (theFile != null) {
                Queue<LogEntry> data = APIEntryPoint.getCommandLogQueue(theFile, 0, Integer.MAX_VALUE);

                try {
                    FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/TabBase.fxml"));

                    SingleTabController newTabController = new SingleTabController(Application, data);
                    controllers.add(newTabController);

                    tabFXML.setController(newTabController);
                    Tab newTab = new Tab(theFile.getName());
                    newTab.setContent(tabFXML.load());

                    //If we close a tab, we want to remove it from controlelr list, search list
                    //And disable the bar if it's the last one
                    newTab.setOnCloseRequest((event) -> {
                        int index = TabElement.getTabs().indexOf((Tab)event.getSource());
                        controllers.remove(index);
                        searchTerms.remove(index);

                        if (TabElement.getTabs().size() == 0)
                            SearchBar.setDisable(true);
                    });
                    //add a blank space to save search terms in for our new tab
                    searchTerms.add("");
                    TabElement.getTabs().add(newTab);
                    SearchBar.setDisable(false);

                    int selectedTab = TabElement.getSelectionModel().getSelectedIndex();
                    UpdateTotal(controllers.get(selectedTab).getEventTotal());

                } catch (IOException Ex) {
                    DialogController.CreateDialog("An error occured!", Ex.getMessage() + "\n" + Ex.getStackTrace().toString(), true);
                    //throw Ex;
                    //Platform.exit();
                }
            }
            LoadingScreen.setVisible(false);
        }
    }

    @FXML
    public void OpenOptions() {
        //TODO: Give this some dummy options and hook them up to the filters or other corresponding parameters
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

    @FXML
    public void SearchKeyReleased() {
        if (TabElement.getTabs().size() > 0) {
            String term = SearchField.getText();
            ClearButton.setVisible(term.length() > 0);
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search(term, true);
            searchTerms.set(selectedTab, term);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }
    }

    private void UpdateTotal(int eventTotal) {
        EventTotalText.setText("Event Total: " + eventTotal);
    }

    @FXML
    public void Clear() {
        if (TabElement.getTabs().size() > 0) {
            ClearButton.setVisible(false);

            SearchField.setText("");
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search("", true);
            searchTerms.set(selectedTab, "");
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }
    }

    @FXML
    public void Exit() {
        Platform.exit();
    }


}

