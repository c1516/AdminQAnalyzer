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

import java.io.IOException;
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
    private TableModel SelectedRowDetail;
    private LogEntry logLines;

    public DetailsPaneController(GUIMain App, TableModel selectedRow) {
        Application = App;
        SelectedRowDetail = selectedRow;
    }

    public DetailsPaneController() {

    }

    @FXML
    public void initialize() {

        ObservableList<DetailTableModel> rows = DetailTable.getItems();

        rows.add(new DetailTableModel(SelectedRowDetail.getOpCode()));
    }

}

