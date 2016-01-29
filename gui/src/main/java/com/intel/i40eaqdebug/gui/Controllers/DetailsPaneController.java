package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.FakeAPIInitilizer;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
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
    private TableView<TableModel> TabTable;
    //endregion
    private GUIMain Application;
    private TableModel Selection;
    private Queue<LogEntry> logLines;
    public DetailsPaneController(GUIMain App, TableModel selectedRow) {
        Application = App;
        Selection = selectedRow;
    }

    public DetailsPaneController() {

    }

    @FXML
    public void initialize() {
        GridPane grid = new GridPane();
/* your code */
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(50);
        column2.setPercentWidth(50);
        grid.getColumnConstraints().add(column1);
//TODO: there's gonna be a better way of handling this instead of this massive lambada.
        /*TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Stage tempStage = new Stage();
                tempStage.initModality(Modality.APPLICATION_MODAL);
                tempStage.initOwner(Application.getMainStage());

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/DetailsPane.fxml"));
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
        });*/
        //fillTable();


    }
    /*private void fillTable() {


        ObservableList<TableModel> data = TabTable.getItems();
        Queue<LogEntry> test = new LinkedList<LogEntry>(logLines);

        while (test.size() > 0) {
            LogEntry temp = test.remove();
            //TODO: this needs to be different and must fetch the strings from the mappings in the API
            String name = FakeAPIInitilizer.FakeCommandStruct.
            //String value = APIEntryPoint.getErrorString(temp.getErr());

            //TODO: this needs to be broken up and we need info on this.
            String Flags = "0x" + Integer.toHexString(temp.getFlags()).toUpperCase();

            data.add(new TableModel(OpCode, Flags);
        }

    }*/

}
