package GUIapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import javafx.event.ActionEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowcaseController implements Initializable {
    @FXML
    private Canvas mainCanvas;
    @FXML
    private Button runButton, setCellCountButton;
    @FXML
    private TextField cellCount;
    @FXML
    private ToggleGroup terrainTypeToggleGroup, algorithmsToggleGroup, startEndSelectionTG;
    @FXML
    private RadioButton clearRB, obstacleRB, dijkstraRB, startRB, endRB;

    private GraphicsContext gc;
    private Grid grid;
    private int terrainType, algorithmUsed, cellSideLength, nodeTypeToSelect;

    private int drawGrid(int cellCount) {
        //No graphicsContext assigned to draw
        if (gc == null) {
            return 1;
        }
        //Clears canvas prior to drawing
        gc.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        //Variables for drawing
        //The width of the canvas dictates the size of the square grid
        double gridSideLength = mainCanvas.getWidth();
//                cellSideLength = gridSideLength / cellCount;

        for (int i = 0; i <= cellCount; ++i) {
            gc.strokeLine(i * cellSideLength, 0, i * cellSideLength, gridSideLength);
            gc.strokeLine(0, i * cellSideLength, gridSideLength, i * cellSideLength);
        }

        return 0;
    }

    private void drawStartEndNodes(int startX, int startY, int oldStartX, int oldStartY, int endX, int endY, int oldEndX
            , int oldEndY) {

        //Keep Graphics context original color
        Paint originalColor = gc.getFill();

        int coordX = startX, coordY = startY, oldCoordX = oldStartX, oldCoordY = oldStartY;
        String mark = "O";
        for (int i = 0; i < 2; ++i) {
            if (grid.innerGrid.get(oldCoordY).get(oldCoordX) == 0) {
                gc.setFill(Color.BLACK);
            } else if (grid.innerGrid.get(oldCoordY).get(oldCoordX) == 1) {//Get color in old marked cell
                gc.setFill(Color.WHITE);
            }

            //Remove existing start/end on grid
            gc.fillRect((oldCoordX * cellSideLength + 0.5f), (oldCoordY * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);


            gc.setFill(Color.RED);
            gc.fillText(mark, coordX * cellSideLength + 0.5f + cellSideLength / 4f,
                    coordY * cellSideLength + cellSideLength - 1f);

            coordX = endX;
            coordY = endY;
            oldCoordX = oldEndX;
            oldCoordY = oldEndY;
            mark = "X";
        }
        gc.setFill(originalColor); //Return gc fill to it's original color
    }

    @FXML
    public void handleSetCellCountButtonAction(ActionEvent event) {
        drawGrid(Integer.parseInt(cellCount.getText()));
        grid.resize(Integer.parseInt(cellCount.getText()), Integer.parseInt(cellCount.getText()));
    }

    @FXML
    public void handleRunButtonAction(ActionEvent event) {
        switch (algorithmUsed) {
            case 0:

                break;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializing FXML variables for drawing with initial values
        gc = mainCanvas.getGraphicsContext2D();
        cellSideLength = (int) mainCanvas.getWidth() / Integer.parseInt(cellCount.getText());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", cellSideLength));
        grid = new Grid(gc, Integer.parseInt(cellCount.getText()), Integer.parseInt(cellCount.getText()));


        //First Grid draw with default values
        drawGrid(Integer.parseInt(cellCount.getText()));

        //Draw default start and end nodes
        {
            int[] startCoords = grid.getStartNode(),
                    endCoords = grid.getEndNode();

            gc.setFill(Color.RED);
            gc.fillText("O", startCoords[0] * cellSideLength + 0.5f + cellSideLength / 4f,
                    startCoords[1] * cellSideLength + cellSideLength - 1f);
            gc.fillText("X", endCoords[0] * cellSideLength + 0.5f + cellSideLength / 4f,
                    endCoords[1] * cellSideLength + cellSideLength - 1f);
            gc.setFill(Color.BLACK);
        }

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
                        terrainType = Integer.parseInt((String) terrainTypeToggleGroup.getSelectedToggle().getUserData());
                        if (terrainType == 1)
                            gc.setFill(Color.WHITE);
                        else
                            gc.setFill(Color.BLACK);
                    }
                }
        );

        //Listens for changes in algorithmsToggleGroup
        algorithmsToggleGroup.selectedToggleProperty().addListener(
                (observableValue, toggle, t1) -> {
                    if (algorithmsToggleGroup.getSelectedToggle() != null)
                        algorithmUsed = Integer.parseInt((String) algorithmsToggleGroup.getSelectedToggle().getUserData());
                }
        );

        //Listens for changes in startEndSelectionTG
        startEndSelectionTG.selectedToggleProperty().addListener(
                (observable, toggle, t1) -> {
                    if (startEndSelectionTG.getSelectedToggle() != null) {
                        nodeTypeToSelect = Integer.parseInt((String) startEndSelectionTG.getSelectedToggle().getUserData());
                    }
                }
        );

        //Listener for single clicks on canvas
        mainCanvas.setOnMouseClicked(mouseEvent -> {
            int normX = (int) mouseEvent.getX() / cellSideLength,
                    normY = (int) mouseEvent.getY() / cellSideLength;

            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                grid.addGridElement(terrainType, normX, normY);

                //**** Might fail when grid has lots of cells ****
                gc.fillRect((normX * cellSideLength + 0.5f), (normY * cellSideLength + 0.5f),
                        cellSideLength - 1f, cellSideLength - 1f);

                //Redraw start and end marks
                drawStartEndNodes(grid.getStartNode()[0], grid.getStartNode()[1], grid.getStartNode()[0],
                        grid.getStartNode()[1],
                        grid.getEndNode()[0], grid.getEndNode()[1], grid.getEndNode()[0], grid.getEndNode()[1]);

            }
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                int[] oldCoords = new int[2];
                String mark;
                if (nodeTypeToSelect == 0) {
                    oldCoords[0] = grid.getStartNode()[0];
                    oldCoords[1] = grid.getStartNode()[1];
                    mark = "O";
                    grid.setStartNode(normX, normY);
                    drawStartEndNodes(normX, normY, oldCoords[0], oldCoords[1],
                            grid.getEndNode()[0], grid.getEndNode()[1], grid.getEndNode()[0], grid.getEndNode()[1]);
                } else {
                    oldCoords[0] = grid.getEndNode()[0];
                    oldCoords[1] = grid.getEndNode()[1];
                    mark = "X";
                    grid.setEndNode(normX, normY);
                    drawStartEndNodes(grid.getStartNode()[0], grid.getStartNode()[1], grid.getStartNode()[0],
                            grid.getStartNode()[1], normX, normY, oldCoords[0], oldCoords[1]);
                }

            }
        });

        //Method call needed for mouse dragging
        mainCanvas.setOnDragDetected(mouseEvent -> mainCanvas.startFullDrag());

        //Actual mouse dragging done in canvas
        mainCanvas.setOnMouseDragOver(mouseDragEvent -> {
            int normX = (int) mouseDragEvent.getX() / cellSideLength,
                    normY = (int) mouseDragEvent.getY() / cellSideLength;

            grid.addGridElement(terrainType, normX, normY);

            //**** Might fail when grid has lots of cells ****
            gc.fillRect((normX * cellSideLength + 0.5f),
                    (normY * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);
            //Redraw start and end marks
            drawStartEndNodes(grid.getStartNode()[0], grid.getStartNode()[1], grid.getStartNode()[0],
                    grid.getStartNode()[1],
                    grid.getEndNode()[0], grid.getEndNode()[1], grid.getEndNode()[0], grid.getEndNode()[1]);
        });
    }
}
