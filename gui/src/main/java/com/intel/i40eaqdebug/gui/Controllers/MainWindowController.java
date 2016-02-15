package com.intel.i40eaqdebug.gui.Controllers;


import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.GUIMain;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    @FXML
    private ToggleButton ErrorFilter, SuccessFilter;

    private List<SingleTabController> controllers = new LinkedList<SingleTabController>();
    private ArrayList<String> searchTerms = new ArrayList<String>();
    private ArrayList<Integer> tabFilters = new ArrayList<>();
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

            //Restore filter/search state
            if (tabFilters.size() > 0) {
                int filterState = tabFilters.get(index);
                if (filterState == 1) {
                    errorFilterClicked(true);
                    SearchField.setText("");
                }
                else if (filterState == 2) {
                    successFilterClicked(true);
                    SearchField.setText("");
                }
                //Restore search state
                else {
                    if (controllers.size() > 0) {
                        SuccessFilter.setSelected(false);
                        SuccessFilter.setStyle("-fx-background-color: limegreen;");
                        ErrorFilter.setSelected(false);
                        ErrorFilter.setStyle("-fx-background-color: crimson;");

                        controllers.get(index).Search(searchTerms.get(index), true);
                        SearchField.setText(searchTerms.get(index));
                        UpdateTotal(controllers.get(index).getEventTotal());
                    }
                }
            }
        });
    }


    @FXML
    public void clearFilters() {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();
        if (TabElement.getTabs().size() > 0) {
            SuccessFilter.setSelected(false);
            SuccessFilter.setStyle("-fx-background-color: limegreen;");

            ErrorFilter.setSelected(false);
            ErrorFilter.setStyle("-fx-background-color: crimson;");
            Clear();
        }
        tabFilters.set(selectedTab, 0);
    }


    @FXML
    public void errorFilterClicked() {
        errorFilterClicked(Boolean.FALSE);
    }


    @FXML
    public void errorFilterClicked(Boolean restore) {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

        //If toggle is set
        if (((TabElement.getTabs().size() > 0) && ErrorFilter.isSelected()) || restore) {
            SuccessFilter.setSelected(false);
            SuccessFilter.setStyle("-fx-background-color: limegreen;");
            ErrorFilter.setSelected(true);

            ErrorFilter.setStyle("-fx-background-color: darkred;");
            String term = "I40E_AQ_RC_OK";
            ClearButton.setVisible(term.length() > 0);
            tabFilters.set(selectedTab, 1);

            controllers.get(selectedTab).Search(term, false);
            searchTerms.set(selectedTab, term);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }

        //If toggle is unset
        else
            clearFilters();
    }


    @FXML
    public void successFilterClicked() {
        successFilterClicked(Boolean.FALSE);
    }


    @FXML
    public void successFilterClicked(Boolean restore) {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

        //If toggle is set
        if (((TabElement.getTabs().size() > 0) && SuccessFilter.isSelected()) || restore) {
            ErrorFilter.setSelected(false);
            ErrorFilter.setStyle("-fx-background-color: crimson;");
            SuccessFilter.setSelected(true);
            SuccessFilter.setStyle("-fx-background-color: green;");

            String term = "I40E_AQ_RC_OK";
            ClearButton.setVisible(term.length() > 0);
            tabFilters.set(selectedTab, 2);

            controllers.get(selectedTab).Search(term, true);
            searchTerms.set(selectedTab, term);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }

        //If toggle is unset
        else
            clearFilters();
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

        new Thread(runner).start();
    }


    class LogRetriver implements Runnable {
        public File theFile = null;
        public StackPane LoadingScreen = null;

        @Override
        public void run() {
            if (theFile != null) {
                Queue<LogEntry> data = APIEntryPoint.getCommandLogQueue(theFile, 0, Integer.MAX_VALUE);

                Platform.runLater(() -> {
                    try {
                        FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/TabBase.fxml"));

                        SingleTabController newTabController = new SingleTabController(Application, data);
                        controllers.add(newTabController);

                        tabFXML.setController(newTabController);
                        Tab newTab = new Tab(theFile.getName());
                        newTab.setContent(tabFXML.load());

                        //If we close a tab, we want to remove it from centraler list, search list
                        //And disable the bar if it's the last one
                        newTab.setOnCloseRequest((event) -> {
                            int index = TabElement.getTabs().indexOf((Tab) event.getSource());
                            controllers.remove(index);
                            searchTerms.remove(index);
                            tabFilters.remove(index);

                            if (TabElement.getTabs().size() == 1)
                                SearchBar.setDisable(true);
                        });
                        //add a blank space to save search terms in for our new tab
                        searchTerms.add("");
                        tabFilters.add(0);
                        TabElement.getTabs().add(newTab);
                        SearchBar.setDisable(false);

                        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();
                        UpdateTotal(controllers.get(selectedTab).getEventTotal());

                    } catch (IOException Ex) {
                        StringWriter writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        Ex.printStackTrace(printWriter);
                        printWriter.flush();

                        String stackTrace = writer.toString();
                        DialogController.CreateDialog("An error occured!", Ex.getMessage() + "\n" + stackTrace, true);
                        //throw Ex;
                        //Platform.exit();
                    }
                });
            }
            Platform.runLater(() -> {
                LoadingScreen.setVisible(false);

                //Switch to newly opened tab
                int newTabIndex = TabElement.getTabs().size() - 1;
                TabElement.getSelectionModel().select(newTabIndex);
            });
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

