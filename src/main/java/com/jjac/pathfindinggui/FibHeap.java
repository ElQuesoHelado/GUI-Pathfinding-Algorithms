package com.jjac.pathfindinggui;

import java.util.ArrayList;

/*
 * Class used for Fibonacci Heap structure
 * */
public class FibHeap<K, V> {
    private Node<K, V> min, tail;
    private int nNodes;

    private static final double LOG_GOLDEN_RATION = 0.4812118250596034474977;

    //Getter for number of nodes in Heap
    public int size() {
        return nNodes;
    }

    /*
     * Function for linking two trees with equal degree into one
     * Makes y child of x
     * */
    private void link(Node<K, V> y, Node<K, V> x) {
        //Removes y from root list
        y.left.right = y.right;
        y.right.left = y.left;

        //Empty child list
        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.right = x.child.right;
            y.left = x.child;
            x.child.right.left = y;
            x.child.right = y;
        }
        x.degree++;
        y.marked = false;
        y.parent = x;
    }

    /*
     * Consolidation function for Fibonacci Heap
     * Reduces number of trees
     * */
    private void consolidate() {
        int initialCapacity = (int) Math.ceil((Math.log(nNodes) / LOG_GOLDEN_RATION)) + 1;
//        int initialCapacity = (int) (Math.log(nNodes) / LOG_GOLDEN_RATION);
        ArrayList<Node<K, V>> A = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; ++i)
            A.add(i, null);

        Node<K, V> x = tail, y, tmp;
        int currDegree;

        do {
            //Checks to see if there is a new min
            if (((Comparable) min.key).compareTo(x.key) >= 0) {
                min = x;
            }

            currDegree = x.degree;
            while (A.get(currDegree) != null) {
                y = A.get(currDegree);
                if (((Comparable) y.key).compareTo(x.key) < 0) {// y < x
                    tmp = x;
                    x = y;
                    y = tmp;
                }

                //Reassign tail to next node if necessary to prevent tail becoming a child
                if (tail == y) {
                    tail = x;
                }

                //Links y to x
                link(y, x);
                A.set(currDegree, null);
                currDegree++;
            }
            A.set(currDegree, x);
            x = x.right;
        } while (A.get(x.degree) != x);
    }

    /*
     * Returns min node
     * */
    public Node<K, V> peek() {
        return min;
    }

    /*
     * Insert node with new key into root list
     * */
    public void insert(Node<K, V> node, K key) {
        node.key = key;
        insert(node);
    }

    /*
     * Inserts node into root list, updates min if necessary
     * */
    public void insert(Node<K, V> node) {
        //Default node values
        node.parent = null;
        node.marked = false;
        node.degree = 0;
        node.child = null;
        node.left = node;
        node.right = node;

        //Empty list
        if (tail == null) {
            tail = node;
            min = node;
        } else {//Reassign lefts and rights
            node.right = tail.right;
            node.left = tail;
            tail.right.left = node;
            tail.right = node;
            tail = node;
            if (((Comparable) min.key).compareTo(node.key) > 0) {
                min = node;
            }
        }
        nNodes++;
    }

    /*
     * Removes and returns the min node of the heap
     * Performs a consolidation step where the number of trees is reduced
     * */
    public Node<K, V> extractMin() {
        Node<K, V> minRet = null;

        if (min != null) {
            Node<K, V> current = min.child, child;
            minRet = min;
            //Adds children to root list
            if (current != null)
                do {
                    child = current;
                    current = current.right;

                    child.right = tail.right;
                    child.left = tail;
                    tail.right.left = child;
                    tail.right = child;
                    child.parent = null;

                } while (current != min.child);

            //Moves tail pointer if it's the same as min
            if (min == tail) {
                tail = min.right;
            }

            //If min is the only element just return
            if (min.right == min && nNodes == 1) {
                min = null;
                tail = null;
            } else {
                //Removes min from root list, keeps its out pointers intact
                min.left.right = min.right;
                min.right.left = min.left;
                min = min.right;
                consolidate();
            }
            nNodes--;
        }
        return minRet;
    }

    /*
     * Decreases the key of node
     * Performs cuts if heap property was violated and cascading cuts to reduce further decreaseKey operations cost
     * */
    public void decreaseKey(Node<K, V> node, K reducedKey) {
        if (((Comparable) reducedKey).compareTo(node.key) > 0)
            return;

        Node<K, V> parent = node.parent;
        node.key = reducedKey;
        //Checks if heap property was violated
        if (parent != null && ((Comparable) parent.key).compareTo(node.key) > 0) {
            cut(node, parent);
            cascadingCut(parent);
        }

        //Reduced key is a new min
        if (min != null && ((Comparable) min.key).compareTo(node.key) > 0)
            min = node;
    }

//    public void delete(Node<T> node) {
//        decreaseKey(node, minimumPossibleValueForType);
//        extractMin();
//    }

    //Cuts child node from parent, adds child to root list
    private void cut(Node<K, V> child, Node<K, V> parent) {
        //Removes node from child list
        if (child == parent.child)
            parent.child = child.right;
        if (child.right == child)
            parent.child = null;
        child.left.right = child.right;
        child.right.left = child.left;
        parent.degree--;

        //Adds child to root list
        child.right = tail.right;
        child.left = tail;
        tail.right.left = child;
        tail.right = child;
//        tail = child;
        child.parent = null;
        child.marked = false;
    }

    private void cascadingCut(Node<K, V> node) {
        Node<K, V> parent = node.parent;
        if (parent != null)
            if (!parent.marked)
                parent.marked = true;
            else {
                cut(node, parent);
                cascadingCut(parent);
            }
    }
}
