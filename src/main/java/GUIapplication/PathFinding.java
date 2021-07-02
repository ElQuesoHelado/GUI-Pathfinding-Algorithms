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

    public static void dijkstra(AdjList adjList, String startNode, String endNode) {
        //Hashtable used to store minimal distance from startNode
        HashMap<String, Integer> d = new HashMap<>(adjList.size());
        PriorityQueue<Entry> q = new PriorityQueue<>(adjList.size()); //Priority queue for storing current shortest path

        for (String key : adjList.getKeySet()) { //Sets all initial distances to infinity
            d.putIfAbsent(key, Integer.MAX_VALUE);
            q.add(new Entry(Integer.MAX_VALUE, key));
        }

        //Set source node value to 0
        d.put(startNode, 0); //Distance form source to source is known == 0
        q.remove(new Entry(Integer.MAX_VALUE, startNode));
        q.add(new Entry(0, startNode));

        Pair<String, Integer>[] neighbors;
        while (q.size() != 0) {
            String u = q.poll().getValue();
            neighbors = adjList.getNeighbors(u);
            for (Pair<String, Integer> pair : neighbors) {//Loops through every neighbor to find a shorter path
                //Relaxation step
                //Checks if distance to neighbor node is less than the min distance already found
                if (d.get(pair.getKey()) > d.get(u) + pair.getValue()) {
                    d.put(pair.getKey(), d.get(u) + pair.getValue()); //Assign shorter path found to neighbor
                    //Decreases the key for neighbor in queue
                    q.remove(new Entry(Integer.MAX_VALUE, startNode));
                    q.add(new Entry(0, startNode));
                }

            }
            //endNode found
            if (Objects.equals(u, endNode))
                break;

        }

    }
}
