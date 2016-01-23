package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Queue;

public class TableController {
    @FXML
    private TableView TabTable;
    @FXML
    private TreeView DataArea;
    @FXML
    private TextArea RawArea;
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

    public ObservableList<TableModel> modelAll = FXCollections.observableArrayList();
    public ObservableList<TableModel> model1 = FXCollections.observableArrayList();
    public ObservableList<TableModel> model2 = FXCollections.observableArrayList();
    public ObservableList<TableModel> model3 = FXCollections.observableArrayList();

    public TableController() {

    }

    @FXML
    public void MouseEnter(MouseEvent event) {
        ((Label)event.getSource()).setText("0x0A07");
        RawArea.selectRange(54, 61);
    }


    @FXML
    public void MouseLeft(MouseEvent event) {
        ((Label)event.getSource()).setText("i40e_aqc_opc_get_cee_dcb_cfg");
        RawArea.selectRange(0, 0);
    }

    public void UpdateTable(int thing) {
        switch (thing) {
            case 0:
                TableTest.setItems(modelAll);
                break;
            case 1:
                TableTest.setItems(model1);
                break;
            case 2:
                TableTest.setItems(model2);
                break;
            case 3:
                TableTest.setItems(model3);
                break;
            default:
                TableTest.setItems(modelAll);
                break;
        }
    }

    @FXML
    public void initialize(){
        RawArea.setText("ntc 54 head 55.\nAQTX: desc and buffer:\nAQ CMD: opcode 0x0A07, flags 0x3000, datalen 0x0020, retval 0x0000\n\tcookie (h,l) 0x00000000 0x00000000\n" +
                "\tparam (0,1)  0x00000000 0x00000000\n\taddr (h,l)   0x00000000 0xFFFC6000\nAQ CMD Buffer:\n\t0x0000  00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00\n" +
                "\t0x0020  53 00 00 00 00 00 00 00 06 00 00 00 00 00 00 00\nAQTX: Command completed with error 0x2.\nAQTX: desc and buffer writeback:\n" +
                "AQ CMD: opcode 0x0A07, flags 0x3007, datalen 0x0020, retval 0x0002\n\tcookie (h,l) 0x00000000 0x00000000\n\tparam (0,1)  0x00000000 0x00000000\n" +
                "\taddr (h,l)   0x00000000 0xFFFC6000\nAQ CMD Buffer:\n\t0x0000  00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00\n\t0x0020  53 00 00 00 00 00 00 00 06 00 00 00 00 00 00 00");


        modelAll.addAll(
                new TableModel("i40e_aqc_opc_get_cee_dcb_cfg", "I40E_AQ_FLAG_ERR, I40E_AQ_FLAG_VFE", "I40E_AQ_RC_ENOENT", "0"),
                new TableModel("111i40e_aqc_opc_get_cee_dcb_cfg", "111I40E_AQ_FLAG_ERR, 111I40E_AQ_FLAG_VFE", "111I40E_AQ_RC_ENOENT", "1"),
                new TableModel("222i40e_aqc_opc_get_cee_dcb_cfg", "222I40E_AQ_FLAG_ERR, 222I40E_AQ_FLAG_VFE", "222I40E_AQ_RC_ENOENT", "2")
        );
        model1.addAll(
                new TableModel("111i40e_aqc_opc_get_cee_dcb_cfg", "111I40E_AQ_FLAG_ERR, 111I40E_AQ_FLAG_VFE", "111I40E_AQ_RC_ENOENT", "1")
        );
        model2.addAll(
                new TableModel("i40e_aqc_opc_get_cee_dcb_cfg", "I40E_AQ_FLAG_ERR, I40E_AQ_FLAG_VFE", "I40E_AQ_RC_ENOENT", "0"),
                new TableModel("222i40e_aqc_opc_get_cee_dcb_cfg", "222I40E_AQ_FLAG_ERR, 222I40E_AQ_FLAG_VFE", "222I40E_AQ_RC_ENOENT", "2")
        );

        opColumn.setCellValueFactory(cellData -> cellData.getValue().getOpCodeProperty());
        flagColumn.setCellValueFactory(cellData -> cellData.getValue().getFlagsProperty());
        errColumn.setCellValueFactory(cellData -> cellData.getValue().getErrorCodeProperty());
        retColumn.setCellValueFactory(cellData -> cellData.getValue().getReturnCodeProperty());
        //lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        final StyleChangingRowFactory<TableModel> rowFactory = new StyleChangingRowFactory<>("highlightedRow");
        TableTest.setRowFactory(rowFactory);

        TableTest.setItems(modelAll);

        TableTest.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selection Changed!");
            }
        });
    }

}


///////////
// No clue
class StyleChangingRowFactory<T> implements
        Callback<TableView<T>, TableRow<T>> {

    private final String styleClass ;
    private final ObservableList<Integer> styledRowIndices ;
    private final Callback<TableView<T>, TableRow<T>> baseFactory ;

    /**
     * Construct a <code>StyleChangingRowFactory</code>,
     * specifying the name of the style class that will be applied
     * to rows determined by <code>getStyledRowIndices</code>
     * and a base factory to create the <code>TableRow</code>. If <code>baseFactory</code>
     * is <code>null</code>, default table rows will be created.
     * @param styleClass The name of the style class that will be applied to specified rows.
     * @param baseFactory A factory for creating the rows. If null, default
     * <code>TableRow&lt;T&gt;</code>s will be created using the default <code>TableRow</code> constructor.
     */
    public StyleChangingRowFactory(String styleClass, Callback<TableView<T>, TableRow<T>> baseFactory) {
        this.styleClass = styleClass ;
        this.baseFactory = baseFactory ;
        this.styledRowIndices = FXCollections.observableArrayList();
    }

    /**
     * Construct a <code>StyleChangingRowFactory</code>,
     * which applies <code>styleClass</code> to the rows determined by
     * <code>getStyledRowIndices</code>, and using default <code>TableRow</code>s.
     * @param styleClass
     */
    public StyleChangingRowFactory(String styleClass) {
        this(styleClass, null);
    }

    @Override
    public TableRow<T> call(TableView<T> tableView) {

        final TableRow<T> row ;
        if (baseFactory == null) {
            row = new TableRow<>();
        } else {
            row = baseFactory.call(tableView);
        }

        row.indexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs,
                                Number oldValue, Number newValue) {
                updateStyleClass(row);
            }
        });

        styledRowIndices.addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(Change<? extends Integer> change) {
                updateStyleClass(row);
            }
        });

        return row;
    }

    /**
     *
     * @return The list of indices of the rows to which <code>styleClass</code> will be applied.
     * Changes to the content of this list will result in the style class being immediately updated
     * on rows whose indices are either added to or removed from this list.
     */
    public ObservableList<Integer> getStyledRowIndices() {
        return styledRowIndices ;
    }

    private void updateStyleClass(TableRow<T> row) {
        final ObservableList<String> rowStyleClasses = row.getStyleClass();
        if (styledRowIndices.contains(row.getIndex()) ) {
            if (! rowStyleClasses.contains(styleClass)) {
                rowStyleClasses.add(styleClass);
            }
        } else {
            // remove all occurrences of styleClass:
            rowStyleClasses.removeAll(Collections.singletonList(styleClass));
        }
    }

}
