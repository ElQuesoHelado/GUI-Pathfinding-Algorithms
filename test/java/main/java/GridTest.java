package main.java;

import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    private GraphicsContext gc;

    private ArrayList<ArrayList<Integer>> getZeroedArrayList(int width, int height) {
        ArrayList<ArrayList<Integer>> array = new ArrayList<ArrayList<Integer>>(height);
        for (int i = 0; i < height; ++i) {
            array.add(new ArrayList<Integer>(width));
            for (int j = 0; j < width; ++j)
                array.get(i).add(0);
        }
        return array;
    }

    @Test
    void addGridElement() {
        Grid grid = new Grid(gc, 4, 7);

        ArrayList<ArrayList<Integer>> array = new ArrayList<>(7);

        array.add(new ArrayList<>(Arrays.asList(0, 1, 1, 0)));
        array.add(new ArrayList<>(Arrays.asList(1, 0, 0, 0)));
        array.add(new ArrayList<>(Arrays.asList(0, 0, 1, 1)));
        array.add(new ArrayList<>(Arrays.asList(1, 1, 1, 1)));
        array.add(new ArrayList<>(Arrays.asList(0, 0, 0, 0)));
        array.add(new ArrayList<>(Arrays.asList(1, 1, 0, 0)));
        array.add(new ArrayList<>(Arrays.asList(0, 1, 0, 1)));

        grid.addGridElement(1, 1, 0);
        grid.addGridElement(1, 2, 0);
        grid.addGridElement(1, 0, 1);
        grid.addGridElement(1, 2, 2);
        grid.addGridElement(1, 3, 2);
        grid.addGridElement(1, 0, 3);
        grid.addGridElement(1, 1, 3);
        grid.addGridElement(1, 2, 3);
        grid.addGridElement(1, 3, 3);
        grid.addGridElement(1, 0, 5);
        grid.addGridElement(1, 1, 5);
        grid.addGridElement(1, 1, 6);
        grid.addGridElement(1, 3, 6);

        assertEquals(array, grid.innerGrid);
    }

    @Test
    void resize() {
        //Testing shrinking
        Grid grid = new Grid(gc, 16, 5);
        grid.addGridElement(1, 0, 0);
        grid.addGridElement(1, 15, 4);
        grid.addGridElement(1, 8, 0);
        grid.addGridElement(1, 8, 4);
        grid.addGridElement(1, 8, 2);
        grid.addGridElement(1, 3, 0);
        grid.addGridElement(1, 13, 2);
        grid.addGridElement(1, 13, 0);
        grid.addGridElement(1, 11, 2);
        grid.addGridElement(1, 2, 0);
        grid.addGridElement(1, 1, 1);


        ArrayList<ArrayList<Integer>> array = getZeroedArrayList(10, 2);
        array.get(0).set(0, 1);
        array.get(0).set(8, 1);
        array.get(0).set(3, 1);
        array.get(0).set(2, 1);
        array.get(1).set(1, 1);

        grid.resize(10, 2);

        assertEquals(array, grid.innerGrid);

        //Testing expansion
        array = getZeroedArrayList(10, 17);
        array.get(0).set(0, 1);
        array.get(0).set(8, 1);
        array.get(0).set(3, 1);
        array.get(0).set(2, 1);
        array.get(1).set(1, 1);

        array.get(16).set(9, 1);
        array.get(16).set(0, 1);
        array.get(16).set(5, 1);
        array.get(13).set(2, 1);
        array.get(12).set(4, 1);
        array.get(11).set(0, 1);
        array.get(11).set(6, 1);

        grid.resize(10, 17);

        grid.addGridElement(1, 9, 16);
        grid.addGridElement(1, 0, 16);
        grid.addGridElement(1, 5, 16);
        grid.addGridElement(1, 2, 13);
        grid.addGridElement(1, 4, 12);
        grid.addGridElement(1, 0, 11);
        grid.addGridElement(1, 6, 11);

        assertEquals(array, grid.innerGrid);

        grid.resize(10, 17);

        array.get(5).set(1, 1);
        grid.addGridElement(1, 1, 5);

        assertEquals(array, grid.innerGrid);

        grid.resize(25, 17);

        array = getZeroedArrayList(25, 17);
        array.get(0).set(0, 1);
        array.get(0).set(8, 1);
        array.get(0).set(3, 1);
        array.get(0).set(2, 1);
        array.get(1).set(1, 1);

        array.get(16).set(9, 1);
        array.get(16).set(0, 1);
        array.get(16).set(5, 1);
        array.get(13).set(2, 1);
        array.get(12).set(4, 1);
        array.get(11).set(0, 1);
        array.get(11).set(6, 1);

        array.get(5).set(24, 1);
        array.get(16).set(21, 1);
        array.get(10).set(10, 1);
        array.get(0).set(21, 1);
        array.get(5).set(1, 1);

        grid.addGridElement(1, 24, 5);
        grid.addGridElement(1, 21, 16);
        grid.addGridElement(1, 10, 10);
        grid.addGridElement(1, 21, 0);

        assertEquals(array, grid.innerGrid);
    }

    @Test
    void clearGrid() {
        Grid grid = new Grid(gc, 20, 20);

        grid.addGridElement(1, 0, 2);
        grid.addGridElement(1, 13, 2);
        grid.addGridElement(1, 14, 19);
        grid.addGridElement(0, 0, 2);
        grid.addGridElement(1, 7, 2);
        grid.addGridElement(1, 0, 0);

        grid.clearGrid();

        ArrayList<ArrayList<Integer>> array = getZeroedArrayList(20, 20);

        assertEquals(array, grid.innerGrid);

        grid = new Grid(gc, 10, 80);
        grid.addGridElement(1, 0, 0);
        grid.addGridElement(1, 2, 2);
        grid.addGridElement(1, 4, 19);
        grid.addGridElement(1, 9, 79);
        grid.addGridElement(1, 9, 0);
        grid.addGridElement(1, 0, 79);
        grid.addGridElement(0, 4, 19);
        grid.addGridElement(0, 9, 0);

        grid.clearGrid();

        array = getZeroedArrayList(10, 80);

        assertEquals(array, grid.innerGrid);
    }

    @Test
    void draw() {
    }
}