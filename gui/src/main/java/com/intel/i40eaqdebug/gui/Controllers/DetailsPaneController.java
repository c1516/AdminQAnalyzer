package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class DetailsPaneController {
    //region FXML properties.
    @FXML
    public AnchorPane Pane;
    //endregion

    private GUIMain Application;
    private TableModel Selection;

    public DetailsPaneController(GUIMain App, TableModel selectedRow) {
        Application = App;
        Selection = selectedRow;
    }

    public DetailsPaneController() {

    }

    @FXML
    public void initialize() {
    }
}
