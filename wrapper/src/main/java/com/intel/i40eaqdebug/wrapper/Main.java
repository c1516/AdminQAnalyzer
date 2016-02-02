package com.intel.i40eaqdebug.wrapper;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.backend.BackendMain;
import com.intel.i40eaqdebug.gui.GUIMain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Main Launcher Class
 */
public class Main {
    public static void main(String... args) {
        try {
            BackendMain.main();
            System.out.println(APIEntryPoint.getCommandStruct(1) != null);
            GUIMain.main();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
