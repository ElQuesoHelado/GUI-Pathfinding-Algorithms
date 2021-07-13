package GUIapplication;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PathFindingTest {

    @Test
    void dijkstra() {
        AdjList list = new AdjList();
        list.processEdgeListFile("test/java/GUIapplication/premadeGraph.txt");
        LinkedList<String> pathList;
        LinkedList<String> expectedPathList;
        int path;

        //Same startNode as endNode
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55I", "55I", pathList);
        assertEquals(0, path);
        expectedPathList = new LinkedList<>(List.of("55I"));
        assertEquals(expectedPathList, pathList);

        //Non reachable node
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55J", "55M", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Shortest path from non connected graphs
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55I", "A", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent startNode
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55Z", "55K", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent endNode
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55N", "55X", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent start and end Nodes
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55Z", "55Y", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Normal cases
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55T", "55C", pathList);
        assertEquals(19, path);
        expectedPathList = new LinkedList<>(List.of("55T", "55Q", "55N", "55I", "55G", "55B", "55A", "55C"));
        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "55S", "55M", pathList);
        assertEquals(19, path);
        expectedPathList = new LinkedList<>(List.of("55S", "55T", "55Q", "55N", "55I", "55G", "55B", "55E", "55M"));
        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "C", "H", pathList);
        assertEquals(13, path);
        expectedPathList = new LinkedList<>(List.of("C", "B", "A", "D", "H"));
        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(list, "F", "A", pathList);
        assertEquals(3, path);
        expectedPathList = new LinkedList<>(List.of("F", "A"));
        assertEquals(expectedPathList, pathList);
    }
}