package com.intel.i40eaqdebug.gui.CustomControls.CheckboxCell;

import com.intel.i40eaqdebug.gui.CustomControls.FlagViewer.FlagViewer;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

/**
 * Created by andrey on 2/11/2016.
 */
public class CheckboxCell extends TableCell<TableModel, Boolean> {
    public CheckBox check = new CheckBox();

    public CheckboxCell(){
        check.setDisable(true);
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            check.setSelected(item);
            setGraphic(check);
        } else
            setGraphic(null);
    }
}
