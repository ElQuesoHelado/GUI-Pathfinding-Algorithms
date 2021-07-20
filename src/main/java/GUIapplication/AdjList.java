package GUIapplication;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*
 * Adjacency List class
 * Node values are stored as string keys in a hashmap
 * each value is a LinkedList containing Pair<String, Integer> neighbors
 * where string = neighbor node value, Integer = edge value
 * */
public class AdjList {
    private Map<String, LinkedList<Pair<String, Integer>>> adj;
    private boolean isWeighted = false, hasNegativeEdges = false;

    AdjList() {
        adj = new HashMap<>();
    }

    /*
     * Method for checking if the Adjacency list is empty
     * */
    public boolean isEmpty() {
        return adj.isEmpty();
    }

    /*
     * Method for checking if graph is weighted
     * */
    public boolean isWeighted() {
        return isWeighted;
    }

    /*
     * Method for checking if graph has at least one negative edge
     * */
    public boolean hasNegativeEdges() {
        return hasNegativeEdges;
    }

    /*
     * Clears adjacency list
     * */
    public void emptyList() {
        adj = new HashMap<>();
    }

    /*
     * Returns adjacency list size
     * */
    public int size() {
        return adj.size();
    }

    /*
     * Get HashMap keySet
     * */
    public Set<String> getKeySet() {
        return adj.keySet();
    }

    /*
     * Returns an array of Pairs of all neighbors of some vertex
     * */
    public Pair[] getNeighbors(String vertex) {
        if (adj.get(vertex) == null)
            return new Pair[0];
        //            return null;
        return adj.get(vertex).toArray(new Pair[0]);
    }

    /*
     * Adds node with no neighbors
     * */
    public void addNode(String value) {
        adj.putIfAbsent(value, new LinkedList<>());
    }

    /*
     * Adds both vertex1 and vertex2 to the HashMap and a directed edge in that direction
     * */
    public void addDirectedEdge(String vertex1, String vertex2, Integer edgeValue) {
        if (edgeValue < 0)
            hasNegativeEdges = true;
        if (edgeValue > 1)
            isWeighted = true;
        adj.putIfAbsent(vertex1, new LinkedList<>());
        adj.putIfAbsent(vertex2, new LinkedList<>());
        adj.get(vertex1).add(new Pair<>(vertex2, edgeValue));
    }

    /*
     * Adds both vertex1 and vertex2 to the HashMap and a bidirectional edge between them
     * */
    public void addBidirectionalEdge(String vertex1, String vertex2, Integer edgeValue) {
        if (edgeValue < 0)
            hasNegativeEdges = true;
        if (edgeValue > 1)
            isWeighted = true;
        adj.putIfAbsent(vertex1, new LinkedList<>());
        adj.putIfAbsent(vertex2, new LinkedList<>());
        adj.get(vertex1).add(new Pair<>(vertex2, edgeValue));
        adj.get(vertex2).add(new Pair<>(vertex1, edgeValue));
    }

    /*
     * Wrapper for readAndAddBufferedReader that accepts a string containing an edge list
     * */
    public void processEdgeListStr(@NotNull String edgeList) {
        //Clear HashMap
        adj = new HashMap<>();

        //Removes all empty lines in string
        edgeList.replaceAll("((?m)^[ \t]*\r?\n)", "");

        //New BufferedReader for line reading in string
        try (BufferedReader reader = new BufferedReader(new StringReader(edgeList))) {
            readAndAddBufferedReader(reader);
        } catch (Exception e) {
            emptyList();
            e.printStackTrace();
        }
    }

    /*
     * Wrapper for readAndAddBufferedReader that accepts a file path for a text file edge list
     * */
    public void processEdgeListFile(@NotNull String edgeListPath) {
        //Clear HashMap
        adj = new HashMap<>();

        //Removes all empty lines in string
        edgeListPath.replaceAll("((?m)^[ \t]*\r?\n)", "");

        //New BufferedReader for line reading in txt file
        try (BufferedReader reader = new BufferedReader(new FileReader(edgeListPath))) {
            readAndAddBufferedReader(reader);
        } catch (Exception e) {
            emptyList();
            e.printStackTrace();
        }
    }

    /*
     * Creates an adjacency list with a given Buffer of structure: String String Integer
     *  node1 node2 edge1
     *  node3 node4 edge2
     *  ...
     * */
    public void readAndAddBufferedReader(BufferedReader reader) throws Exception {
        String line;
        String[] lineArray;

        try {
            while ((line = reader.readLine()) != null) {
                lineArray = line.trim().split("\\s+");
                if (lineArray.length != 3) {
                    throw new IOException("Invalid data for binding");
                }
                addDirectedEdge(lineArray[0], lineArray[1], Integer.parseInt(lineArray[2]));
            }
        } catch (Exception e) {
            emptyList();
            e.printStackTrace();
        }
    }
}
