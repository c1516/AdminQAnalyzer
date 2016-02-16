package com.intel.i40eaqdebug.gui.CustomControls.FlagViewer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Control;

public class FlagViewer extends Control {
    private IntegerProperty flag;

    public FlagViewer() {
        getStyleClass().add("flagviewer-control");

        setMinWidth(0);
        setMinHeight(0);
    }

    public final IntegerProperty flagProperty() {
        if (flag == null) {
            flag = new SimpleIntegerProperty(0);
        }
        return flag;
    }

    public final int getFlag() {
        return flag == null ? -1 : flag.getValue();
    }

    public final void setFlag(int value) {
        flagProperty().setValue(value);
        if (getSkin() != null) {
            ((FlagViewerSkin) getSkin()).Update();
        }
    }

    @Override public String getUserAgentStylesheet() {
        return FlagViewer.class.getResource("/CSS/ControlStyles/FlagViewer.css").toExternalForm();
    }
}
