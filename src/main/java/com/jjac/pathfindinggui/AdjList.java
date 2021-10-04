package com.jjac.pathfindinggui;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/*
 * Adjacency List class
 * Keys of type T are stored in a hashmap
 * each value is a LinkedList containing Pair<String, Integer> neighbors
 * where string = neighbor node value, Integer = edge value
 * */
public class AdjList<V> {
    private Map<V, Pair<Node<Integer, V>, LinkedList<Pair<Node<Integer, V>, Integer>>>> adj;
    private boolean isWeighted = false, hasNegativeEdges = false;
    Class<V> type;

    AdjList(Class<V> type) {
        this.adj = new HashMap<>();
        this.type = type;
    }

    AdjList(Class<V> type, int initialSize) {
        this.type = type;
        this.adj = new HashMap<>((initialSize * 4 + 2) / 3);
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

    public boolean contains(V key) {
        return adj.containsKey(key);
    }

    /*
     * Get HashMap keySet
     * */
    public Set<V> getKeySet() {
        return adj.keySet();
    }

    public Node<Integer, V> getNode(V key) {
        if (!adj.containsKey(key))
            return null;
        return adj.get(key).getKey();

    }

    /*
     * Assigns every field of node to default except value and key
     * */
    public void cleanNodeFields() {
        Node<Integer, V> tmp;
        for (Pair<Node<Integer, V>, LinkedList<Pair<Node<Integer, V>, Integer>>> value : adj.values()) {
            tmp = value.getKey();
            tmp.degree = 0;
            tmp.d = Integer.MAX_VALUE;
            tmp.parent = null;
            tmp.child = null;
            tmp.left = null;
            tmp.right = null;
            tmp.prev = null;
            tmp.marked = false;
            tmp.visited = false;
            tmp.closed = false;
        }
    }

    /*
     * Returns an array of Pairs of all neighbors of some vertex
     * */
    public LinkedList<Pair<Node<Integer, V>, Integer>> getNeighbors(V vertex) {
        if (!adj.containsKey(vertex))
            return null;
        return adj.get(vertex).getValue();
    }


    /*
     * Adds node with no neighbors
     * */
    public void addNode(V vertex) {
        adj.putIfAbsent(vertex, new Pair<>(new Node<>(Integer.MAX_VALUE, vertex), new LinkedList<>()));
    }

    /*
     * Adds both vertex1 and vertex2 to the HashMap and a directed edge in that direction
     * */
    public void addDirectedEdge(V vertex1, V vertex2, int edgeValue) {
        if (edgeValue < 0)
            hasNegativeEdges = true;
        if (edgeValue > 1)
            isWeighted = true;
        adj.putIfAbsent(vertex1, new Pair<>(new Node<>(Integer.MAX_VALUE, vertex1), new LinkedList<>()));
        adj.putIfAbsent(vertex2, new Pair<>(new Node<>(Integer.MAX_VALUE, vertex2), new LinkedList<>()));
        adj.get(vertex1).getValue().add(new Pair<>(adj.get(vertex2).getKey(), edgeValue));
    }

    /*
     * Adds both vertex1 and vertex2 to the HashMap and a bidirectional edge between them
     * */
    public void addBidirectionalEdge(V vertex1, V vertex2, Integer edgeValue) {
        if (edgeValue < 0)
            hasNegativeEdges = true;
        if (edgeValue > 1)
            isWeighted = true;
        adj.putIfAbsent(vertex1, new Pair<>(new Node<>(Integer.MAX_VALUE, vertex1), new LinkedList<>()));
        adj.putIfAbsent(vertex2, new Pair<>(new Node<>(Integer.MAX_VALUE, vertex2), new LinkedList<>()));
        adj.get(vertex1).getValue().add(new Pair<>(adj.get(vertex2).getKey(), edgeValue));
        adj.get(vertex2).getValue().add(new Pair<>(adj.get(vertex1).getKey(), edgeValue));
    }

    /*
     * Wrapper for readAndAddBufferedReader that accepts a string containing an edge list
     * */
    public void processEdgeListStr(String edgeList) {
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
    public void processEdgeListFile(String edgeListPath) {
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
    public void readAndAddBufferedReader(BufferedReader reader) {
        String line;
        String[] lineArray;

        try {
            while ((line = reader.readLine()) != null) {
                lineArray = line.trim().split("\\s+");
                if (lineArray.length != 3) {
                    throw new IOException("Invalid data for binding");
                }

                if (type == String.class)
                    addDirectedEdge(type.cast(lineArray[0]), type.cast(lineArray[1]),
                            Integer.parseInt(lineArray[2]));
                else if (type == Integer.class)
                    addDirectedEdge(type.cast(Integer.parseInt(lineArray[0])),
                            type.cast(Integer.parseInt(lineArray[1])), Integer.parseInt(lineArray[2]));
                else if (type == Long.class)
                    addDirectedEdge(type.cast(Long.parseLong(lineArray[0])),
                            type.cast(Long.parseLong(lineArray[1])), Integer.parseInt(lineArray[2]));
                else if (type == Float.class)
                    addDirectedEdge(type.cast(Float.parseFloat(lineArray[0])),
                            type.cast(Float.parseFloat(lineArray[1])), Integer.parseInt(lineArray[2]));
                else if (type == Double.class)
                    addDirectedEdge(type.cast(Double.parseDouble(lineArray[0])),
                            type.cast(Double.parseDouble(lineArray[1])), Integer.parseInt(lineArray[2]));
                else
                    throw new IOException("Invalid data for binding");


            }
        } catch (Exception e) {
            emptyList();
            e.printStackTrace();
        }
    }
}