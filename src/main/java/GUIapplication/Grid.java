package GUIapplication;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;


/*
 * Grid class
 * Contains an innerGrid array that represents the drawn grid
 * Values of 0 represent an impassable cell
 * Values greater or equal to 1 represent a passable cell
 * */
public class Grid {
    public ArrayList<ArrayList<Integer>> innerGrid;

    private GraphicsContext gc;
    private int pathfindingMethod = 0;
    private int cellSideLength;

    //    private String startNode = "0-0", endNode = "0-0";
    private int[] startNode, endNode;

    public Grid(GraphicsContext _gc, int x, int y) {
        if (x < 1 || y < 1)
            throw new IllegalArgumentException("Grid can't be of negative dimensions");
        gc = _gc;
//        startNode = new int[2]{0};
        startNode = new int[]{0, 0};
        endNode = new int[]{x - 1, y - 1};
        innerGrid = new ArrayList<ArrayList<Integer>>(y);
        for (int i = 0; i < y; ++i) {
            innerGrid.add(new ArrayList<Integer>(x));
            for (int j = 0; j < x; ++j)
                innerGrid.get(i).add(1);
        }
    }

    public void setCellSideLength(int cellSideLength) {
        this.cellSideLength = cellSideLength;
    }

    public void setStartNode(int startNodeX, int startNodeY) {
        this.startNode[0] = startNodeX;
        this.startNode[1] = startNodeY;
    }

    public void setEndNode(int endNodeX, int endNodeY) {
        this.endNode[0] = endNodeX;
        this.endNode[1] = endNodeY;
    }

    public int[] getStartNode() {
        return startNode;
    }

    public int[] getEndNode() {
        return endNode;
    }

    public void setPathfindingMethod(int pathfindingMethod) {
        this.pathfindingMethod = pathfindingMethod;
    }

    /*
     * Adds element to innerGrid in position x,y
     */
    public void addGridElement(int value, int x, int y) {
        innerGrid.get(y).set(x, value);
    }

    /*
     * Resizes array and keeps already existing elements
     */
    public void resize(int x, int y) {
        if (x < 1 || y < 1)
            throw new IllegalArgumentException("Grid can't be of negative dimensions");

        int initialX = innerGrid.get(0).size(),
                initialY = innerGrid.size(), rowsToResizeIndexY;

        // Shrinking in height/y
        if (initialY > y) {
            innerGrid.subList(y, initialY).clear();
            innerGrid.trimToSize();
            rowsToResizeIndexY = y;
        } else {// Expanding & same size , uses new width to create new rows
            innerGrid.ensureCapacity(y);
            for (int i = initialY; i < y; i++) {
                innerGrid.add(new ArrayList<Integer>(x));
                for (int j = 0; j < x; ++j)
                    innerGrid.get(i).add(1);
            }
            rowsToResizeIndexY = initialY;
        }

        if (initialX == x)
            rowsToResizeIndexY = 0;

        //Shrinking in width/x
        if (initialX > x) {
            for (int i = 0; i < rowsToResizeIndexY; i++) {
                innerGrid.get(i).subList(x, initialX).clear();
                innerGrid.trimToSize();
            }
        } else { //Expanding each row
            for (int i = 0; i < rowsToResizeIndexY; i++) {
                innerGrid.get(i).ensureCapacity(x);
                for (int j = 0; j < x - initialX; ++j)
                    innerGrid.get(i).add(1);
            }
        }
    }

    /*
     * Fills entire array with default value : 1
     * */
    public boolean clearGrid() {
        if (innerGrid.size() < 1 || innerGrid.get(0).size() < 1)
            return false;
        for (int i = 0; i < innerGrid.size(); ++i) {
            for (int j = 0; j < innerGrid.get(0).size(); ++j)
                innerGrid.get(i).set(j, 1);
        }
        return true;
    }

    /*
     * Generates an Adjacency list from current innerGrid where
     * grid[x][y] == 0 -> Non passable obstacle
     * grid[x][y] == 1+ -> Normal path
     * Nodes names consist of the strings of structure : CoordX-CoordY
     * */
    public AdjList generateAdjList() {
        AdjList adj = new AdjList();
        for (int y = 0; y < innerGrid.size(); ++y) {
            for (int x = 0; x < innerGrid.get(0).size(); ++x) {
                String currentNode = x + "-" + y;
                if (innerGrid.get(y).get(x) > 0) {
                    if (y - 1 > 0) {
                        if (x - 1 > 0 && innerGrid.get(y - 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, (x - 1) + "-" + (y - 1), innerGrid.get(y - 1).get(x - 1));
                        if (x + 1 < innerGrid.get(0).size() && innerGrid.get(y - 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, (x + 1) + "-" + (y - 1), innerGrid.get(y - 1).get(x + 1));
                        if (innerGrid.get(y - 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, x + "-" + (y - 1), innerGrid.get(y - 1).get(x));
                    }
                    if (y + 1 < innerGrid.size()) {
                        if (x - 1 > 0 && innerGrid.get(y + 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, (x - 1) + "-" + (y + 1), innerGrid.get(y + 1).get(x - 1));
                        if (x + 1 < innerGrid.get(0).size() && innerGrid.get(y + 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, (x + 1) + "-" + (y + 1), innerGrid.get(y + 1).get(x + 1));
                        if (innerGrid.get(y + 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, x + "-" + (y + 1), innerGrid.get(y + 1).get(x));
                    }
                    if (x - 1 > 0 && innerGrid.get(y).get(x - 1) != 0)
                        adj.addDirectedEdge(currentNode, (x - 1) + "-" + y, innerGrid.get(y).get(x - 1));
                    if (x + 1 < innerGrid.get(0).size() && innerGrid.get(y).get(x + 1) != 0)
                        adj.addDirectedEdge(currentNode, (x + 1) + "-" + y, innerGrid.get(y).get(x + 1));
                }
            }
        }
        return adj;
    }

    public int findPath() {
        switch (pathfindingMethod) {
            case 0:
                return PathFinding.dijkstraStepsGrid(generateAdjList(), startNode[0] + "-" + startNode[1],
                        endNode[0] + "-" + endNode[1], gc, cellSideLength);
        }
        return Integer.MAX_VALUE;
    }

    public void draw() {

    }
}
