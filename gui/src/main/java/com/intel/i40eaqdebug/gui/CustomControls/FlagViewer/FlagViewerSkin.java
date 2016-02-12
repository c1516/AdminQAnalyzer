package com.intel.i40eaqdebug.gui.CustomControls.FlagViewer;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Created by andrey on 2/11/2016.
 */
public class FlagViewerSkin extends SkinBase {
    private ArrayList<Rectangle> bitFlags = new ArrayList<Rectangle>();
    private ArrayList<String> flagNames = new ArrayList<String>();
    private HBox mainBox = new HBox();
    private double Width;
    private double Height;
    //TODO: flags and colors need to be configurable?
    //TODO: put colors in css?

    Paint reservedColor = Paint.valueOf("#A8A8A8");
    Paint normalColor = Paint.valueOf("#0044FF");
    Paint normalColorDis = Paint.valueOf("#80B3FF");
    Paint errorColor = Paint.valueOf("#FF0000");
    Paint errorColorDis = Paint.valueOf("#FF8080");
    Insets margin = new Insets(0, 0, 0, 1);

    private boolean Initialized = false;


    public FlagViewerSkin(Control control) {
        super(control);

        flagNames.add("I40E_AQ_FLAG_DD");
        flagNames.add("I40E_AQ_FLAG_CMP");
        flagNames.add("I40E_AQ_FLAG_ERR");
        flagNames.add("I40E_AQ_FLAG_VFE");
        flagNames.add("RESERVED");
        flagNames.add("RESERVED");
        flagNames.add("RESERVED");
        flagNames.add("RESERVED");
        flagNames.add("RESERVED");
        flagNames.add("I40E_AQ_FLAG_LB");
        flagNames.add("I40E_AQ_FLAG_RD");
        flagNames.add("I40E_AQ_FLAG_VFC");
        flagNames.add("I40E_AQ_FLAG_BUF");
        flagNames.add("I40E_AQ_FLAG_SI");
        flagNames.add("I40E_AQ_FLAG_EI");
        flagNames.add("I40E_AQ_FLAG_FE");

        //mainBox.setMouseTransparent(true);
        mainBox.setPadding(new Insets(2,2,2,2));
    }

    public void Update() {
        if (mainBox.getChildren().size() == 0) return;
        short flag = (short)((FlagViewer)getSkinnable()).getFlag();

        Width = (getSkinnable().getWidth() - 4 - 16) / 16;
        Height = (getSkinnable().getHeight()) - 4;

        for (short i = 0; i < 16; i++) {
            Rectangle bit = (Rectangle)mainBox.getChildren().get(i);
            bit.setWidth(Width);
            bit.setHeight(Height);
            Boolean isBitSet = ((1 << (15 - i)) & flag) != 0;
            if (i == 2)
                bit.setFill(isBitSet ? errorColor : errorColorDis);
            else if (i < 4 || i > 8)
                bit.setFill(isBitSet ? normalColor : normalColorDis);
        }

    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        if (!Initialized) {
            for (int i = 0; i < 16; i++) {
                Rectangle bit = new Rectangle(0, 0, reservedColor);
                //bit.onMouseClickedProperty().addListener((obs, oldV, newV) -> { });
                bitFlags.add(bit);

                Tooltip temp = new Tooltip(flagNames.get(i));
                Tooltip.install(bit, temp);

                mainBox.getChildren().add(bit);
                if (i != 0)
                    mainBox.setMargin(bit, margin);
            }


            getChildren().add(mainBox);
            Initialized = true;
        }
        Update();

    }
}
