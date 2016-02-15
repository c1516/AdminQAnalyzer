package com.intel.i40eaqdebug.gui.customcontrols.timestampcell;

import com.intel.i40eaqdebug.api.header.TimeStamp;
import com.intel.i40eaqdebug.gui.datamodels.TableModel;
import javafx.scene.control.TableCell;

/**
 * Created by andrey on 2/11/2016.
 */
public class TimeStampCell extends TableCell<TableModel, TimeStamp> {

    public TimeStampCell(){}

    @Override
    protected void updateItem(TimeStamp item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText(item.toString());
        } else
            setText("");
    }

}

