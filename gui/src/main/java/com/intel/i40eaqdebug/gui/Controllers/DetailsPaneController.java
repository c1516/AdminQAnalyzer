package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.Util;
import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.DataModels.DetailTableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import com.sun.deploy.util.StringUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import javax.xml.bind.DatatypeConverter;
import java.util.*;


public class DetailsPaneController {
    //region FXML properties.
    @FXML
    public AnchorPane Pane;
    @FXML
    public TextArea RawArea;
    @FXML
    public TableView<DetailTableModel> DetailTable = new TableView<>();
    //endregion
    private GUIMain Application;
    private LogEntry LogLine;



    public DetailsPaneController(GUIMain App, LogEntry selectedRow) {
        Application = App;
        LogLine = selectedRow;
    }

    public DetailsPaneController() {

    }

    /*
    So the idea here is pretty simple. We get one log entry from the
    calling window, and display it's contetns (other then the stuff already
    displayed in the table) + use it's opcode to fetch it's command sturct and also display that.
     */
    @FXML
    public void initialize() {
        int spacer = 0;
        int line = 0;
        String buffer = DatatypeConverter.printHexBinary(LogLine.getBuffer());
        RawArea.setWrapText(true);
        StringBuilder temp = new StringBuilder();
        for (char c : buffer.toCharArray()) {

            if (line % RawArea.getPrefColumnCount() == 0) {
                if (line != 0)
                    temp.append('\n');

                String hex = Integer.toHexString(spacer).toUpperCase();
                String format = "0x%1$6" + "s";
                String finals = String.format(format, hex).replace(' ', '0') + "    ";

                temp.append(finals);
            }else if (spacer != 0 && spacer % 4 == 0)
                temp.append(' ');

            //if (line % )
            temp.append(c);

            line++;
            spacer++;
        }
        RawArea.setText(temp.toString());

        ObservableList<DetailTableModel> rows =  DetailTable.getItems();
        String lineNumber = Integer.toString(LogLine.getStartLine());
        //TODO: is this the right way to combine cookies
        String cookie = Integer.toString((LogLine.getCookieHigh() << 16) |  LogLine.getCookieLow());

        //rows.add(new DetailTableModel("Line Number", lineNumber));
        rows.add(new DetailTableModel("Cookie", cookie));

        CommandStruct tempStruct = APIEntryPoint.getCommandStruct((int)LogLine.getOpCode(), LogLine.isWriteback());
        if (tempStruct == null) {
            return;
        }
        LinkedHashMap<String, CommandField> structContents = tempStruct.getFields();

        for (Map.Entry<String, CommandField> entry : structContents.entrySet()) {
            // Build the actual buffer for the command structure latter 16 bytes
            byte[] structBuf = new byte[16];
            // TODO double check that the ordering is actually right
            int[] params = LogLine.getParams();
            int[] addr = LogLine.getAddr();

            byte[][] paramsByte = new byte[][] {Util.toBytes(params[0]), Util.toBytes(params[1])};
            byte[][] addrByte = new byte[][] {Util.toBytes(addr[0]), Util.toBytes(addr[1])};
            System.arraycopy(paramsByte[0], 0, structBuf, 0, 4);
            System.arraycopy(paramsByte[1], 0, structBuf, 4, 4);
            System.arraycopy(addrByte[0], 0, structBuf, 8, 4);
            System.arraycopy(addrByte[1], 0, structBuf, 12, 4);

            DetailTableModel tempModel = new DetailTableModel(entry.getKey(), entry.getValue().getValueAsString(structBuf));

            rows.add(tempModel);
        }
    }

}

