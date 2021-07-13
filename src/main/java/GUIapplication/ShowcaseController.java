package GUIapplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javafx.event.ActionEvent;

import javax.swing.*;
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
    private ToggleGroup terrainTypeToggleGroup, algorithmsToggleGroup;
    @FXML
    private RadioButton clearRB, obstacleRB, dijkstraRB;

    private GraphicsContext gc;
    private Grid grid;
    private int terrainType, algorithmUsed, cellSideLength;

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

    @FXML
    public void handleSetCellCountButtonAction(ActionEvent event) {
        drawGrid(Integer.parseInt(cellCount.getText()));
        grid.resize(Integer.parseInt(cellCount.getText()), Integer.parseInt(cellCount.getText()));
        System.out.println();

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
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.setFill(Color.BLACK);
        grid = new Grid(gc, Integer.parseInt(cellCount.getText()), Integer.parseInt(cellCount.getText()));

        cellSideLength = (int) mainCanvas.getWidth() / Integer.parseInt(cellCount.getText());

        //Default Grid draw
        drawGrid(Integer.parseInt(cellCount.getText()));

        //terrainToggleButtons configuration
        clearRB.setUserData("1");
        obstacleRB.setUserData("0");

        terrainType = Integer.parseInt((String) terrainTypeToggleGroup.getSelectedToggle().getUserData());

        //Algorithm to be used configuration
        dijkstraRB.setUserData("0");

        algorithmUsed = Integer.parseInt((String) algorithmsToggleGroup.getSelectedToggle().getUserData());


        //Listens to changes in grid drawing selection/deselection radio buttons
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
        //Listens to changes in algorithmsToggleGroup
        algorithmsToggleGroup.selectedToggleProperty().addListener(
                (observableValue, toggle, t1) -> {
                    if (algorithmsToggleGroup.getSelectedToggle() != null) {
                        algorithmUsed = Integer.parseInt((String) algorithmsToggleGroup.getSelectedToggle().getUserData());
                    }
                }
        );


        //Method call needed for mouse dragging
        mainCanvas.setOnDragDetected(mouseEvent -> mainCanvas.startFullDrag());

        //Actual mouse dragging done in canvas
        mainCanvas.setOnMouseDragOver(mouseDragEvent -> {
            int normX, normY;

            normX = (int) mouseDragEvent.getX() / cellSideLength;
            normY = (int) mouseDragEvent.getY() / cellSideLength;

            grid.addGridElement(terrainType, normX, normY);

            //**** Might fail when grid has lots of cells ****
            gc.fillRect((normX * cellSideLength + 0.5f),
                    (normY * cellSideLength + 0.5f),
                    cellSideLength - 1f, cellSideLength - 1f);
        });
    }
}
