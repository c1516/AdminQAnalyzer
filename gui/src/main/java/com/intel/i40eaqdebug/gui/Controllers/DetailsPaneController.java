package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.DetailTableModel;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.FakeAPIInitilizer;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;


public class DetailsPaneController {
    //region FXML properties.
    @FXML
    public AnchorPane Pane;
    @FXML
    TableView<DetailTableModel> DetailTable = new TableView<>();
    //endregion
    private GUIMain Application;
    private DetailTableModel SelectedRowDetail;
    private LogEntry logLines;

    public DetailsPaneController(GUIMain App, LogEntry selectedRow) {
        Application = App;

        //Create data model from incoming log entry
        String OpCode = String.valueOf(selectedRow.getOpCode());
        String Flags = String.valueOf(selectedRow.getFlags());
        String Error = String.valueOf(selectedRow.getErr());
        String ReturnVal = String.valueOf(selectedRow.getRetVal());
        String CookieHigh = String.valueOf(selectedRow.getCookieHigh());
        String CookieLow = String.valueOf(selectedRow.getCookieLow());

        SelectedRowDetail = new DetailTableModel(OpCode, Flags, Error, ReturnVal, CookieHigh, CookieLow);
    }

    public DetailsPaneController() {

    }

    @FXML
    public void initialize() {

        ObservableList<DetailTableModel> rows = DetailTable.getItems();

        rows.add(SelectedRowDetail);
    }

}

