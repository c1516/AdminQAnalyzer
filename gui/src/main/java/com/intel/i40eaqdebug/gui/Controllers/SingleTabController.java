package com.intel.i40eaqdebug.gui.Controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.TimeStamp;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.CustomControls.CheckboxCell.CheckboxCell;
import com.intel.i40eaqdebug.gui.CustomControls.FlagViewCell.FlagViewCell;
import com.intel.i40eaqdebug.gui.CustomControls.TimeStampCell.TimeStampCell;
import com.intel.i40eaqdebug.gui.DataModels.TableModel;
import com.intel.i40eaqdebug.gui.GUIMain;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.*;
import java.util.List;

//TODO: format this file better. It's hard to readn and everything is all over the place.

public class SingleTabController {
    //region FXML properties.
    @FXML
    private TableView<TableModel> TabTable;

    @FXML
    private VBox HideablePane;
    @FXML
    private SplitPane BaseSplitPane;
    @FXML
    private TableColumn<TableModel, TimeStamp> timeColumn;
    @FXML
    private TableColumn<TableModel, String> numColumn;
    @FXML
    private TableColumn<TableModel, Integer> flagColumn;
    @FXML
    private TableColumn<TableModel, Boolean> writeBackColumn;

    //TODO Make sure initializing this to zero only happens upon tab creation, otherwise could cause problems
    private int EventTotal = 0;

    @FXML
    private Separator DraggbleSeparator;

    //endregion
    private int pixelPadding = 15;
    private GUIMain Application;
    private Queue<LogEntry> logLines;
    private boolean DetailsVisible = false;
    private double DetailsHeight = -1;
    private VirtualFlow<?> virtualFlow;

    /**
     * * * * * * * * * * * * * * * *
     * TEMPORARY CODE FOR TESTING *
     * * * * * * * * * * * * * * *
     */
    private boolean beh = false;
    public void setResBeh(Boolean beh) {
        this.beh = beh;
        if (beh) {
            double totalWidth = 0;
            for (TableColumn c : TabTable.getColumns()) {
                totalWidth += c.getWidth();
            }
            if (totalWidth < TabTable.getWidth() - pixelPadding) {
                double delta = (TabTable.getWidth() - pixelPadding) - totalWidth;
                TableColumn<?,?> last = TabTable.getColumns().get(TabTable.getColumns().size() - 1);
                last.setPrefWidth(last.getWidth() + delta);
            }
        }

    }
    /**
     * * * * * * * * * * * * * * * * * *
     * END TEMPORARY CODE FOR TESTING *
     * * * * * * * * * * * * * * * * *
     */

    public SingleTabController(GUIMain App, Queue<LogEntry> logs) {
        Application = App;
        logLines = logs;
    }

    public SingleTabController() {
    }

    private void HideDetails() {
        if (DetailsVisible) {
            DetailsVisible = false;
            HideablePane.setMaxHeight(0);
        }
    }

    public void Search(String term, Boolean match) {
        fillTable(term, match);
        TabTable.sort();
    }

    //This function checks to see if a click happens inside or outside the details pane
    //Used to decide weather to hide it or now.
    private boolean clickInPane(double x, double y) {
        double lx = BaseSplitPane.getLayoutX();
        //TODO: The 80 pixel offset needs to be computed dynamically. (this corrects for the toolbar height).
        double ly = BaseSplitPane.getLayoutY() + 80;
        double height = BaseSplitPane.getLayoutBounds().getHeight();
        double width = BaseSplitPane.getLayoutBounds().getWidth();

        if ((x >= lx && x <= (lx + width)) && (y >= ly && y <= (ly + height))) {
            return true;
        } else {
            return false;
        }
    }


    @FXML
    public void initialize() {
        HideablePane.setMaxHeight(0);

        BaseSplitPane.widthProperty().addListener((obv, oldWidth, newWidth) -> {
            TabTable.setMaxWidth((double) newWidth);
        });

        //This sets up an listener on the main scene, watchig for mouse clicks.
        //Used to hide the details pane when the user clicks off of it.
        Application.getMainStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            Object src = event.getSource();
            if (!clickInPane(event.getSceneX(), event.getSceneY())) {
                HideDetails();
            }
        });

        Application.getMainStage().maximizedProperty().addListener((obs, oldv, newv) -> {
            TabTable.refresh();
        });

        Application.getMainStage().getScene().widthProperty().addListener((obs, oldv, newv) -> {
            double oldWidth = (double)oldv;
            double newWidth = (double)newv;

            TableColumn<?,?> biggest = TabTable.getColumns().get(0);
            double totalWidth = 0;
            for (TableColumn c: TabTable.getColumns()) {
                if (c.getWidth() > biggest.getWidth()) biggest = c;
                double newColWidth = ((c.getWidth() / oldWidth) * newWidth);

                if (newColWidth < c.getMinWidth())
                    c.setPrefWidth(c.getMinWidth());
                else if (newColWidth > c.getMaxWidth())
                    c.setPrefWidth(c.getMaxWidth());
                else
                    c.setPrefWidth(newColWidth);

                totalWidth += c.getWidth();
            }
            double tabWidth = TabTable.getWidth() - pixelPadding;
            if (totalWidth > tabWidth)
                biggest.setPrefWidth(biggest.getWidth() + (tabWidth - totalWidth));
        });

        InitializeTableView();
    }

    private void InitializeTableView() {
        double totalColWidth = 0;
        for (TableColumn c: TabTable.getColumns()) totalColWidth += c.getMinWidth();
        //So.... I don't actually know why I need to multiply this by 2, but it works...
        //......yeah, that's what I thought to......
        totalColWidth += pixelPadding * 2;
        Application.getMainStage().setMinWidth(totalColWidth);

        TabTable.setColumnResizePolicy((StuffToResize) -> {
            TableView<?> table = StuffToResize.getTable();
            TableColumn<?,?> column = StuffToResize.getColumn();
            Double delta = StuffToResize.getDelta();

            Double tableWidth = TabTable.getWidth() - pixelPadding;


            if( column != null) {
                double newWidth = column.getWidth() + delta;
                if ((newWidth < column.getMaxWidth()) && (newWidth > column.getMinWidth())) {
                    ArrayList<TableColumn<?,?>> affectedCols = new ArrayList<TableColumn<?,?>>();

                    int index = table.getColumns().indexOf(column);
                    int lastIndex = table.getColumns().size() - 1;
                    double newDelta = delta / (table.getColumns().size() - (index + 1));
                    double totalWidth = 0;
                    int i = 0;
                    for (TableColumn c : table.getColumns()) {
                        double newColWidth = c.getWidth() - newDelta;
                        if (i++ > index && ((delta > 0 && newColWidth >= c.getMinWidth()) || (delta < 0 && newColWidth <= c.getMaxWidth())))
                            affectedCols.add(c);

                        totalWidth += c.getWidth();
                    }
                    totalWidth += delta;
                    //System.out.println("AffCol: " + affectedCols.size());

                    if (affectedCols.size() != 0 || (index == lastIndex && totalWidth < tableWidth) || totalWidth < tableWidth) {
                        column.setPrefWidth(column.getWidth() + delta);

                        //TEMPORARY CODE: "beh || "
                        if (beh || (delta > 0 && totalWidth >= tableWidth)) {
                            newDelta = delta / affectedCols.size();
                            for (TableColumn c : affectedCols) {
                                c.setPrefWidth(c.getWidth() - newDelta);
                            }
                        }
                    }
                }
            }
            return true;
        });


        TabTable.setOnSort((SortEvent) -> {
            TableView<TableModel> target = (TableView<TableModel>) SortEvent.getTarget();
            if (target.getSortOrder().size() == 0) {
                TableColumn<TableModel, ?> targetColumn = null;
                for (int i = 0; i < target.getColumns().size(); i++) {
                    if (target.getColumns().get(i).getText().equals("Time Stamp")) {
                        targetColumn = target.getColumns().get(i);
                        break;
                    }
                }
                if (targetColumn != null) {
                    targetColumn.setSortType(TableColumn.SortType.ASCENDING);
                    target.getSortOrder().add(targetColumn);
                }
            }
        });

        //This sets up an event listener on the tables selector. IE: if the current selection changes, we run this code.
        //There is probably a better way to declare this as a separate function and pass it in instead, hence the todo.
        TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                DetailsVisible = true;
                HideablePane.getChildren().clear();

                HideablePane.setMaxHeight(Double.POSITIVE_INFINITY);

                if (DetailsHeight == -1) {
                    TableViewSkin<?> tableSkin = (TableViewSkin<?>) TabTable.getSkin();
                    virtualFlow = (VirtualFlow<?>) tableSkin.getChildren().get(1);

                    BaseSplitPane.getDividers().get(0).positionProperty().addListener((obj, oldVal, newVal) -> {
                        if (DetailsVisible) {
                            DetailsHeight = newVal.doubleValue();
                            scrollTo(TabTable.getSelectionModel().getSelectedIndex());
                        }
                    });
                    BaseSplitPane.setDividerPosition(0, 0.6);
                } else {
                    BaseSplitPane.setDividerPosition(0, DetailsHeight);
                }


                //Retrieve the row number of the selected entry, retrieve that log entry and pass it to the detail window
                int selectedRow = TabTable.getSelectionModel().getSelectedIndex();
                LogEntry tempLogEntry = (LogEntry) ((LinkedList) logLines).get(selectedRow);

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/DetailsPane.fxml"));
                tabFXML.setController(new DetailsPaneController(Application, tempLogEntry));
                SplitPane testPane = null;
                try {
                    testPane = tabFXML.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HideablePane.getChildren().add(testPane);
                //HideablePane.requestFocus();
                HideablePane.setVisible(true);

            }
        });
        fillTable(null, true);



        numColumn.setComparator((value1, value2) -> {
            //Yeah I know this is inefficient, but at the same time I don't care
            int iv1 = Integer.parseInt(value1);
            int iv2 = Integer.parseInt(value2);
            return iv1 < iv2 ? -1 : (iv1 > iv2 ? 1 : 0);
        });

        timeColumn.setComparator((value1, value2) -> {
            if (value1.getSeconds() > value2.getSeconds())
                return 1;
            else if (value1.getSeconds() < value2.getSeconds())
                return -1;
            else {
                if (value1.getNanos() > value2.getNanos())
                    return 1;
                else if (value1.getNanos() < value2.getNanos())
                    return -1;
                else
                    return 0;
            }
        });

        timeColumn.setCellFactory(ColumnData -> new TimeStampCell());

        flagColumn.setCellFactory(ColumnData -> {
            FlagViewCell test = new FlagViewCell();
            //This is ugly, and it shouldn't be necessary, but because of how events are handled
            //I don't really have a choice.
            test.flagViewer.setOnMousePressed(e -> {
                TabTable.getSelectionModel().select((TableModel)test.getTableRow().getItem());
                TabTable.requestFocus();
            });
            return test;
        });

        //writeBackColumn.setCellValueFactory(CellData ->  CellData.getValue().getIsWriteBackProperty().getValue());
        writeBackColumn.setCellFactory(ColumnData -> new CheckboxCell());

        PseudoClass error = PseudoClass.getPseudoClass("error");

        TabTable.setRowFactory(TableData -> {
            TableRow<TableModel> row = new TableRow<>();

            //This simply sets up a listener on the item property of a row
            //AKA: When the row gets an item (or the item changes) we run this code.
            row.itemProperty().addListener((obs, oldItem, currentItem) -> {
                //If the currentItem doesn't exist (ie: nothing in the row)
                //we clear the row style by setting both CSS pseudo-classes to false.
                if (currentItem == null) {
                    row.pseudoClassStateChanged(error, false);
                    //row.pseudoClassStateChanged(success, false);
                    return;
                }

                if (!currentItem.getErrorCode().equals("I40E_AQ_RC_OK")) {
                    row.pseudoClassStateChanged(error, true);
                }else {
                    row.pseudoClassStateChanged(error, false);
                }
            });

            return row;
        });

        TabTable.refresh();

    }

    @FXML
    private void CopyRow() {
        String row = TabTable.getSelectionModel().getSelectedItem().toString();

        StringSelection stringSelection = new StringSelection(row);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    @FXML
    private void CopyCell() {
        TablePosition pos = TabTable.getSelectionModel().getSelectedCells().get(0);
        TableModel item = TabTable.getItems().get(pos.getRow());
        TableColumn col = pos.getTableColumn();

        String cell = col.getCellObservableValue(item).getValue().toString();

        StringSelection stringSelection = new StringSelection(cell);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    @FXML
    private void KeyShortcuts(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            CopyRow();
        }
    }

    private void scrollTo(int index) {
        int first = virtualFlow.getFirstVisibleCell().getIndex();
        int last = virtualFlow.getLastVisibleCell().getIndex();
        if (index <= first) {
            while (index <= first && virtualFlow.adjustPixels(-1) < 0) {
                first = virtualFlow.getFirstVisibleCell().getIndex();
            }
        } else {
            while (index >= last && virtualFlow.adjustPixels(1) > 0) {
                last = virtualFlow.getLastVisibleCell().getIndex();
            }
        }
    }

    //This function fills the TableView with models for items
    //If there is a filter string, it will only include items that match that string.
    //Match indicates if incidences of the Filter string should be displayed or not
    private void fillTable(String Filter, Boolean Match) {
        ObservableList<TableModel> data = TabTable.getItems();
        data.clear();

        Queue<LogEntry> test = new LinkedList<LogEntry>(logLines);

        Integer Total = 0;
        //Integer LineNumber = 0;
        while (test.size() > 0) {
           // LineNumber++;
            LogEntry temp = test.remove();
            String Error = APIEntryPoint.getErrorString(temp.getErr());

            TableModel tempModel = new TableModel(temp.getTimeStamp(),
                    Integer.toString(temp.getStartLine()), (int) temp.getOpCode(), temp.getFlags(), Error, temp.isWriteback());

            //When wanting to display matched substring
            if (Match) {
                if (Filter == null || (Filter != null && tempModel.hasPartialValue(Filter))) {
                    data.add(tempModel);
                    Total++;
                }
            }

            //When wanting to not display matched substring
            if (!Match) {
                if (Filter == null || (Filter != null && !tempModel.hasPartialValue(Filter))) {
                    data.add(tempModel);
                    Total++;
                }
            }
        }

        EventTotal = Total;
    }


    public int getEventTotal() {
        return EventTotal;
    }
}
