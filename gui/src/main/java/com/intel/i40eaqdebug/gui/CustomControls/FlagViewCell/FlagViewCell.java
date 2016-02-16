package com.intel.i40eaqdebug.gui.CustomControls.FlagViewCell;

import com.intel.i40eaqdebug.gui.CustomControls.FlagViewer.FlagViewer;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import javafx.scene.control.TableCell;

/**
 * Created by andrey on 2/11/2016.
 */
public class FlagViewCell extends TableCell<TableModel, Integer> {
    public FlagViewer flagViewer = new FlagViewer();

    public FlagViewCell() {
        //flagViewer.setFlag(getItem());
    }

    @Override protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            flagViewer.setFlag(empty ? 0 : item.shortValue());
            setGraphic(flagViewer);
        } else
            setGraphic(null);
    }
}
