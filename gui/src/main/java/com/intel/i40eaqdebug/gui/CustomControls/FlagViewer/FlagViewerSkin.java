package com.intel.i40eaqdebug.gui.CustomControls.FlagViewer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.lang.reflect.Field;
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

        //mainBox;
        mainBox.setPadding(new Insets(2,2,2,2));
    }

    public void Update() {
        if (mainBox.getChildren().size() == 0) return;
        short flag = (short)((FlagViewer)getSkinnable()).getFlag();

        Width = ((getSkinnable().getWidth() - 4) / 16) - 2;
        Height = (getSkinnable().getHeight()) - 4;


        for (short i = 0; i < 16; i++) {
            Rectangle bit = (Rectangle)mainBox.getChildren().get(i);
            bit.setWidth(Width);
            bit.setHeight(Height);
            Boolean isBitSet = ((1 << (15 - i)) & flag) != 0;
            if (i < 4 || i > 8)
                bit.setFill(isBitSet ? normalColor : normalColorDis);
        }

    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        if (!Initialized) {
            for (int i = 0; i < 16; i++) {
                Rectangle bit = new Rectangle(0, 0, reservedColor);
                bitFlags.add(bit);

                Tooltip temp = new Tooltip(flagNames.get(i));
                hackTooltipStartTiming(temp, 0);
                Tooltip.install(bit, temp);

                mainBox.getChildren().add(bit);
            }
            mainBox.setFillHeight(true);
            mainBox.setSnapToPixel(false);
            mainBox.setSpacing(2);

            getChildren().add(mainBox);
            Initialized = true;
        }
        Update();
    }

    //Shamelessly stolen from: http://stackoverflow.com/a/27739605
    private static void hackTooltipStartTiming(Tooltip tooltip, int lengthMS) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(lengthMS)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
