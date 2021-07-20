package GUIapplication;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/*
 * Utility class to represent a pair inside a Priority Queue
 * Comparisons are made with the Integer key, for ranking purposes
 * */
class Entry implements Comparable<Entry> {
    private final Integer key;
    private final String value;

    Entry(int _key, String _value) {
        key = _key;
        value = _value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull Entry other) {
        return Integer.compare(this.getKey(), other.getKey());
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

/*
 * PathFinding utility class
 * Contains various algorithms used for pathfinding between two nodes
 * Includes versions used for drawing inside a javaFX based Grid
 *  */
public final class PathFinding {
    private PathFinding() {
    }

    /*
     * Dijkstra based pathfinding algorithm
     * Returns the shortest path between startNode and endNode and the vertex sequence path between them
     * */
    public static int dijkstra(AdjList adjList, String startNode, String endNode, LinkedList<String> pathList) {
        //Valid AdjList
        if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
            return Integer.MAX_VALUE;

        //Checks if graph is suitable for BFS
        if (!adjList.isWeighted())
            return Integer.MAX_VALUE;//**************************************************


        //Hashtable used to store minimal distance from startNode
        HashMap<String, Integer> d = new HashMap<>(adjList.size());
        //Priority queue for storing current shortest path
        PriorityQueue<Entry> q = new PriorityQueue<>(adjList.size());

        //Hashtable used for tracking path of vertexes, each key node has of value the previous node of shortest path
        HashMap<String, String> previousVertexes = new HashMap<>(adjList.size());

        for (String key : adjList.getKeySet()) { //Sets all initial distances to infinity
            d.putIfAbsent(key, Integer.MAX_VALUE);
            q.add(new Entry(Integer.MAX_VALUE, key));
            previousVertexes.putIfAbsent(key, null);
        }

        //Set source node value to 0
        d.put(startNode, 0); //Distance form source to source is known == 0
        q.remove(new Entry(Integer.MAX_VALUE, startNode));
        q.add(new Entry(0, startNode));

        //Helper variables
        Pair<String, Integer>[] neighbors;//Array of pairs <neighborKey, edgeWeight>
        String u, //Current Node with shortest path / top of min-queue
                neighbor;
        int edgeWeight;
        while (q.size() != 0) {
            u = q.poll().getValue();
            neighbors = adjList.getNeighbors(u);
            for (Pair<String, Integer> neighborKeyValuePair : neighbors) {//Loops through every neighbor
                neighbor = neighborKeyValuePair.getKey();
                edgeWeight = neighborKeyValuePair.getValue();
                //Relaxation step
                //Checks if distance to neighbor node is less than the min distance already found
                //Checks if distance to current node is infinite, to avoid overflow
                if ((d.get(u) != Integer.MAX_VALUE) && (d.get(neighbor) > d.get(u) + edgeWeight)) {
                    //Decreases the key for neighbor in queue
                    q.remove(new Entry(d.get(neighbor), neighbor));//Removes and adds to prompt a min-queue restructure
                    q.add(new Entry(d.get(u) + edgeWeight, neighbor));

                    //Assign shorter path found to neighbor in out array
                    d.put(neighbor, d.get(u) + edgeWeight);

                    //Assigns previous shortest node to neighbor
                    previousVertexes.put(neighbor, u);
                }
            }

            //endNode found
            if (Objects.equals(u, endNode)) {
                //Build path of vertexes
                String current = endNode;
                if (d.get(u) != Integer.MAX_VALUE) pathList.add(current);
                while ((current = previousVertexes.get(current)) != null)
                    pathList.addFirst(current);
                return d.get(u);
            }

        }
        return Integer.MAX_VALUE;
    }

    public static int dijkstraStepsGrid(AdjList adjList, String startNode, String endNode,
                                        GraphicsContext checkedNodesGC, GraphicsContext shortestPathGC,
                                        double cellSideLength) {
        //Valid AdjList
        if (adjList == null || adjList.size() == 0 || adjList.hasNegativeEdges())
            return Integer.MAX_VALUE;

        //Hashtable used to store minimal distance from startNode
        HashMap<String, Integer> d = new HashMap<>(adjList.size());
        //Priority queue for storing current shortest path
        PriorityQueue<Entry> q = new PriorityQueue<>(adjList.size());

        //Hashtable used for tracking path of vertexes, each key node has of value the previous node of shortest path
        HashMap<String, String> previousVertexes = new HashMap<>(adjList.size());

        for (String key : adjList.getKeySet()) { //Sets all initial distances to infinity
            d.putIfAbsent(key, Integer.MAX_VALUE);
            q.add(new Entry(Integer.MAX_VALUE, key));
            previousVertexes.putIfAbsent(key, null);
        }

        //Set source node value to 0
        d.put(startNode, 0); //Distance form source to source is known == 0
        q.remove(new Entry(Integer.MAX_VALUE, startNode));
        q.add(new Entry(0, startNode));

        //Helper variables
        Pair<String, Integer>[] neighbors;//Array of pairs <neighborKey, edgeWeight>
        String u, neighbor; //Current Node with shortest path / top of min-queue

        String[] nodeCoords;
        int edgeWeight, time = 0, speed = 300;

        Timeline timeline = new Timeline();
        while (q.size() != 0) {
            u = q.poll().getValue();
            nodeCoords = u.split("-");

            //Marks node as checked an saves a keyframe
            String[] finalNodeCoords1 = nodeCoords;
            KeyFrame kf1 = new KeyFrame(Duration.millis(time), actionEvent -> {
                checkedNodesGC.setFill(Color.VIOLET);
                checkedNodesGC.fillRect(Integer.parseInt(finalNodeCoords1[0]) * cellSideLength + 0.5f,
                        Integer.parseInt(finalNodeCoords1[1]) * cellSideLength + 0.5f,
                        cellSideLength - 1f, cellSideLength - 1f);


            });
            timeline.getKeyFrames().add(kf1);
            time += speed;

            //Draws current shortest path and saves its keyframe
            String finalU = u;
            KeyFrame kf2 = new KeyFrame(Duration.millis(time), actionEvent -> {
                //Clears past path
                shortestPathGC.clearRect(0, 0,
                        shortestPathGC.getCanvas().getWidth(), shortestPathGC.getCanvas().getHeight());

                String current = finalU;
                String[] coords;
                int[] prev = new int[2], curr = new int[2];
                prev[0] = Integer.parseInt(finalNodeCoords1[0]);
                prev[1] = Integer.parseInt(finalNodeCoords1[1]);

                while ((current = previousVertexes.get(current)) != null) {
                    coords = current.split("-");
                    curr[0] = Integer.parseInt(coords[0]);
                    curr[1] = Integer.parseInt(coords[1]);

                    shortestPathGC.strokeLine(prev[0] * cellSideLength + cellSideLength / 2,
                            prev[1] * cellSideLength + cellSideLength / 2,
                            curr[0] * cellSideLength + cellSideLength / 2,
                            curr[1] * cellSideLength + cellSideLength / 2);

                    prev[0] = curr[0];
                    prev[1] = curr[1];
                }


            });
            timeline.getKeyFrames().add(kf2);
            time += speed;

            neighbors = adjList.getNeighbors(u);

            int relaxedCounter = 0;
            for (int i = 0; i < neighbors.length; ++i) {
                neighbor = neighbors[i].getKey();
                edgeWeight = neighbors[i].getValue();


                //Relaxation step
                //Checks if distance to neighbor node is less than the min distance already found
                //Checks if distance to current node is infinite, to avoid overflow
                if ((d.get(u) != Integer.MAX_VALUE) && (d.get(neighbor) > d.get(u) + edgeWeight)) {
                    //Decreases the key for neighbor in queue
                    q.remove(new Entry(d.get(neighbor), neighbor));//Removes and adds to prompt a min-queue restructure
                    q.add(new Entry(d.get(u) + edgeWeight, neighbor));


                    nodeCoords = neighbor.split("-");
                    String[] finalNodeCoords = nodeCoords;
                    KeyFrame kf = new KeyFrame(Duration.millis(time + relaxedCounter), actionEvent -> {
                        checkedNodesGC.setFill(Color.PALEGREEN);
                        checkedNodesGC.fillRect(Integer.parseInt(finalNodeCoords[0]) * cellSideLength + 0.5f,
                                Integer.parseInt(finalNodeCoords[1]) * cellSideLength + 0.5f,
                                cellSideLength - 1f, cellSideLength - 1f);


                    });


                    timeline.getKeyFrames().add(kf);
                    relaxedCounter += speed;


                    //Assign shorter path found to neighbor in out array
                    d.put(neighbor, d.get(u) + edgeWeight);

                    //Assigns previous shortest node to neighbor
                    previousVertexes.put(neighbor, u);
                }
            }
            time += relaxedCounter;


            //endNode found
            if (Objects.equals(u, endNode)) {
                //Color grid to found
                timeline.play();
                return d.get(u);
            }
        }
        return Integer.MAX_VALUE;
    }
}
