package com.intel.i40eaqdebug.gui.customcontrols.flagviewer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Control;

public class FlagViewer extends Control {
    public final IntegerProperty flagProperty() {
        if (flag == null) {
            flag = new SimpleIntegerProperty(0);
        }
        return flag;
    }
    private IntegerProperty flag;
    public final void setFlag(int value) {
        flagProperty().setValue(value);
        if (getSkin() != null) {
            ((FlagViewerSkin) getSkin()).Update();
        }
    }
    public final int getFlag() { return flag == null ? -1 : flag.getValue(); }

    public FlagViewer() {
        getStyleClass().add("flagviewer-control");

        setMinWidth(0);
        setMinHeight(0);
    }

    @Override
    public String getUserAgentStylesheet() {
        return FlagViewer.class.getResource("/CSS/ControlStyles/FlagViewer.css").toExternalForm();
    }
}
