package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.Util;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


public class SingleTabController {
    //region FXML properties.
    @FXML
    private TableView<TableModel> TabTable;

    @FXML
    private VBox HideablePane;

    @FXML
    private RowConstraints Row1;
    @FXML
    private RowConstraints Row2;

    //endregion

    private GUIMain Application;
    private Queue<LogEntry> logLines;
    private boolean DetailsVisible = false;

    public SingleTabController(GUIMain App, Queue<LogEntry> logs) {
        Application = App;
        logLines = logs;
    }

    public SingleTabController() {

    }

    public void HideDetails() {
        if (DetailsVisible) {
            Row1.setPercentHeight(100);
            Row2.setPercentHeight(0);
            DetailsVisible = false;
            HideablePane.getChildren().clear();
        }
    }

    public void Search(String term) {
        if (term == null) {
            fillTable();
            return;
        }

        ObservableList<TableModel> tableLines = TabTable.getItems();
        for (int i = 0; i < tableLines.size(); i++) {
            if (!tableLines.get(i).hasPartialValue(term)) {
                tableLines.remove(i);
                i--;
            }
        }
    }

    private boolean clickInPane(double x, double y) {
        double lx = HideablePane.getLayoutX();
        double ly = HideablePane.getLayoutY() + 80;//TODO: this needs to be progmatically corrected to tool-bar size.
        double height = HideablePane.getLayoutBounds().getHeight();
        double width = HideablePane.getLayoutBounds().getWidth();
        System.out.println("X: " + x + ", Y: " + y + " | lx: " + lx + ", ly: " + ly + ", h: " + height + ", w: " + width);


        if ((x >= lx && x <= (lx + width)) && (y >= ly && y <= (ly + height))) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    public void HandleTabClick(MouseEvent event) {

    }

    @FXML
    public void initialize() {
        Application.getMainStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            Object src = event.getSource();
            if (!clickInPane(event.getSceneX(), event.getSceneY())){
                HideDetails();
            }
        });

        //TODO: there's gonna be a better way of handling this instead of this massive lambada.
        TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Row1.setPercentHeight(70);
                Row2.setPercentHeight(30);
                DetailsVisible = true;

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/DetailsPane.fxml"));
                tabFXML.setController(new DetailsPaneController(Application, (TableModel)newSelection));
                GridPane testPane = null;
                try {
                    testPane = tabFXML.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HideablePane.getChildren().add(testPane);
                HideablePane.requestFocus();
                HideablePane.setVisible(true);
            }
        });
        fillTable();
        //new Thread(new DelaySearch(this)).start();
    }


    private void fillTable() {

        ObservableList<TableModel> data = TabTable.getItems();
        Queue<LogEntry> test = new LinkedList<LogEntry>(logLines);

        while (test.size() > 0) {
            LogEntry temp = test.remove();
            //TODO: this needs to be different and must fetch the strings from the mappings in the API
            String OpCode = APIEntryPoint.getCommandName((int)temp.getOpCode());
            String Error = APIEntryPoint.getErrorString(temp.getErr());

            //TODO: this needs to be broken up and we need info on this.
            String Flags = "0x" + Integer.toHexString(temp.getFlags()).toUpperCase();

            data.add(new TableModel(OpCode, Flags,  Error, Short.toString(temp.getRetVal())));
        }

    }
/*
    public class DelaySearch implements Runnable {

        private SingleTabController test;
        public void run() {
            System.out.println("cats");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("dogs");

            test.Search(Integer.toString(0x0A00));
        }

        DelaySearch(SingleTabController test) {
            this.test = test;
        }

    }
*/
}
