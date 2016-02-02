package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;
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
import java.util.*;


public class DetailsPaneController {
    //region FXML properties.
    @FXML
    public AnchorPane Pane;
    @FXML
    public TableView<DetailTableModel> DetailTable = new TableView<>();
    //endregion
    private GUIMain Application;
    private LogEntry LogLine;



    public DetailsPaneController(GUIMain App, LogEntry selectedRow) {
        Application = App;
        LogLine = selectedRow;
    }

    public DetailsPaneController() {

    }

    /*
    So the idea here is pretty simple. We get one log entry from the
    calling window, and display it's contetns (other then the stuff already
    displayed in the table) + use it's opcode to fetch it's command sturct and also display that.
     */
    @FXML
    public void initialize() {
        ObservableList<DetailTableModel> rows =  DetailTable.getItems();
        String lineNumber = Integer.toString(LogLine.getStartLine());
        String cookieHigh = Integer.toString(LogLine.getCookieHigh());
        String cookieLow = Integer.toString(LogLine.getCookieLow());
        //TODO: add some sort of link to, or display of, the raw command byte file.

        rows.add(new DetailTableModel("Line Number", lineNumber));
        rows.add(new DetailTableModel("Cookie High", cookieHigh));
        rows.add(new DetailTableModel("Cookie Low", cookieLow));

        CommandStruct tempStruct = APIEntryPoint.getCommandStruct((int)LogLine.getOpCode());
        LinkedHashMap<String, CommandField> structContents = tempStruct.getFields();

        for (Map.Entry<String, CommandField> entry : structContents.entrySet()) {
            DetailTableModel tempModel = new DetailTableModel(entry.getKey(), entry.getValue().getValueAsString(LogLine.getBuffer()));
            rows.add(tempModel);
        }
        System.out.println(rows.size());
    }

}

