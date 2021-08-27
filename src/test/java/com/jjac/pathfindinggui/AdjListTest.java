package com.jjac.pathfindinggui;

import javafx.util.Pair;
//import GUIapplication.AdjList;
import org.junit.jupiter.api .Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class AdjListTest {

    @Test
    void addNode() {
        AdjList list = new AdjList();
        list.addNode("NODE1");
        list.addNode("NODE2");

        Pair<String, Integer>[] expectedNeighbors = new Pair[0];

        assertArrayEquals(expectedNeighbors, list.getNeighbors("NODE1"));
        assertArrayEquals(expectedNeighbors, list.getNeighbors("NODE2"));
        assertArrayEquals(null, list.getNeighbors("NODE3"));
    }

    @Test
    void addDirectedEdge() {
        AdjList list = new AdjList();
        list.addDirectedEdge("NODE1", "NODE2", 12);
        list.addDirectedEdge("NODE1", "NODE3", -999);
        list.addDirectedEdge("NODE3", "NODE1", 78);
        list.addDirectedEdge("NODE3", "NODE5", 1111);
        list.addDirectedEdge("NODE3", "NODE2", 25);
        list.addDirectedEdge("NODE2", "NODE4", 0);
        list.addDirectedEdge("NODE4", "NODE4", 1);

        Pair[] expNghNode1 = {new Pair("NODE2", 12), new Pair("NODE3", -999)};
        Pair[] expNghNode2 = {new Pair("NODE4", 0)};
        Pair[] expNghNode3 = {new Pair("NODE1", 78), new Pair("NODE5", 1111), new Pair("NODE2", 25)};
        Pair[] expNghNode4 = {new Pair("NODE4", 1)};

        assertArrayEquals(expNghNode1, list.getNeighbors("NODE1"));
        assertArrayEquals(expNghNode2, list.getNeighbors("NODE2"));
        assertArrayEquals(expNghNode3, list.getNeighbors("NODE3"));
        assertArrayEquals(expNghNode4, list.getNeighbors("NODE4"));
        assertArrayEquals(new Pair[0], list.getNeighbors("NODE5"));
    }

    @Test
    void addBidirectionalEdge() {
        AdjList list = new AdjList();
        list.addBidirectionalEdge("NODE1", "NODE2", 2);
        list.addDirectedEdge("NODE2", "NODE3", 0);
        list.addBidirectionalEdge("NODE3", "NODE1", 78);
        list.addBidirectionalEdge("NODE4", "NODE1", -1);

        Pair[] expNghNode1 = {new Pair("NODE2", 2), new Pair("NODE3", 78), new Pair("NODE4", -1)};
        Pair[] expNghNode2 = {new Pair("NODE1", 2), new Pair("NODE3", 0)};
        Pair[] expNghNode3 = {new Pair("NODE1", 78)};
        Pair[] expNghNode4 = {new Pair("NODE1", -1)};

        assertArrayEquals(expNghNode1, list.getNeighbors("NODE1"));
        assertArrayEquals(expNghNode2, list.getNeighbors("NODE2"));
        assertArrayEquals(expNghNode3, list.getNeighbors("NODE3"));
        assertArrayEquals(expNghNode4, list.getNeighbors("NODE4"));
    }

    @Test
    void readAndAddBufferedReader() {
        AdjList list = new AdjList();
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