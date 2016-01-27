package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


public class SingleTabController {
    //region FXML properties.
    @FXML
    private TableView<TableModel> TabTable;
    //endregion

    private GUIMain Application;

    private Queue<LogEntry> logLines;

    public SingleTabController(GUIMain App, Queue<LogEntry> logs) {
        Application = App;
        logLines = logs;
    }

    public SingleTabController() {

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

    @FXML
    public void MouseEnter(MouseEvent event) {
    }


    @FXML
    public void MouseLeft(MouseEvent event) {
    }

    @FXML
    public void initialize() {

        //TODO: there's gonna be a better way of handling this instead of this massive lambada.
        TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Stage tempStage = new Stage();
                tempStage.initModality(Modality.APPLICATION_MODAL);
                tempStage.initOwner(Application.getMainStage());

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/TabBase.fxml"));
                tabFXML.setController(new DetailsPaneController(Application, (TableModel)newSelection));
                //TODO: determine good size.
                Scene tempScene = null;
                try {
                    tempScene = new Scene(tabFXML.load(), 300, 200);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tempStage.setScene(tempScene);
                tempStage.show();
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
            //TODO: this needs to be diffrent and must fetch the strings from the mappings in the API
            data.add(new TableModel(Short.toString(temp.getOpCode()), Short.toString(temp.getFlags()),
                    Byte.toString(temp.getErr()), Short.toString(temp.getRetVal())));
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
