package com.jjac.pathfindinggui;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class AdjListTest {

    @Test
    void addNode() {
        AdjList<String> list = new AdjList<>(String.class);
        list.addNode("NODE1");
        list.addNode("NODE2");

        LinkedList<Pair<Node<Integer, String>, Integer>> expectedNeighbors = new LinkedList<>();

        assertEquals(expectedNeighbors, list.getNeighbors("NODE1"));
        assertEquals(expectedNeighbors, list.getNeighbors("NODE2"));
        assertNull(list.getNeighbors("NODE3"));


    }

    @Test
    void addDirectedEdge() {
        AdjList<String> list = new AdjList<>(String.class);
        list.addDirectedEdge("NODE1", "NODE2", 12);
        list.addDirectedEdge("NODE1", "NODE3", -999);
        list.addDirectedEdge("NODE3", "NODE1", 78);
        list.addDirectedEdge("NODE3", "NODE5", 1111);
        list.addDirectedEdge("NODE3", "NODE2", 25);
        list.addDirectedEdge("NODE2", "NODE4", 0);
        list.addDirectedEdge("NODE4", "NODE4", 1);

        assertEquals(list.getNeighbors("NODE1").get(0).getKey().value, "NODE2");
        assertEquals(list.getNeighbors("NODE1").get(0).getValue(), 12);
        assertEquals(list.getNeighbors("NODE1").get(1).getKey().value, "NODE3");
        assertEquals(list.getNeighbors("NODE1").get(1).getValue(), -999);

        assertEquals(list.getNeighbors("NODE3").get(0).getKey().value, "NODE1");
        assertEquals(list.getNeighbors("NODE3").get(0).getValue(), 78);
        assertEquals(list.getNeighbors("NODE3").get(1).getKey().value, "NODE5");
        assertEquals(list.getNeighbors("NODE3").get(1).getValue(), 1111);
        assertEquals(list.getNeighbors("NODE3").get(2).getKey().value, "NODE2");
        assertEquals(list.getNeighbors("NODE3").get(2).getValue(), 25);

        assertEquals(list.getNeighbors("NODE2").get(0).getKey().value, "NODE4");
        assertEquals(list.getNeighbors("NODE2").get(0).getValue(), 0);

        assertEquals(list.getNeighbors("NODE4").get(0).getKey().value, "NODE4");
        assertEquals(list.getNeighbors("NODE4").get(0).getValue(), 1);
    }

    @Test
    void addBidirectionalEdge() {
        AdjList<String> list = new AdjList<>(String.class);
        list.addBidirectionalEdge("NODE1", "NODE2", 2);
        list.addDirectedEdge("NODE2", "NODE3", 0);
        list.addBidirectionalEdge("NODE3", "NODE1", 78);
        list.addBidirectionalEdge("NODE4", "NODE1", -1);

        assertEquals(list.getNeighbors("NODE1").get(0).getKey().value, "NODE2");
        assertEquals(list.getNeighbors("NODE1").get(0).getValue(), 2);
        assertEquals(list.getNeighbors("NODE1").get(1).getKey().value, "NODE3");
        assertEquals(list.getNeighbors("NODE1").get(1).getValue(), 78);
        assertEquals(list.getNeighbors("NODE1").get(2).getKey().value, "NODE4");
        assertEquals(list.getNeighbors("NODE1").get(2).getValue(), -1);

        assertEquals(list.getNeighbors("NODE2").get(0).getKey().value, "NODE1");
        assertEquals(list.getNeighbors("NODE2").get(0).getValue(), 2);
        assertEquals(list.getNeighbors("NODE2").get(1).getKey().value, "NODE3");
        assertEquals(list.getNeighbors("NODE2").get(1).getValue(), 0);

        assertEquals(list.getNeighbors("NODE3").get(0).getKey().value, "NODE1");
        assertEquals(list.getNeighbors("NODE3").get(0).getValue(), 78);

        assertEquals(list.getNeighbors("NODE4").get(0).getKey().value, "NODE1");
        assertEquals(list.getNeighbors("NODE4").get(0).getValue(), -1);
    }

    @Test
    void readAndAddBufferedReader() {
        AdjList<String> list = new AdjList<>(String.class);
        IOException thrownIO;
        NumberFormatException thrownNFE;

        //Test edgeList with invalid strings
        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader(""))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("a\na"))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("asd 567 870\najg iton 5657 123"))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("      \n     \n    \n    \n "))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        //Partially valid string
        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("   str1  str2 465      \nstr3 str4 876   \n tr1 tr3  "))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        //Invalid integers for edge weight
        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("   str1  str2 99887      \nstr3 str4 143g   \n tr1 tr3  0980 "))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("   str1  str2 99887      \nstr3 str4 143.56"))),
                "Not expected exception thrown");
        assertTrue(list.isEmpty());

        //Valid edge lists
        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("str1  str2 998      \nstr3 str4 000176"))),
                "Not expected exception thrown");
        assertFalse(list.isEmpty());

        assertDoesNotThrow(() -> list.readAndAddBufferedReader(new BufferedReader(new StringReader("     str1  str2 " +
                        "911     \n     str3      str4    567\n"))),
                "Not expected exception thrown");
        assertFalse(list.isEmpty());
    }
}