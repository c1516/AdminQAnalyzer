package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.util.Queue;

public class TableController {
    @FXML
    private TableView<TableModel> TableTest;
    @FXML
    private TableColumn<TableModel, String> opColumn;
    @FXML
    private TableColumn<TableModel, String> flagColumn;
    @FXML
    private TableColumn<TableModel, String> errColumn;
    @FXML
    private TableColumn<TableModel, String> retColumn;

    public ObservableList<TableModel> testModelThing = FXCollections.observableArrayList();


    public TableController() {
    }

    @FXML
    public void initialize(){

        testModelThing.addAll(
                new TableModel("i40e_aqc_opc_get_cee_dcb_cfg", "I40E_AQ_FLAG_ERR, I40E_AQ_FLAG_VFE", "I40E_AQ_RC_ENOENT", "0"),
                new TableModel("111i40e_aqc_opc_get_cee_dcb_cfg", "111I40E_AQ_FLAG_ERR, 111I40E_AQ_FLAG_VFE", "111I40E_AQ_RC_ENOENT", "1"),
                new TableModel("222i40e_aqc_opc_get_cee_dcb_cfg", "222I40E_AQ_FLAG_ERR, 222I40E_AQ_FLAG_VFE", "222I40E_AQ_RC_ENOENT", "2")
        );

        opColumn.setCellValueFactory(cellData -> cellData.getValue().getOpCodeProperty());
        flagColumn.setCellValueFactory(cellData -> cellData.getValue().getFlagsProperty());
        errColumn.setCellValueFactory(cellData -> cellData.getValue().getErrorCodeProperty());
        retColumn.setCellValueFactory(cellData -> cellData.getValue().getReturnCodeProperty());
        //lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        TableTest.setItems(testModelThing);

        TableTest.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("");
            }
        });
    }

}
