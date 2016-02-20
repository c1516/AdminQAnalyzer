package com.intel.i40eaqdebug.gui.controllers;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.TimeStamp;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.gui.customcontrols.opcodecell.OpCodeCell;
import com.intel.i40eaqdebug.gui.GUIMain;
import com.intel.i40eaqdebug.gui.customcontrols.checkboxcell.CheckboxCell;
import com.intel.i40eaqdebug.gui.customcontrols.flagviewcell.FlagViewCell;
import com.intel.i40eaqdebug.gui.customcontrols.timestampcell.TimeStampCell;
import com.intel.i40eaqdebug.gui.datamodels.TableModel;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SingleTabController {
    boolean noTimeStamp = false;
    //region FXML properties.
    @FXML private TableView<TableModel> TabTable;
    @FXML private VBox HideablePane;
    @FXML private SplitPane BaseSplitPane;
    @FXML private TableColumn<TableModel, TimeStamp> timeColumn;
    @FXML private TableColumn<TableModel, String> numColumn;
    @FXML private TableColumn<TableModel, Integer> flagColumn;
    @FXML private TableColumn<TableModel, Boolean> writeBackColumn;
    @FXML private TableColumn<TableModel, Integer> opCodeColumn;
    @FXML private ContextMenu TabContext;

    //endregion
    private int EventTotal = 0;
    private int pixelPadding = 15;
    private GUIMain Application;
    private Queue<LogEntry> logLines;
    private boolean DetailsVisible = false;
    private double DetailsHeight = -1;
    private VirtualFlow<?> virtualFlow;

    /**
     * Initializes a tab controller
     *
     * @param App  Not null, the main application, can't be null
     * @param logs A Queue contaning {@link LogEntry}
     */
    public SingleTabController(GUIMain App, Queue<LogEntry> logs) {
        Application = App;
        logLines = logs;
    }

    //region FXML Functions

    /**
     * Initializes the tab.
     */
    @FXML public void initialize() {
        HideablePane.setMaxHeight(0);

        BaseSplitPane.widthProperty().addListener((obv, oldWidth, newWidth) -> {
            TabTable.setMaxWidth((double) newWidth);
        });

        //This listener is used to hide the details pane, if we click out of our tab.
        Application.getMainStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            Object src = event.getSource();
            if (!clickInPane(event.getSceneX(), event.getSceneY())) {
                HideDetails();
            }
        });

        //This listener refreshes the table on maximization so the FlagViewers can fix themselves.
        Application.getMainStage().maximizedProperty().addListener((obs, oldv, newv) -> {
            refresh(TabTable);
        });

        TabContext.setOnShown((windowEvent) -> {
            if (TabTable.getItems().size() == 0 || TabTable.getSelectionModel().getSelectedItem() == null) {
                TabContext.hide();
            }
        });

        InitializeTableView();
    }

    private void refresh(TableView view) {
        try {
            Method refresh = TableView.class.getDeclaredMethod("refresh");
            refresh.setAccessible(true);
            refresh.invoke(view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function initializes everything related with the TableView.
     */
    private void InitializeTableView() {
        double totalColWidth = 0;
        for (TableColumn c : TabTable.getColumns())
            totalColWidth += c.getMinWidth();
        //So.... I don't actually know why I need to multiply this by 2, but it works...
        //......yeah, that's what I thought to......
        totalColWidth += pixelPadding * 2;
        Application.getMainStage().setMinWidth(totalColWidth);

        //This listener resizes the columns by percentage when the table gets resized.
        //IE: if one column took up 50% of the screen, when the window is resized (or maximized)
        //it will still take up 50% of the screen (unless it hit it's max/min sizes).
        TabTable.widthProperty().addListener((obs, oldv, newv) -> {
            double oldWidth = (double) oldv;
            double newWidth = (double) newv;

            if (oldWidth == 0)
                return;

            TableColumn<?, ?> biggest = TabTable.getColumns().get(0);
            double totalWidth = 0;
            for (TableColumn c : TabTable.getColumns()) {
                if (c.getWidth() > biggest.getWidth())
                    biggest = c;

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


        //This changes the table sorting event so that instead of doing nothing on the "neutral" state,
        //It sorts by the TimeStamp in Ascending order.
        TabTable.setOnSort((SortEvent) -> {
            TableView<TableModel> target = (TableView<TableModel>) SortEvent.getTarget();
            //Find the timestamp column and sort it.
            if (target.getSortOrder().size() == 0) {
                TableColumn<TableModel, ?> targetColumn = null;
                for (int i = 0; i < target.getColumns().size(); i++) {
                    if ((!noTimeStamp && target.getColumns().get(i).getText().equals("Time Stamp")) || (noTimeStamp
                        && target.getColumns().get(i).getText().equals("Line Number"))) {
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

        //This listener creates and brings up a details panel based on the current selection in our table.
        TabTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                DetailsVisible = true;
                HideablePane.getChildren().clear();
                HideablePane.setMaxHeight(Double.POSITIVE_INFINITY);
                int selectedRow = TabTable.getSelectionModel().getSelectedIndex();
                TableModel theRow = TabTable.getSelectionModel().getSelectedItem();


                //This initialization code need to only runs once, when the panel is first made.
                //It adds a divider to the base split panel and sets it's height to 60% of the screen.
                //On every other run it restores the saved division height;
                if (DetailsHeight == -1) {
                    TableViewSkin<?> tableSkin = (TableViewSkin<?>) TabTable.getSkin();
                    virtualFlow = (VirtualFlow<?>) tableSkin.getChildren().get(1);

                    BaseSplitPane.getDividers().get(0).positionProperty().addListener((obj, oldVal, newVal) -> {
                        if (DetailsVisible) {
                            DetailsHeight = newVal.doubleValue();
                            scrollTo(selectedRow);
                        }
                    });
                    BaseSplitPane.setDividerPosition(0, 0.6);
                } else
                    BaseSplitPane.setDividerPosition(0, DetailsHeight);

                //Retrieve the row number of the selected entry, retrieve that log entry and pass it to the detail window
                LogEntry tempLogEntry = theRow.logLine;//(LogEntry) ((LinkedList) logLines).get(selectedRow);

                FXMLLoader tabFXML = new FXMLLoader(getClass().getResource("/DetailsPane.fxml"));
                tabFXML.setController(new DetailsPaneController(Application, tempLogEntry));
                SplitPane testPane = null;

                try {
                    testPane = tabFXML.load();

                    HideablePane.getChildren().add(testPane);
                    HideablePane.setVisible(true);
                } catch (IOException Ex) {
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    Ex.printStackTrace(printWriter);
                    printWriter.flush();

                    String stackTrace = writer.toString();
                    DialogController.CreateDialog("An error occured!", Ex.getMessage() + "\n" + stackTrace, true);
                }

            }
        });

        //This listener modifies the rowfactory a little bit, so that
        //Rows with errors in them get a nice red highlight.
        TabTable.setRowFactory(TableData -> {
            TableRow<TableModel> row = new TableRow<>();
            PseudoClass error = PseudoClass.getPseudoClass("error");

            //This listener sets the rows CSS class if it has an error.
            row.itemProperty().addListener((obs, oldItem, currentItem) -> {
                if (currentItem == null) {
                    row.pseudoClassStateChanged(error, false);
                    return;
                }

                if (!currentItem.getErrorCode().equals("I40E_AQ_RC_OK"))
                    row.pseudoClassStateChanged(error, true);
                else
                    row.pseudoClassStateChanged(error, false);
            });

            return row;
        });

        InitializeTableColumns();

        fillTable(null, true);
        refresh(TabTable);
    }

    /**
     * This function initializes all of the custom columns in our table view.
     * It also creates the resize policy
     */
    private void InitializeTableColumns() {

        //Here we create a custom resize policy that dose two things:
        //1) It prevents us from expanding our columns of the screen
        //2) It automatically sizes columns to the right of the ones the user is resizing
        TabTable.setColumnResizePolicy((StuffToResize) -> {
            TableView<?> table = StuffToResize.getTable();
            TableColumn<?, ?> column = StuffToResize.getColumn();
            Double delta = StuffToResize.getDelta();

            Double tableWidth = TabTable.getWidth() - pixelPadding;
            if (column != null) {
                double newWidth = column.getWidth() + delta;
                //We only want to resize if our column hasn't hit it's limits.
                if ((newWidth < column.getMaxWidth()) && (newWidth > column.getMinWidth())) {
                    ArrayList<TableColumn<?, ?>> affectedCols = new ArrayList<>();

                    int index = table.getColumns().indexOf(column);
                    int lastIndex = table.getColumns().size() - 1;
                    double newDelta = delta / (table.getColumns().size() - (index + 1));
                    double totalWidth = 0;
                    int i = 0;

                    //Here we figure out which columns to the right of ours are going to be affected (ie: also resized)
                    //by our column
                    for (TableColumn c : table.getColumns()) {
                        double newColWidth = c.getWidth() - newDelta;
                        if (i++ > index && ((delta > 0 && newColWidth >= c.getMinWidth()) || (delta < 0
                            && newColWidth <= c.getMaxWidth())))
                            affectedCols.add(c);

                        totalWidth += c.getWidth();
                    }
                    totalWidth += delta;

                    //Here we determine if we should 1) actually resize our column 2) resize the rest of the affected ones.
                    if (affectedCols.size() != 0 || (index == lastIndex && totalWidth < tableWidth)
                        || totalWidth < tableWidth) {
                        column.setPrefWidth(column.getWidth() + delta);


                        //Uncomment for another version of resizing. This way it wont resizing when collapsing our
                        //column
                        //if (delta > 0 && totalWidth >= tableWidth) {
                        newDelta = delta / affectedCols.size();
                        for (TableColumn c : affectedCols) {
                            c.setPrefWidth(c.getWidth() - newDelta);
                        }
                        //}
                    }
                }
            }
            return true;
        });

        //This listener overrides the Line Number columns comparator to compare the numbers, not the strings.
        numColumn.setComparator((value1, value2) -> {
            //Yeah I know this is inefficient, but at the same time I don't care
            int iv1 = Integer.parseInt(value1);
            int iv2 = Integer.parseInt(value2);
            return iv1 < iv2 ? -1 : (iv1 > iv2 ? 1 : 0);
        });

        //This listener overrides the TimeStamp columns comparator so it compares by time.
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

        opCodeColumn.setCellFactory(ColumnData -> new OpCodeCell());
        opCodeColumn.setComparator((v1, v2) -> (v1 > v2 ? 1 : (v1 < v2 ? -1 : 0)));

        //This makes sure the TimeStamp column uses TimeStamp cells, not normal ones
        timeColumn.setCellFactory(ColumnData -> new TimeStampCell());


        //This does the same as above.
        writeBackColumn.setCellFactory(ColumnData -> new CheckboxCell());

        //This does the same same above, except it also lets us select the table row through the FlagViewer
        flagColumn.setCellFactory(ColumnData -> {
            FlagViewCell test = new FlagViewCell();
            //This is ugly, and it shouldn't be necessary, but because of how events are handled
            //there's no other choice.
            test.flagViewer.setOnMousePressed(e -> {
                TabTable.getSelectionModel().select((TableModel) test.getTableRow().getItem());
                TabTable.requestFocus();
            });
            return test;
        });
    }

    /**
     * This allows our context menu to copy the entier row as a string.
     */
    @FXML public void CopyRow() {
        String row = TabTable.getSelectionModel().getSelectedItem().toString();

        StringSelection stringSelection = new StringSelection(row);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    /**
     * This allows our context meny to only copy the cell that's clicked on.
     * Unfortunately since we're selecting an row at a time, this can be a little
     * in-accurate (due to having no visual indication of the selected cell).
     */
    @FXML public void CopyCell() {
        TablePosition pos = TabTable.getSelectionModel().getSelectedCells().get(0);
        TableModel item = TabTable.getItems().get(pos.getRow());
        TableColumn col = pos.getTableColumn();

        String cell = col.getCellObservableValue(item).getValue().toString();

        StringSelection stringSelection = new StringSelection(cell);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    /**
     * This handles a keypress on a row, and lets you copy it if CTRL+C is pressed
     *
     * @param event The Keyboard event.
     */
    @FXML public void KeyShortcuts(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            CopyRow();
        }
    }

    //endregion

    /**
     * Returns the total number of events currently visible
     *
     * @return the total number of events currently visible
     */
    public int getEventTotal() {
        return EventTotal;
    }

    /**
     * This function searches the table by re-filling it with a filter
     * and then re-sorts it.
     *
     * @param term  The search term, null clears the filter
     * @param match Weather to use the filter buttons.
     */
    public void Search(String term, Boolean match) {
        fillTable(term, match);
        TabTable.sort();
    }


    /**
     * This checks to see if a coordinate is within our tab. Used to hide the details pane if we click somewhere else.
     *
     * @param x The X position of the click
     * @param y The Y position of the click
     * @return Weather the click is in the pane or not.
     */
    private boolean clickInPane(double x, double y) {
        double lx = BaseSplitPane.getLayoutX();
        //TODO: The 80 pixel offset needs to be computed dynamically. (this corrects for the toolbar height).
        double ly = BaseSplitPane.getLayoutY() + 80;
        double height = BaseSplitPane.getLayoutBounds().getHeight();
        double width = BaseSplitPane.getLayoutBounds().getWidth();

        return (x >= lx && x <= (lx + width)) && (y >= ly && y <= (ly + height));
    }

    /**
     * Hides the details pane.
     */
    private void HideDetails() {
        if (DetailsVisible) {
            DetailsVisible = false;
            HideablePane.setMaxHeight(0);
        }
    }


    /**
     * Sexily scrolls our Table to a specified index.
     * Stolen from StackOverflow but I already forgot from where.
     *
     * @param index The index of the row to scroll to.
     */
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

    /**
     * This fills our table with data from the Queue of {@link LogEntry}s
     * If a filter exists, it will only add entries who contains that string
     * Match is used for filter buttons
     *
     * @param Filter Used to include results, if null everything is displayed
     * @param Match  Used for filter buttons.
     */
    private void fillTable(String Filter, Boolean Match) {
        ObservableList<TableModel> data = TabTable.getItems();
        data.clear();

        Queue<LogEntry> test = new LinkedList<>(logLines);

        Integer Total = 0;
        while (test.size() > 0) {
            LogEntry temp = test.remove();
            //removes timestamp column if it doesnt exist in file.
            if (!noTimeStamp && temp.getTimeStamp().getSeconds() == -1 && temp.getTimeStamp().getNanos() == -1) {
                noTimeStamp = true;
                TabTable.getColumns().remove(0);
            }


            String Error = APIEntryPoint.getErrorString(temp.getErr());
            String LineNumber = Integer.toString(temp.getStartLine());
            TimeStamp Time = temp.getTimeStamp();
            int OpCode = temp.getOpCode();
            short Flags = temp.getFlags();
            boolean IsWriteBack = temp.isWriteback();

            TableModel tempModel = new TableModel(Time, LineNumber, OpCode, Flags, Error, IsWriteBack);
            tempModel.logLine = temp;

            //When wanting to display matched substring
            if (Match) {
                if (Filter == null || tempModel.hasPartialValue(Filter)) {
                    data.add(tempModel);
                    Total++;
                }
            }

            //When wanting to not display matched substring
            if (!Match) {
                if (Filter == null || !tempModel.hasPartialValue(Filter)) {
                    data.add(tempModel);
                    Total++;
                }
            }
        }

        EventTotal = Total;
        if (noTimeStamp)
            ResizeColumsAfterRemoval();

    }

    private void ResizeColumsAfterRemoval() {
        double totalSize = 0;
        double tabSize = TabTable.getWidth() - pixelPadding;
        for (TableColumn c : TabTable.getColumns()) {
            totalSize += c.getPrefWidth();
        }
        TableColumn lastCol = TabTable.getColumns().get(TabTable.getColumns().size() - 1);
        lastCol.setPrefWidth(lastCol.getPrefWidth() + (tabSize - totalSize));
    }

}
