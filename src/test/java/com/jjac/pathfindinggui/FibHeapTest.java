package com.jjac.pathfindinggui;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class FibHeapTest {

    @Test
    void insert() {
        LinkedList<Node<Integer, Integer>> nodeList = new LinkedList<>();
        nodeList.add(new Node<>(99, 0));
        nodeList.add(new Node<>(35, 0));
        nodeList.add(new Node<>(80, 0));
        nodeList.add(new Node<>(46, 0));
        nodeList.add(new Node<>(34, 0));
        FibHeap<Integer, Integer> heap = new FibHeap<>();
        assertEquals(0, heap.size());

        heap.insert(nodeList.get(0));
        heap.insert(nodeList.get(1));
        heap.insert(nodeList.get(2));
        heap.insert(nodeList.get(3));

        assertEquals(35, heap.peek().key);
        heap.insert(nodeList.get(4));

        assertEquals(5, heap.size());
        assertEquals(34, heap.peek().key);
    }

    @Test
    void extractMin() {
        LinkedList<Node<Integer, Integer>> nodeList = new LinkedList<>();
        nodeList.add(new Node<>(5, 0));
        nodeList.add(new Node<>(50, 0));
        nodeList.add(new Node<>(-5, 0));
        nodeList.add(new Node<>(2, 0));
        nodeList.add(new Node<>(22, 0));
        nodeList.add(new Node<>(17, 0));
        nodeList.add(new Node<>(1, 0));
        nodeList.add(new Node<>(44, 0));
        nodeList.add(new Node<>(145, 0));
        nodeList.add(new Node<>(999, 0));
        nodeList.add(new Node<>(89, 0));
        nodeList.add(new Node<>(567, 0));
        nodeList.add(new Node<>(432, 0));
        nodeList.add(new Node<>(678, 0));
        nodeList.add(new Node<>(12342, 0));
        nodeList.add(new Node<>(400, 0));
        FibHeap<Integer, Integer> heap = new FibHeap<>();

        heap.insert(nodeList.get(0));
        assertEquals(5, heap.extractMin().key);
        assertEquals(0, heap.size());
        assertNull(heap.extractMin());

        heap.insert(nodeList.get(0));
        heap.insert(nodeList.get(1));
        heap.insert(nodeList.get(2));
        heap.insert(nodeList.get(3));
        heap.insert(nodeList.get(4));
        heap.insert(nodeList.get(5));
        heap.insert(nodeList.get(6));
        heap.insert(nodeList.get(7));
        heap.insert(nodeList.get(8));
        heap.insert(nodeList.get(9));
        heap.insert(nodeList.get(10));
        heap.insert(nodeList.get(11));
        heap.insert(nodeList.get(12));
        heap.insert(nodeList.get(13));
        heap.insert(nodeList.get(14));
        heap.insert(nodeList.get(15));

        assertEquals(-5, heap.extractMin().key);
        assertEquals(1, heap.extractMin().key);
        assertEquals(2, heap.extractMin().key);
        assertEquals(5, heap.extractMin().key);
        assertEquals(17, heap.extractMin().key);
        assertEquals(22, heap.extractMin().key);
    }

    @Test
    void decreaseKey() {
        LinkedList<Node<Integer, Integer>> nodeList = new LinkedList<>();
        nodeList.add(new Node<>(5, 0));
        nodeList.add(new Node<>(50, 0));
        nodeList.add(new Node<>(-5, 0));
        nodeList.add(new Node<>(2, 0));
        nodeList.add(new Node<>(22, 0));
        nodeList.add(new Node<>(17, 0));
        nodeList.add(new Node<>(1, 0));
        nodeList.add(new Node<>(44, 0));
        nodeList.add(new Node<>(145, 0));
        nodeList.add(new Node<>(999, 0));
        nodeList.add(new Node<>(89, 0));
        nodeList.add(new Node<>(567, 0));
        nodeList.add(new Node<>(432, 0));
        nodeList.add(new Node<>(678, 0));
        nodeList.add(new Node<>(12342, 0));
        nodeList.add(new Node<>(400, 0));
        FibHeap<Integer, Integer> heap = new FibHeap<>();

        heap.insert(nodeList.get(0));
        heap.insert(nodeList.get(1));
        heap.insert(nodeList.get(2));
        heap.insert(nodeList.get(3));
        heap.insert(nodeList.get(4));
        heap.insert(nodeList.get(5));
        heap.insert(nodeList.get(6));
        heap.insert(nodeList.get(7));
        heap.insert(nodeList.get(8));
        heap.insert(nodeList.get(9));
        heap.insert(nodeList.get(10));
        heap.insert(nodeList.get(11));
        heap.insert(nodeList.get(12));
        heap.insert(nodeList.get(13));
        heap.insert(nodeList.get(14));
        heap.insert(nodeList.get(15));

        heap.extractMin();
        heap.decreaseKey(nodeList.get(14), -9);
        heap.decreaseKey(nodeList.get(11), 50);
        assertEquals(-9, heap.extractMin().key);
    }
}