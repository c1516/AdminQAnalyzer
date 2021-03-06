package com.intel.i40eaqdebug.gui.customcontrols.opcodecell;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.gui.datamodels.TableModel;
import javafx.scene.control.TableCell;

/**
 * Created by andrey on 2/11/2016.
 */
public class OpCodeCell extends TableCell<TableModel, Integer> {
    @Override protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            String hex = Integer.toHexString(item).toUpperCase();
            String format = "0x%1$4" + "s";
            String HexOpCode = String.format(format, hex).replace(' ', '0');

            setText(HexOpCode + " - " + APIEntryPoint.getCommandName(item));
        } else
            setText(null);
    }
}
