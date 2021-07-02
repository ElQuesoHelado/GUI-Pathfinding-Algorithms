package main.java;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Grid {
    public ArrayList<ArrayList<Integer>> innerGrid;

    private GraphicsContext gc;

    public Grid(GraphicsContext _gc, int x, int y) {
        if (x < 1 || y < 1)
            throw new IllegalArgumentException("Grid can't be of negative dimensions");
        gc = _gc;
        innerGrid = new ArrayList<ArrayList<Integer>>(y);
        for (int i = 0; i < y; ++i) {
            innerGrid.add(new ArrayList<Integer>(x));
            for (int j = 0; j < x; ++j)
                innerGrid.get(i).add(0);
        }
    }

    /*
     * Adds element to innerGrid in position x,y
     */
    public void addGridElement(int value, int x, int y) {
//        innerGrid[y][x] = value;
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
                    innerGrid.get(i).add(0);
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
                    innerGrid.get(i).add(0);
            }
        }
    }

    /*
    * Zeroes the entire array
    * */
    public boolean clearGrid() {
        if (innerGrid.size() < 1 || innerGrid.get(0).size() < 1)
            return false;
        for (int i = 0; i < innerGrid.size(); ++i) {
            for (int j = 0; j < innerGrid.get(0).size(); ++j)
                innerGrid.get(i).set(j, 0);
        }
        return true;
    }

    public void draw() {

    }
}
