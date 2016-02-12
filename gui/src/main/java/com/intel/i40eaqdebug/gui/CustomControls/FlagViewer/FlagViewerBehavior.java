package com.intel.i40eaqdebug.gui.CustomControls.FlagViewer;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Created by andrey on 2/11/2016.
 */
public class FlagViewerBehavior extends BehaviorBase {
    /**
     * Create a new BehaviorBase for the given control. The Control must not
     * be null.
     *
     * @param control The control. Must not be null.
     * @param list    The key bindings that should be used with this behavior.
     */
    public FlagViewerBehavior(Control control, List list) {
        super(control, list);
    }
}
