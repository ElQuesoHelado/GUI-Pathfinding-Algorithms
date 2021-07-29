package GUIapplication;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;


/*
 * Grid class
 * Contains an innerGrid array that represents the drawn grid
 * Values of 0 represent an impassable cell
 * Values greater or equal to 1 represent a passable cell
 * Start and end nodes have both X and Y coordinates compressed inside an integer
 * */
public class Grid {
    public ArrayList<ArrayList<Integer>> innerArray;

    private GraphicsContext checkedNodesGC, shortestPathGC;
    private double cellSideLength;

    private int startNode, endNode;

    public Grid(GraphicsContext checkedNodesGC, GraphicsContext shortestPathGC, int width, int height) {
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("Grid can't be of negative or zero dimension");
        this.checkedNodesGC = checkedNodesGC;
        this.shortestPathGC = shortestPathGC;

        startNode = 0;
        endNode = ((width - 1) << 16) | (height - 1);
        cellSideLength = 10;
        innerArray = new ArrayList<ArrayList<Integer>>(height);
        for (int i = 0; i < height; ++i) {
            innerArray.add(new ArrayList<Integer>(width));
            for (int j = 0; j < width; ++j)
                innerArray.get(i).add(1);
        }
    }

    public void setCellSideLength(double cellSideLength) {
        this.cellSideLength = cellSideLength;
    }

    public void setStartNode(int startNodeX, int startNodeY) {
        startNode = startNodeX << 16 | startNodeY;
    }

    public void setEndNode(int endNodeX, int endNodeY) {
        endNode = endNodeX << 16 | endNodeY;
    }

    public int[] getStartNode() {
        return new int[]{startNode >> 16, startNode & 0x0000FFFF};
    }

    public int[] getEndNode() {
        return new int[]{endNode >> 16, endNode & 0x0000FFFF};
    }

    /*
     * Adds element to innerGrid in position x,y
     */
    public void addGridElement(int value, int x, int y) {
        innerArray.get(y).set(x, value);
    }

    /*
     * Resizes array and keeps already existing elements
     */
    public void resize(int x, int y) {
        if (x < 1 || y < 1)
            throw new IllegalArgumentException("Grid can't be of negative dimensions");

        int initialX = innerArray.get(0).size(),
                initialY = innerArray.size(), rowsToResizeIndexY;

        // Shrinking in height/y
        if (initialY > y) {
            innerArray.subList(y, initialY).clear();
            innerArray.trimToSize();
            rowsToResizeIndexY = y;
        } else {// Expanding & same size , uses new width to create new rows
            innerArray.ensureCapacity(y);
            for (int i = initialY; i < y; i++) {
                innerArray.add(new ArrayList<Integer>(x));
                for (int j = 0; j < x; ++j)
                    innerArray.get(i).add(1);
            }
            rowsToResizeIndexY = initialY;
        }

        if (initialX == x)
            rowsToResizeIndexY = 0;

        //Shrinking in width/x
        if (initialX > x) {
            for (int i = 0; i < rowsToResizeIndexY; i++) {
                innerArray.get(i).subList(x, initialX).clear();
                innerArray.trimToSize();
            }
        } else { //Expanding each row
            for (int i = 0; i < rowsToResizeIndexY; i++) {
                innerArray.get(i).ensureCapacity(x);
                for (int j = 0; j < x - initialX; ++j)
                    innerArray.get(i).add(1);
            }
        }
    }

    /*
     * Fills entire array with default value : 1
     * */
    public boolean clearGrid() {
        if (innerArray.size() < 1 || innerArray.get(0).size() < 1)
            return false;
        for (int i = 0; i < innerArray.size(); ++i) {
            for (int j = 0; j < innerArray.get(0).size(); ++j)
                innerArray.get(i).set(j, 1);
        }
        return true;
    }

    /*
     * Generates an Adjacency list from current innerGrid where
     * grid[x][y] == 0 -> Non passable obstacle
     * grid[x][y] == 1+ -> Normal path
     * Nodes names consist of an integer where the first 2 bytes represent the X coordinate and the last ones the Y
     * */
    public AdjList generateAdjList() {
        AdjList adj = new AdjList();
        for (int y = 0; y < innerArray.size(); ++y) {
            for (int x = 0; x < innerArray.get(0).size(); ++x) {
                String currentNode = Integer.toString(x << 16 | y);
                if (innerArray.get(y).get(x) > 0) {
                    if (y - 1 > 0) {
                        if (x - 1 > 0 && innerArray.get(y - 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString((x - 1) << 16 | (y - 1)),
                                    innerArray.get(y - 1).get(x - 1));
                        if (x + 1 < innerArray.get(0).size() && innerArray.get(y - 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString((x + 1) << 16 | (y - 1)),
                                    innerArray.get(y - 1).get(x + 1));
                        if (innerArray.get(y - 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString(x << 16 | (y - 1)),
                                    innerArray.get(y - 1).get(x));
                    }
                    if (y + 1 < innerArray.size()) {
                        if (x - 1 > 0 && innerArray.get(y + 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString((x - 1) << 16 | (y + 1)),
                                    innerArray.get(y + 1).get(x - 1));
                        if (x + 1 < innerArray.get(0).size() && innerArray.get(y + 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString((x + 1) << 16 | (y + 1)),
                                    innerArray.get(y + 1).get(x + 1));
                        if (innerArray.get(y + 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, Integer.toString(x << 16 | (y + 1)),
                                    innerArray.get(y + 1).get(x));
                    }
                    if (x - 1 > 0 && innerArray.get(y).get(x - 1) != 0)
                        adj.addDirectedEdge(currentNode, Integer.toString((x - 1) << 16 | y),
                                innerArray.get(y).get(x - 1));
                    if (x + 1 < innerArray.get(0).size() && innerArray.get(y).get(x + 1) != 0)
                        adj.addDirectedEdge(currentNode, Integer.toString((x + 1) << 16 | y),
                                innerArray.get(y).get(x + 1));
                }
            }
        }
        return adj;
    }
}
