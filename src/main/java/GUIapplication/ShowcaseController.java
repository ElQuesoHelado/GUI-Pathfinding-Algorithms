package GUIapplication;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import javafx.event.ActionEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ShowcaseController implements Initializable {
    @FXML
    private Canvas mainCanvas, checkedNodesCanvas, shortestPathCanvas, startNodeCanvas, endNodeCanvas, lockClickCanvas;
    @FXML
    private Button runButton, stopButton, clearButton;
    @FXML
    private TextField cellCountTF, startXTF, startYTF, endXTF, endYTF;
    @FXML
    private ToggleGroup terrainTypeToggleGroup, algorithmsToggleGroup, startEndSelectionTG;
    @FXML
    private RadioButton clearRB, obstacleRB, roughRB, dijkstraRB, startRB, endRB;
    @FXML
    private Label infoLabel;

    private GraphicsContext mainGC, checkedNodesGC, shortestPathGC, startNodeGC, endNodeGC;
    private Grid grid;
    private int terrainType, algorithmUsed, nodeTypeToSelect, cellCount;
    private double cellSideLength, canvasWidth, canvasHeight;
    private Task<Integer> pathfindingTask;

    private final Pattern validCellCount = Pattern.compile("^([2-9]|[1-9][0-9]|[1][0][0])$");

    private int drawGrid() {
        //No graphicsContext assigned to draw
        if (mainGC == null) {
            return 1;
        }
        //Clears canvas prior to drawing
        mainGC.clearRect(0, 0, canvasWidth, canvasHeight);

        //Variables for drawing
        //The width of the canvas dictates the size of the square grid
        double gridSideLength = canvasWidth;
        for (int i = 0; i <= cellCount; ++i) {
            mainGC.strokeLine(i * cellSideLength, 0, i * cellSideLength, gridSideLength);
            mainGC.strokeLine(0, i * cellSideLength, gridSideLength, i * cellSideLength);
        }
        return 0;
    }

    private int fillGridFromGridObject() {
        if (grid == null || grid.innerArray.size() != cellCount || grid.innerArray.get(0).size() != cellCount)
            return 1;
        //Save already assigned Fill color
        Paint savedFill = mainGC.getFill();

        for (int j = 0; j < cellCount; ++j) {
            for (int i = 0; i < cellCount; ++i) {
                switch (grid.innerArray.get(j).get(i)) {
                    case 0 -> mainGC.setFill(Color.BLACK);
                    case 1 -> mainGC.setFill(Color.WHITE);
                }
                mainGC.fillRect((i * cellSideLength + 0.5f), (j * cellSideLength + 0.5f),
                        cellSideLength - 1f, cellSideLength - 1f);
            }
        }

        mainGC.setFill(savedFill);

        return 0;
    }

    private void disableInputs() {
        runButton.setDisable(true);
        clearButton.setDisable(true);

        startXTF.setDisable(true);
        startYTF.setDisable(true);
        endXTF.setDisable(true);
        endYTF.setDisable(true);
        cellCountTF.setDisable(true);

        obstacleRB.setDisable(true);
        clearRB.setDisable(true);
        roughRB.setDisable(true);
        dijkstraRB.setDisable(true);
        startRB.setDisable(true);
        endRB.setDisable(true);
    }

    private void enableInputs() {
        runButton.setDisable(false);
        clearButton.setDisable(false);

        startXTF.setDisable(false);
        startYTF.setDisable(false);
        endXTF.setDisable(false);
        endYTF.setDisable(false);
        cellCountTF.setDisable(false);

        obstacleRB.setDisable(false);
        clearRB.setDisable(false);
        roughRB.setDisable(false);
        dijkstraRB.setDisable(false);
        startRB.setDisable(false);
        endRB.setDisable(false);
    }

    @FXML
    public void handleCellCountTF(ActionEvent event) {
        String newCellCountStr = cellCountTF.getText();
        //Invalid cellCount value, no changes
        if (!validCellCount.matcher(newCellCountStr).matches()) {
            cellCountTF.setText(Integer.toString(cellCount));
            return;
        }

        //Assign main controller variables
        cellCount = Integer.parseInt(newCellCountStr);
        cellSideLength = canvasWidth / (double) cellCount;
        startNodeGC.setFont(Font.font("Arial", FontWeight.BOLD, cellSideLength));
        endNodeGC.setFont(Font.font("Arial", FontWeight.BOLD, cellSideLength));

        //Resize grid and assign variables
        grid.resize(cellCount, cellCount);
        grid.setCellSideLength(cellSideLength);
        grid.setStartNode(0, 0);
        grid.setEndNode(cellCount - 1, cellCount - 1);

        //change textFields values
        startXTF.setText(Integer.toString(grid.getStartNode()[0]));
        startYTF.setText(Integer.toString(grid.getStartNode()[1]));
        endXTF.setText(Integer.toString(grid.getEndNode()[0]));
        endYTF.setText(Integer.toString(grid.getEndNode()[1]));

        //Clear all canvas
        checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
        shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        startNodeGC.clearRect(0, 0, canvasWidth, canvasHeight);
        endNodeGC.clearRect(0, 0, canvasWidth, canvasHeight);

        //Redraw based on existing Grid object
        drawGrid();
        fillGridFromGridObject();

        //Draw start and end
        if (cellCount > 50)
            shortestPathGC.setLineWidth(cellSideLength);
        else
            shortestPathGC.setLineWidth(cellSideLength / 2);

        startNodeGC.fillText("O", (grid.getStartNode()[0] + (1 / 8f)) * cellSideLength,
                (grid.getStartNode()[1] + (6 / 7f)) * cellSideLength);
        endNodeGC.fillText("X", (grid.getEndNode()[0] + (1 / 8f)) * cellSideLength,
                (grid.getEndNode()[1] + (6 / 7f)) * cellSideLength);
    }

    @FXML
    public void handleStartXTF(ActionEvent event) {
        int value = Integer.parseInt(startXTF.getText());
        int[] oldCoords = new int[2];
        oldCoords[0] = grid.getStartNode()[0];
        oldCoords[1] = grid.getStartNode()[1];

        if (value >= 0 && value < cellCount) {
            grid.setStartNode(value, grid.getStartNode()[1]);

            //Remove existing start on grid
            startNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f),
                    (oldCoords[1] * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);

            //Add start
            startNodeGC.fillText("O", value * cellSideLength + 0.5f + cellSideLength / 4f,
                    oldCoords[1] * cellSideLength + cellSideLength - 1f);

            //Clears checkedNodesCanvas and shortestPathCanvas
            checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
            shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        } else {
            startXTF.setText(Integer.toString(oldCoords[0]));
        }
    }

    @FXML
    public void handleStartYTF(ActionEvent event) {
        int value = Integer.parseInt(startYTF.getText());
        int[] oldCoords = new int[2];
        oldCoords[0] = grid.getStartNode()[0];
        oldCoords[1] = grid.getStartNode()[1];

        if (value >= 0 && value < cellCount) {
            grid.setStartNode(grid.getStartNode()[0], value);

            //Remove existing start on grid
            startNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f),
                    (oldCoords[1] * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);

            //Add start
            startNodeGC.fillText("O", oldCoords[0] * cellSideLength + 0.5f + cellSideLength / 4f,
                    value * cellSideLength + cellSideLength - 1f);

            //Clears checkedNodesCanvas and shortestPathCanvas
            checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
            shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        } else {
            startYTF.setText(Integer.toString(oldCoords[1]));
        }
    }

    @FXML
    public void handleEndXTF(ActionEvent event) {
        int value = Integer.parseInt(endXTF.getText());
        int[] oldCoords = new int[2];
        oldCoords[0] = grid.getEndNode()[0];
        oldCoords[1] = grid.getEndNode()[1];

        if (value >= 0 && value < cellCount) {
            grid.setEndNode(value, grid.getEndNode()[1]);

            //Remove existing end on grid
            endNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f), (oldCoords[1] * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);

            //Draw end
            endNodeGC.fillText("X", value * cellSideLength + 0.5f + cellSideLength / 4f,
                    oldCoords[1] * cellSideLength + cellSideLength - 1f);

            //Clears checkedNodesCanvas and shortestPathCanvas
            checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
            shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        } else {
            endXTF.setText(Integer.toString(oldCoords[0]));
        }
    }

    @FXML
    public void handleEndYTF(ActionEvent event) {
        int value = Integer.parseInt(endYTF.getText());
        int[] oldCoords = new int[2];
        oldCoords[0] = grid.getEndNode()[0];
        oldCoords[1] = grid.getEndNode()[1];

        if (value >= 0 && value < cellCount) {
            grid.setEndNode(grid.getEndNode()[0], value);

            //Remove existing end on grid
            endNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f), (oldCoords[1] * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);

            //Draw end
            endNodeGC.fillText("X", oldCoords[0] * cellSideLength + 0.5f + cellSideLength / 4f,
                    value * cellSideLength + cellSideLength - 1f);

            //Clears checkedNodesCanvas and shortestPathCanvas
            checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
            shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        } else {
            endYTF.setText(Integer.toString(oldCoords[1]));
        }
    }


    @FXML
    public void handleRunButtonAction(ActionEvent event) {
        disableInputs();
        stopButton.setDisable(false);
//        infoLabel.setText("Shortest path: " + grid.findPath(algorithmUsed));


        switch (algorithmUsed) {
            default:
                pathfindingTask = new PathFinding.DijkstraStepsTask(grid.generateAdjList(),
                        grid.getStartNode()[0] << 16 | grid.getStartNode()[1],
                        grid.getEndNode()[0] << 16 | grid.getEndNode()[1],
                        checkedNodesGC, shortestPathGC, cellSideLength);
                break;
        }

        pathfindingTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Integer result = pathfindingTask.getValue();
                infoLabel.setText("Shortest path: " + result);
                lockClickCanvas.toFront();
                stopButton.setDisable(true);
                enableInputs();
            }
        });

        pathfindingTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Integer result = pathfindingTask.getValue();
                infoLabel.setText("CANCELED");
                lockClickCanvas.toFront();
                stopButton.setDisable(true);
                enableInputs();
            }
        });

        Thread t = new Thread(pathfindingTask);
        t.start();
    }

    @FXML
    public void handleStopButton(ActionEvent event) {
        pathfindingTask.cancel();
    }

    @FXML
    public void handleClearButton(ActionEvent event) {
        //Clear all layers,reset Grid and redraw grid
        mainGC.clearRect(0, 0, canvasWidth, canvasHeight);
        checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
        shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
        grid.clearGrid();
        drawGrid();
        lockClickCanvas.toBack();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializing FXML variables for drawing with initial values
        canvasWidth = mainCanvas.getWidth();
        canvasHeight = mainCanvas.getHeight();
        mainGC = mainCanvas.getGraphicsContext2D();
        checkedNodesGC = checkedNodesCanvas.getGraphicsContext2D();
        shortestPathGC = shortestPathCanvas.getGraphicsContext2D();
        startNodeGC = startNodeCanvas.getGraphicsContext2D();
        endNodeGC = endNodeCanvas.getGraphicsContext2D();
        cellCount = Integer.parseInt(cellCountTF.getText());
        cellSideLength = canvasWidth / (double) cellCount;
        mainGC.setStroke(Color.BLACK);
        mainGC.setLineWidth(1.0);
        mainGC.setFill(Color.BLACK);
        shortestPathGC.setStroke(Color.RED);
//        shortestPathGC.setLineWidth(0.017585 * cellCount * cellCount - 2.74776 * cellCount + 105.425);
        shortestPathGC.setLineWidth(cellSideLength / 2);
        checkedNodesGC.setFill(Color.VIOLET);
        startNodeGC.setFill(Color.RED);
        endNodeGC.setFill(Color.RED);
        startNodeGC.setFont(Font.font("Arial", FontWeight.BOLD, cellSideLength));
        endNodeGC.setFont(Font.font("Arial", FontWeight.BOLD, cellSideLength));

        //First Grid draw with default values
        drawGrid();


        //**********************
//        int[] buffer = new int[650 * 650];
//        WritablePixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
//
//        DrawBufferShapes.drawLine(110, 50, 175, 115,
//                10, 0xFFFF0000, buffer, 650);
////        DrawBufferShapes.drawSquare(50.123, 300, 2, 0xFFFF0000, buffer, 650);
//
//
//        PixelWriter p = shortestPathGC.getPixelWriter();
//        p.setPixels(0, 0, 650, 650, pixelFormat, buffer, 0, 650);
        //***********************

        grid = new Grid(checkedNodesGC, shortestPathGC, cellCount, cellCount);
        grid.setCellSideLength(cellSideLength);
        grid.setStartNode(Integer.parseInt(startXTF.getText()), Integer.parseInt(startYTF.getText()));
        grid.setEndNode(Integer.parseInt(endXTF.getText()), Integer.parseInt(endYTF.getText()));


        //Draw default start and end nodes
        startNodeGC.fillText("O", (grid.getStartNode()[0] + (1 / 8f)) * cellSideLength,
                (grid.getStartNode()[1] + (6 / 7f)) * cellSideLength);
        endNodeGC.fillText("X", (grid.getEndNode()[0] + (1 / 8f)) * cellSideLength,
                (grid.getEndNode()[1] + (6 / 7f)) * cellSideLength);

        /*
         * Toggle Groups configurations
         * */

        // terrainToggleButtons configuration
        clearRB.setUserData("1");
        obstacleRB.setUserData("0");
        terrainType = Integer.parseInt((String) terrainTypeToggleGroup.getSelectedToggle().getUserData());

        //Algorithm to be used configuration
        dijkstraRB.setUserData("0");
        algorithmUsed = Integer.parseInt((String) algorithmsToggleGroup.getSelectedToggle().getUserData());

        //Start and end node configuration
        startRB.setUserData("0");
        endRB.setUserData("1");
        nodeTypeToSelect = Integer.parseInt((String) startEndSelectionTG.getSelectedToggle().getUserData());

        /*
         * Listeners assignments
         * */

        //Listens to changes in grid terrain type radio buttons
        terrainTypeToggleGroup.selectedToggleProperty().addListener(
                (observableValue, toggle, t1) -> {
                    if (terrainTypeToggleGroup.getSelectedToggle() != null) {
                        terrainType =
                                Integer.parseInt((String) terrainTypeToggleGroup.getSelectedToggle().getUserData());
                        if (terrainType == 1)
                            mainGC.setFill(Color.WHITE);
                        else
                            mainGC.setFill(Color.BLACK);
                    }
                }
        );

        //Listens for changes in algorithmsToggleGroup
        algorithmsToggleGroup.selectedToggleProperty().addListener(
                (observableValue, toggle, t1) -> {
                    if (algorithmsToggleGroup.getSelectedToggle() != null)
                        algorithmUsed =
                                Integer.parseInt((String) algorithmsToggleGroup.getSelectedToggle().getUserData());
                }
        );

        //Listens for changes in startEndSelectionTG
        startEndSelectionTG.selectedToggleProperty().addListener(
                (observable, toggle, t1) -> {
                    if (startEndSelectionTG.getSelectedToggle() != null) {
                        nodeTypeToSelect =
                                Integer.parseInt((String) startEndSelectionTG.getSelectedToggle().getUserData());
                    }
                }
        );
        //Listener for any type of click on lockClickCanvas
        lockClickCanvas.setOnMousePressed(mouseEvent -> {
            //Clears checkedNodesCanvas and shortestPathCanvas after clicking
            checkedNodesGC.clearRect(0, 0, canvasWidth, canvasHeight);
            shortestPathGC.clearRect(0, 0, canvasWidth, canvasHeight);
            lockClickCanvas.toBack();
        });


        //Listener for single clicks on mainCanvas
        mainCanvas.setOnMouseClicked(mouseEvent -> {
            int normX = (int) (mouseEvent.getX() / cellSideLength),
                    normY = (int) (mouseEvent.getY() / cellSideLength);

            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                grid.addGridElement(terrainType, normX, normY);

                //**** Might fail when grid has lots of cells ****
                mainGC.fillRect((normX * cellSideLength + 0.5f), (normY * cellSideLength + 0.5f),
                        cellSideLength - 1f, cellSideLength - 1f);
            }
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                int[] oldCoords = new int[2];
                if (nodeTypeToSelect == 0) {
                    oldCoords[0] = grid.getStartNode()[0];
                    oldCoords[1] = grid.getStartNode()[1];
                    grid.setStartNode(normX, normY);

                    //Remove existing start on grid
                    startNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f),
                            (oldCoords[1] * cellSideLength + 0.5f),
                            cellSideLength - 1f, cellSideLength - 1f);

                    //Add start
                    startNodeGC.fillText("O", (normX + (1 / 8f)) * cellSideLength,
                            (normY + (6 / 7f)) * cellSideLength);

                    //Change TextField in frontend
                    startXTF.setText(Integer.toString(normX));
                    startYTF.setText(Integer.toString(normY));
                } else {
                    oldCoords[0] = grid.getEndNode()[0];
                    oldCoords[1] = grid.getEndNode()[1];
                    //Add end
                    grid.setEndNode(normX, normY);

                    //Remove existing end on grid
                    endNodeGC.clearRect((oldCoords[0] * cellSideLength + 0.5f), (oldCoords[1] * cellSideLength + 0.5f),
                            cellSideLength - 1f, cellSideLength - 1f);

//                    endNodeGC.fillText("X", normX * cellSideLength + 0.5f + cellSideLength / 4f,
//                            normY * cellSideLength + cellSideLength - 1f);
                    endNodeGC.fillText("X", (normX + (1 / 8f)) * cellSideLength,
                            (normY + (6 / 7f)) * cellSideLength);

                    //Change TextField in frontend
                    endXTF.setText(Integer.toString(normX));
                    endYTF.setText(Integer.toString(normY));
                }

            }
        });

        //Method call needed for mouse dragging
        mainCanvas.setOnDragDetected(mouseEvent -> mainCanvas.startFullDrag());

        //Actual mouse dragging done in canvas
        mainCanvas.setOnMouseDragOver(mouseDragEvent -> {
            int normX = (int) (mouseDragEvent.getX() / cellSideLength),
                    normY = (int) (mouseDragEvent.getY() / cellSideLength);

            grid.addGridElement(terrainType, normX, normY);

            //**** Might fail when grid has lots of cells ****
            mainGC.fillRect((normX * cellSideLength + 0.5f),
                    (normY * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);
        });
    }
}
