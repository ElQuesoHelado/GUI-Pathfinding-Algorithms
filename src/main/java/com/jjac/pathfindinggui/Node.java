package com.jjac.pathfindinggui;

/*
 * Node class used in Fibonacci Heap implementation
 * */
public class Node<K, V> {
    public int degree, d;
    public K key;
    public V value;
    public Node<K, V> parent, child, left, right, prev;
    public boolean marked, visited, closed;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}