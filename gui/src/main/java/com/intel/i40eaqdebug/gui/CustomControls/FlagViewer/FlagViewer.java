package com.intel.i40eaqdebug.gui.CustomControls.FlagViewer;

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
    public void resize(double width, double height) {
        double newWidth = Math.round(width);//(Math.round((width) / 16) * 16)-4;
        double newHeight = Math.round(height);

        super.resize(width, height);

    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        double newWidth = Math.round(width);//(Math.round((width) / 16) * 16) - 4;
        double newHeight = Math.round(height);

        super.resizeRelocate(x, y, width, height);
    }

    @Override
    public String getUserAgentStylesheet() {
        return FlagViewer.class.getResource("/CSS/ControlStyles/FlagViewer.css").toExternalForm();
    }
}
