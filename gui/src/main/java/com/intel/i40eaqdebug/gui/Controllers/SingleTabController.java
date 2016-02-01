package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.Util;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
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
        fillTable(term);
    }

    //This function checks to see if a click happens inside or outside the details pane
    //Used to decide weather to hide it or now.
    private boolean clickInPane(double x, double y) {
        double lx = HideablePane.getLayoutX();
        //TODO: The 80 pixel offset needs to be computed dinamically. (this corrects for the toolbar height).
        double ly = HideablePane.getLayoutY() + 80;
        double height = HideablePane.getLayoutBounds().getHeight();
        double width = HideablePane.getLayoutBounds().getWidth();

        if ((x >= lx && x <= (lx + width)) && (y >= ly && y <= (ly + height))) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    public void initialize() {
        //This sets up an listener on the main scene, watchig for mouse clicks.
        //Used to hide the details pane when the user clicks off of it.
        Application.getMainStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            Object src = event.getSource();
            if (!clickInPane(event.getSceneX(), event.getSceneY())){
                HideDetails();
            }
        });

        //This sets up an event listener on the tables selector. IE: if the current selection changes, we run this code.
        //There is probably a better way to declare this as a separate function and pass it in instead, hence the todo.
        //TODO: there's gonna be a better way of handling this instead of this massive lambada.
        TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Row1.setPercentHeight(70);
                Row2.setPercentHeight(30);
                DetailsVisible = true;

                //Retrieve the row number of the selected entry, retrieve that log entry and pass it to the detail window
                int selectedRow = TabTable.getSelectionModel().getSelectedIndex();
                LogEntry tempLogEntry = (LogEntry) ((LinkedList) logLines).get(selectedRow);

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/DetailsPane.fxml"));
                tabFXML.setController(new DetailsPaneController(Application, tempLogEntry));
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
        fillTable(null);


        //These are CSS pseudo classes. We more or less load these from our CSS file
        //The CSS file itself is currently loaded in GUIMain.
        PseudoClass error = PseudoClass.getPseudoClass("error");
        PseudoClass success = PseudoClass.getPseudoClass("success");

        //This over-rides the table view rowfactory with our custom lambada.
        //The idea is, for every row in the table, we set up a listener on a specific property
        //of the object that's being displayed. Then, based on that value, we change the styles of the row
        //Or other things (if we had a text box in our rows, we could disable it for instance).
        TabTable.setRowFactory(tableView -> {
            //First, we create a table row. This is ultimately what we will be inserting into the controll
            TableRow<TableModel> row = new TableRow<>();

            //This simply sets up a listener on the item property of a row
            //AKA: When the row gets an item (or the item changes) we run this code.
            row.itemProperty().addListener((obs, oldItem, currentItem) -> {
                //If the currentItem doesn't exist (ie: nothing in the row)
                //we clear the row style by setting both CSS pseudo-classes to false.
                if (currentItem == null ) {
                    row.pseudoClassStateChanged(error, false);
                    row.pseudoClassStateChanged(success, false);
                    return;
                };

                //currentRow.getErrorCodeProperty().removeListener(changeListener);
                //currentRow.getErrorCodeProperty().addListener(changeListener);

                //Then based on the error code from our table model (or anything really) we
                //set CSS psuedo-classes to appropriately style our table row.
                //NOTE: all normal CSS rules apply. In other words, if your psuedo-class is
                //physically higher in the file then another one that applies styles to the same element,
                //your style wont be seen.
                if (currentItem.getErrorCode().equals("")) {
                    row.pseudoClassStateChanged(error, false);
                    row.pseudoClassStateChanged(success, true);
                } else if (currentItem.getErrorCode().equals("Bad Thing")) {
                    row.pseudoClassStateChanged(error, true);
                    row.pseudoClassStateChanged(success, false);
                }
            });

            /*Since our rows will never change during execution (only loaded once) we don't need
              to set up a listener for property changes. However I will leave this here for posterity.
            ChangeListener<String> changeListener = (obs, oldRow, currentRow) -> {
            };*/
            return row;
        });
    }


    //This function fills the TableView with models for items
    //If there is a filter string, it will only include itesm that match that string.
    private void fillTable(String Filter) {
        ObservableList<TableModel> data = TabTable.getItems();
        data.clear();

        Queue<LogEntry> test = new LinkedList<LogEntry>(logLines);

        while (test.size() > 0) {
            LogEntry temp = test.remove();
            String OpCode = APIEntryPoint.getCommandName((int)temp.getOpCode());
            String Error = APIEntryPoint.getErrorString(temp.getErr());

            //TODO: At some point we'll probably want to get the actual flag names from API (assuming it's implemented then)
            String Flags = "0x" + Integer.toHexString(temp.getFlags()).toUpperCase();

            TableModel tempModel = new TableModel(OpCode, Flags,  Error, Short.toString(temp.getRetVal()));
            if (Filter == null || (Filter != null && tempModel.hasPartialValue(Filter)))
                data.add(tempModel);
        }

    }
}
