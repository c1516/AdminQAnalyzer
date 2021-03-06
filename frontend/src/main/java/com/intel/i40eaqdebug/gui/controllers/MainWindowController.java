package com.intel.i40eaqdebug.gui.controllers;


import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MainWindowController {
    @FXML private TabPane TabElement;
    @FXML private ToolBar SearchBar;
    @FXML private Button ClearButton;
    @FXML private TextField SearchField;
    @FXML private StackPane LoadingScreen;
    @FXML private Parent RootPanel;
    @FXML private Label EventTotalText;
    @FXML private ToggleButton ErrorFilter, SuccessFilter;

    private List<SingleTabController> controllers = new LinkedList<SingleTabController>();
    private ArrayList<String> searchTerms = new ArrayList<String>();
    private ArrayList<Integer> tabFilters = new ArrayList<>();
    private GUIMain Application;
    private final int MaxTextLength = 255;
    private String rootFolder = null;

    public MainWindowController(GUIMain app) {
        Application = app;
    }

    public MainWindowController() {
    }

    @FXML public void initialize() {
        //Disable search bar when we start
        SearchBar.setDisable(true);
        //When we switch tabs, load whatever it is we where searching for in that one (this will alos eventually work with filtering)
        TabElement.getSelectionModel().selectedIndexProperty().addListener((obj, prev, next) -> {
            int index = (int) next;

            //Restore filter/search state
            if (tabFilters.size() > 0) {
                String filter = "I40E_AQ_RC_OK";
                Boolean match = Boolean.TRUE;

                int filterState = tabFilters.get(index);
                //Filter for errors
                if (filterState == 1) {
                    SuccessFilter.setSelected(false);
                    SuccessFilter.setStyle("-fx-background-color: limegreen;");
                    ErrorFilter.setSelected(true);
                    ErrorFilter.setStyle("-fx-background-color: darkred;");

                    match = Boolean.FALSE;
                }
                //Filter for success
                else if (filterState == 2) {
                    SuccessFilter.setSelected(true);
                    SuccessFilter.setStyle("-fx-background-color: green;");
                    ErrorFilter.setSelected(false);
                    ErrorFilter.setStyle("-fx-background-color: crimson;");
                }

                //No filters
                else {
                    SuccessFilter.setSelected(false);
                    SuccessFilter.setStyle("-fx-background-color: limegreen;");
                    ErrorFilter.setSelected(false);
                    ErrorFilter.setStyle("-fx-background-color: crimson;");
                    filter = "";
                }

                //Restore search / filters
                controllers.get(index).Search(searchTerms.get(index), filter, match);
                SearchField.setText(searchTerms.get(index));
                UpdateTotal(controllers.get(index).getEventTotal());
            }
        });

        SearchField.textProperty().addListener((obs, oldv, newv) -> {
            String text = SearchField.getText();
            if (text.length() > MaxTextLength)
                SearchField.setText(text.substring(0, MaxTextLength));
        });
    }


    @FXML public void clearFilters() {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();
        if (TabElement.getTabs().size() > 0) {
            SuccessFilter.setSelected(false);
            SuccessFilter.setStyle("-fx-background-color: limegreen;");

            ErrorFilter.setSelected(false);
            ErrorFilter.setStyle("-fx-background-color: crimson;");
            controllers.get(selectedTab).Search(SearchField.getText(), "", true);
        }
        tabFilters.set(selectedTab, 0);
    }


    @FXML public void errorFilterClicked() {
        errorFilterClicked(Boolean.FALSE);
    }


    @FXML public void errorFilterClicked(Boolean restore) {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

        String term = SearchField.getText();
        ClearButton.setVisible(term.length() > 0);

        //If toggle is set
        if (((TabElement.getTabs().size() > 0) && ErrorFilter.isSelected()) || restore) {
            SuccessFilter.setSelected(false);
            SuccessFilter.setStyle("-fx-background-color: limegreen;");
            ErrorFilter.setSelected(true);
            ErrorFilter.setStyle("-fx-background-color: darkred;");

            String filter = "I40E_AQ_RC_OK";
            ClearButton.setVisible(filter.length() > 0);
            tabFilters.set(selectedTab, 1);

            controllers.get(selectedTab).Search(term, filter, false);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }

        //If toggle is unset
        else
            clearFilters();
    }


    @FXML public void successFilterClicked() {
        successFilterClicked(Boolean.FALSE);
    }


    @FXML public void successFilterClicked(Boolean restore) {
        int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

        String term = SearchField.getText();
        ClearButton.setVisible(term.length() > 0);

        //If toggle is set
        if (((TabElement.getTabs().size() > 0) && SuccessFilter.isSelected()) || restore) {
            ErrorFilter.setSelected(false);
            ErrorFilter.setStyle("-fx-background-color: crimson;");
            SuccessFilter.setSelected(true);
            SuccessFilter.setStyle("-fx-background-color: green;");

            String filter = "I40E_AQ_RC_OK";
            ClearButton.setVisible(filter.length() > 0);
            tabFilters.set(selectedTab, 2);

            controllers.get(selectedTab).Search(term, filter, true);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }

        //If toggle is unset
        else
            clearFilters();
    }


    @FXML public void OpenFileUI() throws IOException {
        FileChooser chooser = new FileChooser();
        if (rootFolder == null)
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            chooser.setInitialDirectory(new File(rootFolder));

        chooser.setTitle("Select Log File");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Log Files", "*.log"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));
        File theFile = chooser.showOpenDialog(RootPanel.getScene().getWindow());

        OpenFile(theFile);
    }

    private void OpenFile(File theFile) throws IOException {
        if (theFile != null) {
            rootFolder = theFile.getParent();

            LogRetriver runner = new LogRetriver();
            runner.theFile = theFile;
            runner.LoadingScreen = LoadingScreen;

            LoadingScreen.setVisible(true);

            new Thread(runner).start();
        }
    }

    @FXML public void OpenOptions() {
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

    @FXML public void SearchKeyReleased() {
        if (TabElement.getTabs().size() > 0) {
            String term = SearchField.getText();
            ClearButton.setVisible(term.length() > 0);
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            String filter = "";
            if(SuccessFilter.isSelected() || ErrorFilter.isSelected())
                filter = "I40E_AQ_RC_OK";

            controllers.get(selectedTab).Search(term, filter, SuccessFilter.isSelected());
            searchTerms.set(selectedTab, term);
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }
    }

    private void UpdateTotal(int eventTotal) {
        EventTotalText.setText("Event Total: " + eventTotal);
    }

    @FXML public void Clear() {
        if (TabElement.getTabs().size() > 0) {
            ClearButton.setVisible(false);

            SearchField.setText("");
            int selectedTab = TabElement.getSelectionModel().getSelectedIndex();

            controllers.get(selectedTab).Search("", "", true);
            searchTerms.set(selectedTab, "");
            UpdateTotal(controllers.get(selectedTab).getEventTotal());
        }
    }

    @FXML public void Exit() {
        Platform.exit();
    }

    //TODO: add background progress indicator
    @FXML
    public void fileDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        LinkedList<String> failed = new LinkedList<>();
        if (db.hasFiles()) {
            success = true;
            String filePath = null;
            for (File file: db.getFiles()) {
                try {
                    OpenFile(file);
                } catch (Exception E) {
                    failed.add(file.getName());
                }
            }
        }

        if (failed.size() != 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : failed)
                builder.append(s).append('\n');

            DialogController.CreateDialog("Failed to open files", builder.toString(), true);
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    public void fileDragDetected(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        } else {
            event.consume();
        }
    }


    class LogRetriver implements Runnable {
        public File theFile = null;
        public StackPane LoadingScreen = null;

        @Override public void run() {
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

                            if (TabElement.getTabs().size() == 1) {
                                UpdateTotal(0);
                                SearchBar.setDisable(true);
                            }
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


}

