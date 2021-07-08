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
        LinkedList<String> pathList;
        LinkedList<String> expectedPathList;

        pathList = new LinkedList<>();
        list.processEdgeListFile("test/java/GUIapplication/premadeGraph.txt");
        int path = PathFinding.dijkstra(list, "55T", "55C", pathList);
        assertEquals(19, path);

        expectedPathList = new LinkedList<>(List.of("55T", "55Q", "55N", "55I", "55G", "55B", "55A", "55C"));
        assertEquals(expectedPathList, pathList);
    }
}