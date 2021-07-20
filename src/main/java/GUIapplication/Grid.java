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
    public ArrayList<ArrayList<Integer>> innerArray;

    private GraphicsContext checkedNodesGC, shortestPathGC;
    private double cellSideLength;

    private int[] startNode, endNode;

    public Grid(GraphicsContext checkedNodesGC, GraphicsContext shortestPathGC, int x, int y) {
        if (x < 1 || y < 1)
            throw new IllegalArgumentException("Grid can't be of negative or zero dimension");
        this.checkedNodesGC = checkedNodesGC;
        this.shortestPathGC = shortestPathGC;

        startNode = new int[]{0, 0};
        endNode = new int[]{x - 1, y - 1};
        innerArray = new ArrayList<ArrayList<Integer>>(y);
        for (int i = 0; i < y; ++i) {
            innerArray.add(new ArrayList<Integer>(x));
            for (int j = 0; j < x; ++j)
                innerArray.get(i).add(1);
        }
    }

    public void setCellSideLength(double cellSideLength) {
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
     * Nodes names consist of the strings of structure : CoordX-CoordY
     * */
    public AdjList generateAdjList() {
        AdjList adj = new AdjList();
        for (int y = 0; y < innerArray.size(); ++y) {
            for (int x = 0; x < innerArray.get(0).size(); ++x) {
                String currentNode = x + "-" + y;
                if (innerArray.get(y).get(x) > 0) {
                    if (y - 1 > 0) {
                        if (x - 1 > 0 && innerArray.get(y - 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, (x - 1) + "-" + (y - 1), innerArray.get(y - 1).get(x - 1));
                        if (x + 1 < innerArray.get(0).size() && innerArray.get(y - 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, (x + 1) + "-" + (y - 1), innerArray.get(y - 1).get(x + 1));
                        if (innerArray.get(y - 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, x + "-" + (y - 1), innerArray.get(y - 1).get(x));
                    }
                    if (y + 1 < innerArray.size()) {
                        if (x - 1 > 0 && innerArray.get(y + 1).get(x - 1) != 0)
                            adj.addDirectedEdge(currentNode, (x - 1) + "-" + (y + 1), innerArray.get(y + 1).get(x - 1));
                        if (x + 1 < innerArray.get(0).size() && innerArray.get(y + 1).get(x + 1) != 0)
                            adj.addDirectedEdge(currentNode, (x + 1) + "-" + (y + 1), innerArray.get(y + 1).get(x + 1));
                        if (innerArray.get(y + 1).get(x) != 0)
                            adj.addDirectedEdge(currentNode, x + "-" + (y + 1), innerArray.get(y + 1).get(x));
                    }
                    if (x - 1 > 0 && innerArray.get(y).get(x - 1) != 0)
                        adj.addDirectedEdge(currentNode, (x - 1) + "-" + y, innerArray.get(y).get(x - 1));
                    if (x + 1 < innerArray.get(0).size() && innerArray.get(y).get(x + 1) != 0)
                        adj.addDirectedEdge(currentNode, (x + 1) + "-" + y, innerArray.get(y).get(x + 1));
                }
            }
        }
        return adj;
    }

    public int findPath(int pathfindingMethod) {
        switch (pathfindingMethod) {
            case 0:
                return PathFinding.dijkstraStepsGrid(generateAdjList(), startNode[0] + "-" + startNode[1],
                        endNode[0] + "-" + endNode[1], checkedNodesGC, shortestPathGC, cellSideLength);
        }
        return Integer.MAX_VALUE;
    }

    public void draw() {

    }
}
