package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class SingleTabController {
    //region FXML properties.
    @FXML
    private TableView TabTable;
    @FXML
    private TreeView DataArea;
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
    //endregion

    public ObservableList<TableModel> modelAll = FXCollections.observableArrayList();

    public SingleTabController() {

    }

    @FXML
    public void MouseEnter(MouseEvent event) {
        ((Label) event.getSource()).setText("0x0A07");
    }


    @FXML
    public void MouseLeft(MouseEvent event) {
        ((Label) event.getSource()).setText("i40e_aqc_opc_get_cee_dcb_cfg");
    }

    @FXML
    public void initialize() {

        //opColumn.setCellValueFactory(cellData -> cellData.getValue().getOpCodeProperty());
        opColumn.setCellValueFactory(cellData -> cellData.getValue().getOpCodeProperty());
        flagColumn.setCellValueFactory(cellData -> cellData.getValue().getFlagsProperty());
        errColumn.setCellValueFactory(cellData -> cellData.getValue().getErrorCodeProperty());
        retColumn.setCellValueFactory(cellData -> cellData.getValue().getReturnCodeProperty());

        TableTest.setItems(modelAll);

        TableTest.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selection Changed!");
            }
        });
    }

}
