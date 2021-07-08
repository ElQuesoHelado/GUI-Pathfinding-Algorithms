package GUIapplication;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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


public final class PathFinding {
    private PathFinding() {
    }

    public static int dijkstra(AdjList adjList, String startNode, String endNode, LinkedList<String> pathList) {
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
                pathList.add(current);
                while ((current = previousVertexes.get(current)) != null) {
                    pathList.addFirst(current);
                }
                return d.get(u);
            }

        }
        return Integer.MAX_VALUE;
    }
}
