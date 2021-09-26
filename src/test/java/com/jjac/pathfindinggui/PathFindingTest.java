package com.jjac.pathfindinggui;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PathFindingTest {

    @Test
    void AStar() {
        Grid grid = new Grid(6, 6);
        AdjList<Integer> list;
        LinkedList<String> pathList;
        LinkedList<String> expectedPathList;
        int path;

        grid.addGridElement(0, 0, 0);
        grid.addGridElement(0, 2, 0);
        grid.addGridElement(0, 4, 0);
        grid.addGridElement(0, 2, 1);
        grid.addGridElement(0, 4, 1);
        grid.addGridElement(0, 5, 1);
        grid.addGridElement(0, 1, 2);
        grid.addGridElement(4, 5, 2);
        grid.addGridElement(0, 1, 3);
        grid.addGridElement(0, 3, 3);
        grid.addGridElement(0, 4, 3);
        grid.addGridElement(4, 5, 3);
        grid.addGridElement(0, 1, 4);
        grid.addGridElement(0, 2, 4);
        grid.addGridElement(0, 3, 4);
        grid.addGridElement(2, 4, 4);
        grid.addGridElement(2, 5, 4);
        grid.addGridElement(4, 2, 5);
        grid.addGridElement(4, 3, 5);

        list = grid.generateAdjList();

        //null AdjList
        pathList = new LinkedList<>();
        path = PathFinding.AStar(null, 1 << 16 | 1, 3 << 16 | 1, pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Same startNode as endNode
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 1 << 16 | 1, 1 << 16 | 1, pathList);
        assertEquals(0, path);
        expectedPathList = new LinkedList<>(List.of(Integer.toString(1 << 16 | 1)));
        assertEquals(expectedPathList, pathList);

        //Non reachable node
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00000001, 0x00050000, pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent startNode
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 7 << 16 | 4, 5 << 16 | 4, pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent endNode
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 1 << 16 | 1, 1 << 16 | 4, pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Non existent start and end Nodes
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0, 6 << 16 | 6, pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

        //Normal cases
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00030000, 0x00020005, pathList);
        assertEquals(11, path);
        expectedPathList = new LinkedList<>(
                List.of(Integer.toString(0x00030000), Integer.toString(0x00030001),
                        Integer.toString(0x00040002), Integer.toString(0x00050003),
                        Integer.toString(0x00040004), Integer.toString(0x00030005),
                        Integer.toString(0x00020005)));
//        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00000005, 0x00020003, pathList);
        assertEquals(6, path);
        expectedPathList = new LinkedList<>(List.of(
                Integer.toString(0x00000005), Integer.toString(0x00000004),
                Integer.toString(0x00000003), Integer.toString(0x00000002),
                Integer.toString(0x00010001), Integer.toString(0x00020002),
                Integer.toString(0x00020003)));
        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00030005, 0x00040004, pathList);
        assertEquals(2, path);
        expectedPathList = new LinkedList<>(List.of(Integer.toString(0x00030005), Integer.toString(0x00040004)));
        assertEquals(expectedPathList, pathList);

        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00010000, 0x00040004, pathList);
        assertEquals(10, path);
        expectedPathList = new LinkedList<>(List.of(
                Integer.toString(0x00010000), Integer.toString(0x00010001),
                Integer.toString(0x00020002), Integer.toString(0x00030002),
                Integer.toString(0x00040002), Integer.toString(0x00050003),
                Integer.toString(0x00040004)));
        assertEquals(expectedPathList, pathList);

        grid = new Grid(10, 10);
        grid.addGridElement(0, 3, 1);
        grid.addGridElement(0, 4, 1);
        grid.addGridElement(0, 5, 1);
        grid.addGridElement(0, 6, 1);
        grid.addGridElement(0, 7, 1);
        grid.addGridElement(0, 8, 1);
        grid.addGridElement(0, 1, 2);
        grid.addGridElement(0, 8, 2);
        grid.addGridElement(0, 1, 3);
        grid.addGridElement(0, 8, 3);
        grid.addGridElement(0, 1, 4);
        grid.addGridElement(0, 8, 4);
        grid.addGridElement(0, 1, 5);
        grid.addGridElement(0, 8, 5);
        grid.addGridElement(0, 1, 6);
        grid.addGridElement(0, 8, 6);
        grid.addGridElement(0, 1, 7);
        grid.addGridElement(0, 8, 7);
        grid.addGridElement(0, 1, 8);
        grid.addGridElement(0, 2, 8);
        grid.addGridElement(0, 3, 8);
        grid.addGridElement(0, 4, 8);
        grid.addGridElement(0, 5, 8);
        grid.addGridElement(0, 6, 8);
        grid.addGridElement(0, 7, 8);
        grid.addGridElement(0, 8, 8);


        list = grid.generateAdjList();
        pathList = new LinkedList<>();
        path = PathFinding.AStar(list, 0x00000000, 0x00090009, pathList);
        assertEquals(17, path);

    }

    @Test
    void dijkstra() {
        AdjList<String> list = new AdjList<String>(String.class);
        list.processEdgeListFile(getClass().getResource("/premadeGraph.txt").getFile());
        LinkedList<String> pathList;
        LinkedList<String> expectedPathList;
        int path;

        //null AdjList
        pathList = new LinkedList<>();
        path = PathFinding.dijkstra(null, "55I", "55I", pathList);
        assertEquals(Integer.MAX_VALUE, path);
        expectedPathList = new LinkedList<>();
        assertEquals(expectedPathList, pathList);

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

    @Test
    void BFS() {
        AdjList<String> list = new AdjList<String>(String.class);
        list.processEdgeListFile(getClass().getResource("/unweightedGraph.txt").getFile());
        int path;

        //null adjList
        path = PathFinding.BFS(null, "55R", "55C", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Same startNode as endNode
        path = PathFinding.BFS(list, "55I", "55I", new LinkedList<>());
        assertEquals(0, path);

        //Non reachable node
        path = PathFinding.BFS(list, "55K", "55T", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Shortest path from non connected graphs
        path = PathFinding.BFS(list, "G", "55D", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Non existent startNode
        path = PathFinding.BFS(list, "55Z", "55Q", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Non existent endNode
        path = PathFinding.BFS(list, "55N", "55ER", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Non existent start and end Nodes
        path = PathFinding.BFS(list, "55HJ", "O", new LinkedList<>());
        assertEquals(Integer.MAX_VALUE, path);

        //Normal cases
        path = PathFinding.BFS(list, "55R", "55C", new LinkedList<>());
        assertEquals(5, path);

        path = PathFinding.BFS(list, "55F", "55L", new LinkedList<>());
        assertEquals(5, path);

        path = PathFinding.BFS(list, "55O", "55S", new LinkedList<>());
        assertEquals(4, path);

        path = PathFinding.BFS(list, "G", "E", new LinkedList<>());
        assertEquals(3, path);

        path = PathFinding.BFS(list, "C", "F", new LinkedList<>());
        assertEquals(2, path);

        path = PathFinding.BFS(list, "55T", "55M", new LinkedList<>());
        assertEquals(6, path);

        path = PathFinding.BFS(list, "55R", "55K", new LinkedList<>());
        assertEquals(5, path);
    }
}