package com.jjac.pathfindinggui;

import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.util.Pair;

import java.nio.ByteBuffer;
import java.util.*;

/*
 * Utility classes to represent a pair inside a Priority Queue
 * Comparisons are made with the Integer key, for ranking purposes
 * */
class Entry implements Comparable<Entry> {
    private final Integer key;
    private final String value;

    Entry(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(Entry other) {
        return Integer.compare(key, other.getKey());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Entry)) return false;

        final Entry other = (Entry) obj;
        if (!this.key.equals(other.getKey()))
            return false;
        return Objects.equals(this.value, other.getValue());
    }
}

class IntegerEntry implements Comparable<IntegerEntry> {
    private final Integer key;
    private final Integer value;

    IntegerEntry(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(IntegerEntry other) {
        return Integer.compare(this.key, other.getKey());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof IntegerEntry)) return false;

        final IntegerEntry other = (IntegerEntry) obj;
        if (!this.key.equals(other.getKey()))
            return false;
        return this.value == other.getValue();
    }
}

class IntegerBooleanPair {
    public int i;
    public boolean b;

    IntegerBooleanPair(int i, boolean b) {
        this.i = i;
        this.b = b;
    }
}

/*
 * PathFinding utility class
 * Contains various algorithms used for pathfinding between two nodes
 * Includes versions used for drawing inside a javaFX based Grid
 *  */
public final class PathFinding {
    private PathFinding() {
    }

    //Helper function for finding manhattan distance between two nodes where first two bytes == X and last 2 == Y
    private static int MHDistance(int NODE1, int NODE2) {
        return Math.abs((NODE1 >> 16) - (NODE2 >> 16)) + Math.abs((NODE1 & 0x00FF) - (NODE2 & 0x00FF));
    }

    //Helper function for diagonal distance between two nodes when 8 movements are allowed
    private static int diagonalDistance(int NODE1, int NODE2) {
        int dx = Math.abs((NODE1 >> 16) - (NODE2 >> 16));
        int dy = Math.abs((NODE1 & 0x00FF) - (NODE2 & 0x00FF));
        if (dx > dy)
            return 2 * (dx - dy) + 2 * dy;
        else
            return 2 * (dy - dx) + 2 * dx;
    }


    /*
     * A* pathfinding algorithm
     * Returns the shortest path between startNode and endNode and the vertex sequence path between them
     * Uses Manhattan distance between nodes to guide the search
     * Nodes input are integers, where first two bytes = x position and last 2 = y position
     * */
    public static int AStar(AdjList<Integer> adjList, int startNode, int endNode, LinkedList<String> pathList) {
        //Valid AdjList
        if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
            return Integer.MAX_VALUE;

        //Checks if start and end nodes exist in adjList
        if (!adjList.contains(startNode) || !adjList.contains(endNode))
            return Integer.MAX_VALUE;

        //Sets all node objects in adjList to defaults
        adjList.cleanNodeFields();

        //Fibonacci Heap for storing current shortest path + heuristic value as direction for search
        FibHeap<Integer, Integer> qh = new FibHeap<>();

        //Set source node value to 0
        qh.insert(adjList.getNode(startNode), 0);
        qh.peek().d = 0;
        qh.peek().visited = true;

        //Helper variables
        LinkedList<Pair<Node<Integer, Integer>, Integer>> neighbors;//List of pairs <neighborKey, edgeWeight>
        Node<Integer, Integer> neighbor, u;//Current Node with shortest path / top of min-queue
        int edgeWeight;
        while (qh.size() != 0) {
            u = qh.extractMin();
            u.closed = true;

            //endNode found
            if (Objects.equals(u.value, endNode)) {
                //Build path of vertexes
                Node<Integer, Integer> current = adjList.getNode(endNode);
                pathList.add(Integer.toString(current.value));
                while ((current = current.prev) != null)
                    pathList.addFirst(Integer.toString(current.value));
                return u.d;
            }

            neighbors = adjList.getNeighbors(u.value);
            for (Pair<Node<Integer, Integer>, Integer> neighborKeyValuePair : neighbors) {//Loops through every neighbor
                neighbor = neighborKeyValuePair.getKey();
                edgeWeight = neighborKeyValuePair.getValue();

                //Node already has his min path
                if (neighbor.closed)
                    continue;

                //Relaxation step
                //Checks if node has been visited before
                //Checks if distance to neighbor node is less than the min distance already found
                if (!neighbor.visited || neighbor.d > u.d + edgeWeight) {
                    //Assign shorter path found to neighbor in out array
                    neighbor.d = u.d + edgeWeight;

                    //Assigns previous shortest node to neighbor
                    neighbor.prev = u;

                    //Adds neighbor to queue
                    if (!neighbor.visited) {
                        qh.insert(neighbor, u.d + edgeWeight + diagonalDistance(neighbor.value, endNode));
                        neighbor.visited = true;
                    } else {
                        //Decreases the key for neighbor in queue
                        qh.decreaseKey(neighbor,
                                u.d + edgeWeight + diagonalDistance(neighbor.value, endNode));
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }


    /*
     * Dijkstra based pathfinding algorithm
     * Returns the shortest path between startNode and endNode and the vertex sequence path between them
     * */
    public static int dijkstra(AdjList<String> adjList, String startNode, String endNode, LinkedList<String> pathList) {
        //Valid AdjList
        if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
            return Integer.MAX_VALUE;

        //Checks if start and end nodes exist in adjList
        if (!adjList.contains(startNode) || !adjList.contains(endNode))
            return Integer.MAX_VALUE;

        //Checks if graph is suitable for BFS
        if (!adjList.isWeighted())
            return BFS(adjList, startNode, endNode, pathList);

        //Sets all node objects in adjList to defaults
        adjList.cleanNodeFields();

        //Fibonacci heap for storing current shortest path
        FibHeap<Integer, String> q = new FibHeap<>();

        //Set source node value to 0
        q.insert(adjList.getNode(startNode), 0);
        q.peek().d = 0;
        q.peek().visited = true;

        //Helper variables
        LinkedList<Pair<Node<Integer, String>, Integer>> neighbors;//Array of pairs <neighborKey, edgeWeight>
        Node<Integer, String> neighbor, u;//Current Node with shortest path / top of min-queue
        int edgeWeight;
        while (q.size() != 0) {
            u = q.extractMin();
            u.closed = true;

            //endNode found
            if (Objects.equals(u.value, endNode)) {
                //Build path of vertexes
                Node<Integer, String> current = adjList.getNode(endNode);
                pathList.add(current.value);
                while ((current = current.prev) != null)
                    pathList.addFirst(current.value);
                return u.d;
            }

            neighbors = adjList.getNeighbors(u.value);
            for (Pair<Node<Integer, String>, Integer> neighborKeyValuePair : neighbors) {//Loops through every neighbor
                neighbor = neighborKeyValuePair.getKey();
                edgeWeight = neighborKeyValuePair.getValue();
                //Relaxation step
                //Checks if distance to neighbor node is less than the min distance already found
                if (!neighbor.visited || neighbor.d > u.d + edgeWeight) {
                    //Assign shorter path found to neighbor in out array
                    neighbor.d = u.d + edgeWeight;

                    //Assigns previous shortest node to neighbor
                    neighbor.prev = u;

                    //Inserts or decrease key
                    if (!neighbor.visited) {
                        q.insert(neighbor, u.d + edgeWeight);
                        neighbor.visited = true;
                    } else {
                        q.decreaseKey(neighbor, u.d + edgeWeight);
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public static int BFS(AdjList<String> adjList, String startNode, String endNode, LinkedList<String> pathList) {
        //Valid AdjList
        if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges() || adjList.isWeighted())
            return Integer.MAX_VALUE;

        //Checks if start and end nodes exist in adjList
        if (!adjList.contains(startNode) || !adjList.contains(endNode))
            return Integer.MAX_VALUE;

        //Sets all node objects in adjList to defaults
        adjList.cleanNodeFields();

        //Queue for storing current shortest path
        Queue<Node<Integer, String>> q = new LinkedList<>();

        q.add(adjList.getNode(startNode));
        q.peek().key = 0;
        q.peek().d = 0;
        q.peek().visited = true;

        //Helper variables
        LinkedList<Pair<Node<Integer, String>, Integer>> neighbors;//Array of pairs <neighborKey, edgeWeight>
        //Current Node with shortest path / top of min-queue
        Node<Integer, String> neighbor, u;
        while (q.size() != 0) {
            u = q.poll();
            u.closed = true;

            //endNode found
            if (Objects.equals(u.value, endNode)) {
                //Build path of vertexes
                Node<Integer, String> current = adjList.getNode(endNode);
                pathList.add(current.value);
                while ((current = current.prev) != null) {
                    pathList.addFirst(current.value);
                }
                return u.d;
            }

            neighbors = adjList.getNeighbors(u.value);
            for (Pair<Node<Integer, String>, Integer> neighborKeyValuePair : neighbors) {//Loops through every neighbor
                neighbor = neighborKeyValuePair.getKey();
                //Relaxation step
                //Checks if neighbor hasn't been checked, if so the assign the shortest path
                if (!neighbor.visited) {
                    //Adds checked neighbor to queue
                    neighbor.key = u.d + 1;
                    neighbor.d = u.d + 1; //Assign shorter path found to neighbor
                    neighbor.prev = u;
                    neighbor.visited = true;
                    q.add(neighbor);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    /*
     * Class used for Performing Dijkstra pathfinding in Grid
     * startNode and endNode are compressed each inside a int, where first 2 bytes == x coordinate
     * and last 2 == y coordinate
     * */
    public static class DijkstraStepsTask extends Task<Integer> {
        private final AdjList<Integer> adjList;
        private final int startNode, endNode;
        GraphicsContext checkedNodesGC;
        GraphicsContext shortestPathGC;
        double cellSideLength;

        DijkstraStepsTask(AdjList<Integer> adjList, int startNode, int endNode, GraphicsContext checkedNodesGC,
                          GraphicsContext shortestPathGC, double cellSideLength) {
            this.adjList = adjList;
            this.startNode = startNode;
            this.endNode = endNode;
            this.checkedNodesGC = checkedNodesGC;
            this.shortestPathGC = shortestPathGC;
            this.cellSideLength = cellSideLength;
        }

        @Override
        public Integer call() throws Exception {
            //Valid AdjList
            if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
                return Integer.MAX_VALUE;

            //Checks if start and end nodes exist in adjList
            if (!adjList.contains(startNode) || !adjList.contains(endNode))
                return Integer.MAX_VALUE;

            //Checks if graph is suitable for BFS
            if (!adjList.isWeighted())
                return BFS();

            //Sets all node objects in adjList to defaults
            adjList.cleanNodeFields();

            //Fibonacci Heap for storing current shortest path
            FibHeap<Integer, Integer> q = new FibHeap<>();

            //Hashtable used for backtracking of vertex, each key node has of value the previous node of shortest path
//            HashMap<Integer, Integer> previousVertexes = new HashMap<>(adjList.size());
//
//            for (Integer key : adjList.getKeySet()) { //Sets all initial distances to infinity
//                d.putIfAbsent(key, Integer.MAX_VALUE);
//                q.add(new IntegerEntry(Integer.MAX_VALUE, key));
//                previousVertexes.putIfAbsent(key, Integer.MIN_VALUE);
//            }

            //Set source node value to 0
            q.insert(adjList.getNode(startNode), 0);
            q.peek().d = 0;
            q.peek().visited = true;

            //Helper variables
            LinkedList<Pair<Node<Integer, Integer>, Integer>> neighbors;//Array of pairs <neighborKey, edgeWeight>
            Node<Integer, Integer> neighbor, u;
            int edgeWeight;

            //Variables used for buffer drawing
            long speed = (long) (cellSideLength * 1.20);

            while (!isCancelled() && q.size() != 0) {
                u = q.extractMin();
                u.closed = true;

                //Marks node as checked
                checkedNodesGC.fillRect((u.value >> 16) * cellSideLength + 0.5f,
                        (u.value & 0x0000FFFF) * cellSideLength + 0.5f,
                        (int) cellSideLength - 1, (int) cellSideLength - 1);

                //endNode found
                if (u.value == endNode) {
                    //Draws shortest path
                    Node<Integer, Integer> current = u, prev = u;

                    while ((current = current.prev) != null) {
                        shortestPathGC.strokeLine((prev.value >> 16) * cellSideLength + cellSideLength / 2,
                                (prev.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2,
                                (current.value >> 16) * cellSideLength + cellSideLength / 2,
                                (current.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2);
                        prev = current;
                    }
                    return u.d;
                }

                //Wait after checking node
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException interrupted) {
                    if (isCancelled())
                        break;
                }

                //Checking Neighbors
                neighbors = adjList.getNeighbors(u.value);
                for (Pair<Node<Integer, Integer>, Integer> neighborKeyValuePair : neighbors) {
                    neighbor = neighborKeyValuePair.getKey();
                    edgeWeight = neighborKeyValuePair.getValue();

                    //Relaxation step
                    //Checks if distance to neighbor node is less than the min distance already found
                    if (!neighbor.visited || neighbor.d > u.d + edgeWeight) {
                        //Assign shorter path found to neighbor in out array
                        neighbor.d = u.d + edgeWeight;

                        //Assigns previous shortest node to neighbor
                        neighbor.prev = u;

                        //Inserts or decrease key
                        if (!neighbor.visited) {
                            q.insert(neighbor, u.d + edgeWeight);
                            neighbor.visited = true;
                        } else {
                            q.decreaseKey(neighbor, u.d + edgeWeight);
                        }
                    }
                }
            }
            return Integer.MAX_VALUE;
        }

        private int BFS() {
            //Sets all node objects in adjList to defaults
            adjList.cleanNodeFields();

            //Queue for storing current shortest path
            Queue<Node<Integer, Integer>> q = new LinkedList<>();

            //Set source node value to 0
            q.add(adjList.getNode(startNode));
            q.peek().key = 0;
            q.peek().d = 0;
            q.peek().visited = true;

            //Helper variables
            LinkedList<Pair<Node<Integer, Integer>, Integer>> neighbors;//Array of pairs <neighborKey, edgeWeight>
            Node<Integer, Integer> neighbor, u;
            //Variables used for buffer drawing
            byte[] shortestPathBuffer = new byte[650 * 650 * 4], checkedNodesBuffer = new byte[650 * 650 * 4];
            WritablePixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
            PixelWriter shortestPathPW = shortestPathGC.getPixelWriter();
            PixelWriter checkedNodesPW = checkedNodesGC.getPixelWriter();
            long speed = (long) (cellSideLength * 1.20);

            while (!isCancelled() && q.size() != 0) {
                u = q.poll();

                //Marks node as checked
                checkedNodesGC.fillRect((u.value >> 16) * cellSideLength + 0.5f,
                        (u.value & 0x0000FFFF) * cellSideLength + 0.5f,
                        (int) cellSideLength - 1, (int) cellSideLength - 1);

                //endNode found
                if (u.value == endNode) {
                    //Draws shortest path
                    Node<Integer, Integer> current = u, prev = u;

                    while ((current = current.prev) != null) {
                        shortestPathGC.strokeLine((prev.value >> 16) * cellSideLength + cellSideLength / 2,
                                (prev.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2,
                                (current.value >> 16) * cellSideLength + cellSideLength / 2,
                                (current.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2);
                        prev = current;
                    }
                    return u.d;
                }

                //Wait after checking node
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException interrupted) {
                    if (isCancelled())
                        break;
                }

                //Checking Neighbors
                neighbors = adjList.getNeighbors(u.value);
                for (Pair<Node<Integer, Integer>, Integer> neighborKeyValuePair : neighbors) {
                    neighbor = neighborKeyValuePair.getKey();
                    //Relaxation step
                    //Checks if neighbor hasn't been checked, if so the assign the shortest path
                    if (!neighbor.visited) {
                        //Adds checked neighbor to queue
                        neighbor.key = u.d + 1;
                        neighbor.d = u.d + 1; //Assign shorter path found to neighbor
                        neighbor.prev = u;
                        neighbor.visited = true;
                        q.add(neighbor);
                    }
                }
            }
            return Integer.MAX_VALUE;
        }
    }

    public static class AStarStepsTask extends Task<Integer> {
        private final AdjList<Integer> adjList;
        private final int startNode, endNode;
        GraphicsContext checkedNodesGC;
        GraphicsContext shortestPathGC;
        double cellSideLength;

        AStarStepsTask(AdjList<Integer> adjList, int startNode, int endNode, GraphicsContext checkedNodesGC,
                       GraphicsContext shortestPathGC, double cellSideLength) {
            this.adjList = adjList;
            this.startNode = startNode;
            this.endNode = endNode;
            this.checkedNodesGC = checkedNodesGC;
            this.shortestPathGC = shortestPathGC;
            this.cellSideLength = cellSideLength;
        }

        @Override
        public Integer call() throws Exception {
            //Valid AdjList
            if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
                return Integer.MAX_VALUE;

            //Checks if start and end nodes exist in adjList
            if (!adjList.contains(startNode) || !adjList.contains(endNode))
                return Integer.MAX_VALUE;

            //Sets all node objects in adjList to defaults
            adjList.cleanNodeFields();

            //Fibonacci Heap for storing current shortest path + heuristic value as direction for search
            FibHeap<Integer, Integer> qh = new FibHeap<>();

            //Set source node value to 0
            qh.insert(adjList.getNode(startNode), 0);
            qh.peek().d = 0;
            qh.peek().visited = true;

            //Helper variables
            LinkedList<Pair<Node<Integer, Integer>, Integer>> neighbors;//Array of pairs <neighborKey, edgeWeight>
            //Current Node with shortest path / top of min-queue
            int edgeWeight;
            Node<Integer, Integer> neighbor, u;
            long speed = (long) (cellSideLength * 1.20);
            while (!isCancelled() && qh.size() != 0) {
                u = qh.extractMin();
                u.closed = true;

                //Marks node as checked
                checkedNodesGC.fillRect((u.value >> 16) * cellSideLength + 0.5f,
                        (u.value & 0x0000FFFF) * cellSideLength + 0.5f,
                        (int) cellSideLength - 1, (int) cellSideLength - 1);

                //endNode found
                if (u.value == endNode) {
                    //Draws shortest path
                    Node<Integer, Integer> current = u, prev = u;

                    while ((current = current.prev) != null) {
                        shortestPathGC.strokeLine((prev.value >> 16) * cellSideLength + cellSideLength / 2,
                                (prev.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2,
                                (current.value >> 16) * cellSideLength + cellSideLength / 2,
                                (current.value & 0x0000FFFF) * cellSideLength + cellSideLength / 2);
                        prev = current;
                    }
                    return u.d;
                }


                //Wait after checking node
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException interrupted) {
                    if (isCancelled())
                        break;
                }

                neighbors = adjList.getNeighbors(u.value);
                for (Pair<Node<Integer, Integer>, Integer> neighborKeyValuePair : neighbors) {//Loops through every neighbor
                    neighbor = neighborKeyValuePair.getKey();
                    edgeWeight = neighborKeyValuePair.getValue();

                    //Node already has his min path
                    if (neighbor.closed)
                        continue;

                    //Relaxation step
                    //Checks if node has been visited before
                    //Checks if distance to neighbor node is less than the min distance already found
                    if (!neighbor.visited || neighbor.d > u.d + edgeWeight) {
                        //Assign shorter path found to neighbor in out array
                        neighbor.d = u.d + edgeWeight;

                        //Assigns previous shortest node to neighbor
                        neighbor.prev = u;

                        //Adds neighbor to queue
                        if (!neighbor.visited) {
                            qh.insert(neighbor, u.d + edgeWeight + diagonalDistance(neighbor.value, endNode));
                            neighbor.visited = true;
                        } else {
                            //Decreases the key for neighbor in queue
                            qh.decreaseKey(neighbor,
                                    u.d + edgeWeight + diagonalDistance(neighbor.value, endNode));
                        }
                    }
                }
            }
            return Integer.MAX_VALUE;
        }
    }
}
