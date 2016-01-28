package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class DetailsPaneController {
    //region FXML properties.
    @FXML
    public AnchorPane Pane;
    //endregion

    private GUIMain Application;
    private TableModel Selection;

    public DetailsPaneController(GUIMain App, TableModel selectedRow) {
        Application = App;
        Selection = selectedRow;
    }

    public DetailsPaneController() {

    }

    @FXML
    public void initialize() {
        /*Text text1 = new Text("Big italic red text");
        text1.setFill(Color.RED);
        text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 40));
        Text text2 = new Text(" little bold blue text");
        text2.setFill(Color.BLUE);
        text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
        TextFlow textFlow = new TextFlow(text1, text2);*/
    }

}
