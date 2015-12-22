import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {

    private static final String MOCK_BYTEBUF_DATA = "01 80 C2 00 00 0E 68 05 CA 30 E1 30 88 CC 02 07";
    private static final String[] MOCK_BYTEBUF_RAW = MOCK_BYTEBUF_DATA.split(" ");
    private static HTMLEditor rawbytes;

    private static HashMap<String, Command> command;

    public static void main(String[] args) {
        launch(args);
    }

    // Mock command data
    private static void initCommands() {
        command = new HashMap<>();
        Command vCommand = new Command();
        vCommand.name = "i40e_aqc_get_version";
        vCommand.fields = new String[] {"rom_ver", "fw_build", "fw_major", "fw_minor", "api_major", "api_minor"};
        vCommand.sizes = new Integer[] {4, 4, 2, 2, 2, 2};
        command.put(vCommand.name, vCommand);
        Command dCommand = new Command();
        dCommand.name = "i40e_aqc_driver_version";
        dCommand.fields =
            new String[] {"driver_major_ver", "driver_minor_ver", "driver_build_ver", "driver_subbuild_ver", "reserved",
                "address_high", "address_low"};
        dCommand.sizes = new Integer[] {1, 1, 1, 1, 4, 4, 4};
        command.put(dCommand.name, dCommand);
        Command sCommand = new Command();
        sCommand.name = "i40e_aqc_queue_shutdown";
        sCommand.fields = new String[] {"driver_unloading", "reserved"};
        sCommand.sizes = new Integer[] {4, 12};
        command.put(sCommand.name, sCommand);
    }

    @Override public void start(Stage stage) throws Exception {
        initCommands();
        stage.setTitle("Test GUI With Example Applicable to AdminQ display");
        stage.setMaximized(true);
        VBox box = new VBox();
        Scene scene = new Scene(box, stage.getWidth(), stage.getHeight());
        stage.setScene(scene);

        // Populate mock entries
        ObservableList<String> entriesObservable = FXCollections
            .observableArrayList("i40e_aqc_get_version", "i40e_aqc_driver_version", "i40e_aqc_queue_shutdown");
        ListView<String> entriesView = new ListView<>(entriesObservable);

        // Create view pane for entries
        entriesView.setMinHeight(200);
        box.getChildren().addAll(entriesView);
        VBox.setVgrow(entriesView, Priority.ALWAYS);

        // Create view pane for fields
        ObservableList<String> fieldsObservable = FXCollections.observableArrayList();
        ListView<String> fieldsView = new ListView<>(fieldsObservable);
        box.getChildren().addAll(fieldsView);
        fieldsView.setMinHeight(100);
        VBox.setVgrow(fieldsView, Priority.ALWAYS);

        // Create view pane for the actual byte info
        rawbytes = new HTMLEditor();
        rawbytes.setMinHeight(100);
        rawbytes.setHtmlText(MOCK_BYTEBUF_DATA);
        box.getChildren().add(rawbytes);

        // Register an update listener for clicks on entries to update views
        entriesView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateFields(newValue, fieldsView);
        });
        stage.show();
    }

    private void updateFields(String input, ListView<String> fieldsView) {
        ObservableList<String> curr = fieldsView.getItems();
        curr.clear();
        Command c = command.get(input);
        String[] bytes = MOCK_BYTEBUF_DATA.split(" ");
        for (int i = 0; i < bytes.length / 2; i++) {
            String temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
        for (String field : c.fields) {
            curr.add(field);
        }
        // Update highlighting on a field selection
        fieldsView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int offset = 0;
            int start = -1;
            int end = -1;
            for (int i = 0; i < c.fields.length; i++) {
                String currName = c.fields[i];
                int len = c.sizes[i];
                if (currName.equals(newValue)) {
                    end = MOCK_BYTEBUF_RAW.length - offset;
                    start = end - len;
                    break;
                } else {
                    offset += len;
                }
            }
            StringBuilder out = new StringBuilder();
            int i = 0;
            for (String byteraw : MOCK_BYTEBUF_RAW) {
                if (i == start) {
                    out.append("<b>");
                }
                out.append(byteraw).append("  ");
                i++;
                if (i == end) {
                    out.append("</b>");
                }
            }
            rawbytes.setHtmlText(out.toString());
        });
    }


    private static class Command {
        String name;
        String[] fields;
        Integer[] sizes;
    }
}
