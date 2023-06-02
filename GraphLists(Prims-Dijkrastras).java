/*
 * Author: Ian Hipolito
 * StudentID: C21436494
 * Class: TU856/2 - Algorithms & Data Structures
 * Assignment: Graph Traversal, MST & SPT Algorithm Assignment
 */


// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.*;

class Heap {
    private int[] a; // heap array
    private int[] hPos; // hPos[h[k]] == k
    private int[] dist; // dist[v] = priority of v

    private int N; // heap size

    // The heap constructor gets passed from the Graph:
    // 1. maximum heap size
    // 2. reference to the dist[] array
    // 3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    public boolean isEmpty() {
        return N == 0; //checks if the heap is empty returns true, otherwise returns false
    }

    public void siftUp(int k) {
        int v = a[k]; //store value of element at index k in v

        // code yourself
        // must use hPos[] and dist[] arrays

        //while parent's priority is greater than element's priority
        while (k > 1 && dist[v] < dist[a[k / 2]]) {
            a[k] = a[k / 2]; //swap element with parent
            hPos[a[k]] = k; //update position of swappen element in hPos array
            k = k / 2; //move index k to parent
        }

        a[k] = v; //insert current element at its correct position
        hPos[v] = k; //update position of current element in hPos array
    }

    public void siftDown(int k) {
        int v, j;

        v = a[k]; //store value of element at index k in v

        // code yourself
        // must use hPos[] and dist[] arrays

        //while element has at least one child
        while (2 * k <= N) {
            j = 2 * k; //index of left child

            //if right child has greater priority than left child
            if (j < N && dist[a[j]] > dist[a[j + 1]]){
                j++; //index of right child
            }
            //if element's is less than or equal to its child's
            if (dist[v] <= dist[a[j]]){
                break; //exit loop
            }

            a[k] = a[j]; //swap element with its smallest child
            hPos[a[k]] = k; //update position of swappen element in hPos array
            k = j; //move index k to smallest child
        }

        a[k] = v; //insert current element at its correct position
        hPos[v] = k; //update position of current element in hPos array
    }

    public void insert(int x) {
        a[++N] = x; //insert element x at the end of the heap
        siftUp(N); //sift up the element to its correct position
    }

    public int remove() {
        int v = a[1]; //store value of root in v
        hPos[v] = 0; // v is no longer in heap
        a[N + 1] = 0; // put null node into empty spot

        a[1] = a[N--]; //swap last element with root, and decrement N
        siftDown(1); //perform sift-down operation to maintain heap property

        return v; //return value of removed element
    }

    public void showHeap() {
        System.out.print("Heap = ");
        for (int i = 1; i <= N; i++)
            System.out.print(a[i] + " "); //print all elements in the heap
        System.out.println();
    }

}

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public Graph(String graphFile) throws IOException {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +"; // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v)
            adj[v] = z;

        // read the edges
        System.out.println("Reading edges from text file");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            // write code to put edge into adjacency matrix
            t = new Node(); //create new node and store in t
            t.vert = v; //set vertex of t to v
            t.wgt = wgt; //set weight of t to wgt
            t.next = adj[u]; //set next pointer of new node to current head of adjacency list
            adj[u] = t; //update head of adjacency list for vertex u to new node

            t = new Node(); //create new node and store in t
            t.vert = u; //set vertex of t to u
            t.wgt = wgt; //set weight of t to wgt
            t.next = adj[v]; //set next pointer of new node to current head of adjacency list
            adj[v] = t; //update head of adjacency list for vertex v to new node
        }
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // method to display the graph representation
    public void display() {
        int v;
        Node n;

        System.out.println("\nAdjacency List");

        for (v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }

    //method to to print out contents of the array
    public void showArray(int[] arr) {
        //iterate through array starting at index 1
        for (int i = 1; i < arr.length; i++) {
            System.out.print(arr[i] + " "); //print the current element of the array followed by a space
        }
        System.out.print("\n"); //print a newline character after all the elements have been printed
    }

    public void showMST() {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for (int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(mst[v]));
        System.out.println("");
    }

    //method to find the minimum spanning tree using Prim's algorithm, starting at vertex s
    public void MST_Prim(int s) {
        int v, u; //vertices
        int wgt, wgt_sum = 0; //wgt = weight, wgt_sum = total weight of MST
        int[] dist, parent, hPos; //dist = distance, parent = parent, hPos = heap position
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];
        Heap h = new Heap(V, dist, hPos); //h is a heap that stores the vertices not yet in the MST
        Node t; //node in the adjacency list of a vertex

        //initialise dist, parent, and hPos arrays
        for (v = 0; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }

        h.insert(s); //insert the starting vertex into the heap
        dist[s] = 0; //set the distance from the starting vertex to itself to 0

        System.out.print("\n------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");
        System.out.print("\n\nPrim's Minimum Spanning Tree Algorithm\n");
        System.out.print("\n\nStarting from vertex " + s + "\n");
        
        //while there are no vertices in the MST yet
        while (!h.isEmpty()) {
            v = h.remove(); //remove the vertex with the smallest distance from the heap
            dist[v] = -dist[v]; //set the distance to the vertex to negative to indicate that it is now in the MST

            System.out.print("\nVertex " + v + " removed from heap.\n");

            //show contents of heap, parent[], and dist[] arrays
            System.out.print("Heap: ");
            h.showHeap();
            System.out.print("Parent: ");
            showArray(parent);
            System.out.print("Distance: ");
            showArray(dist);

            //for loop to iterate through the vertices
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert;
                wgt = t.wgt;

                System.out.print("Examining vertex " + u + " with the weight " + wgt + ".\n");

                //if weight if edge is less than the distance of the vertex
                if (wgt < dist[u]) {
                    //if the vertex is already in the heap
                    if (dist[u] != Integer.MAX_VALUE) {
                        wgt_sum -= dist[u];
                    }

                    dist[u] = wgt; //update minimum distance of vertex "u" to the MST
                    parent[u] = v; //update parent vertex of "u" in the MST to "v"
                    wgt_sum += wgt; //update total weight of the MST

                    if (hPos[u] == 0) {
                        h.insert(u);
                        System.out.print("Vertex " + u + " inserted into heap.\n");
                    } 
                    else {
                        h.siftUp(hPos[u]);
                        System.out.print("Vertex " + u + " updated in heap.\n");
                    }
                }
            }

        }

        System.out.print("\nThe Weight of the MST = " + wgt_sum + "\n");

        mst = parent;
        showMST();

        System.out.print("\n------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");
    }

    public void SPT_Dijkstra(int s) {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist, parent, hPos;

        //create arrays to store the distances, parents, and heap positions of each vertex
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        //initialise dist, parent, and hPos arrays
        for(v = 0; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }

        Heap h = new Heap(V, dist, hPos); //create a heap to store the vertices by distance from the source vertex
        h.insert(s);
        dist[s] = 0;

        System.out.print("\n\nDijkstra's Shortest Path Tree Algorithm\n\n");
        System.out.print("Starting from vertex " + s + "\n\n"); //print the source vertex

        //while the heap is not empty, remove the vertex with the smallest distance from the heap
        while(!h.isEmpty()){
            u = h.remove();

            System.out.print("\nVertex " + u + " removed from heap.\n");

            // Show contents of heap, parent[], and dist[] arrays
            System.out.print("Heap: ");
            h.showHeap();
            System.out.print("Parent: ");
            showArray(parent);
            System.out.print("Dist: ");
            showArray(dist);
            System.out.print("\n");

            //for each adjacent vertex, update its distance and parent if a shorter path is found
            for(Node n = adj[u]; n != z; n = n.next){
                v = n.vert;
                wgt = n.wgt;

                System.out.print("Examining vertex " + v + " with the weight " + wgt + ".\n");

                //if the distance to the adjacent vertex is greater than the distance to the current vertex + the weight of the edge
                if(dist[u] + wgt < dist[v]){
                    dist[v] = dist[u] + wgt; //update the distance to the adjacent vertex
                    parent[v] = u; //update the parent of the adjacent vertex to the current vertex
                    
                    if(hPos[v] == 0){
                        h.insert(v);
                        System.out.println("Vertex " + v + " inserted into heap.");
                    } 
                    else{
                        h.siftUp(hPos[v]);
                        System.out.println("Vertex " + v + " updated in heap.");
                    }
                }
            }
        }

        System.out.println("SPT Using Dijkstra's Algorithm From Source Vertex " + s + ":"); //print the shortest path tree
        System.out.println("Vertex   Parent  Distance");
        for (v = 1; v <= V; v++) {
            System.out.println(toChar(v) + "         " + toChar(parent[v]) + "       " + dist[v]);
        }

        int totalWeight = 0; //calculate and print the weight of the shortest path tree
        for (int i = 1; i <= V; i++) {
            if (dist[i] != Integer.MAX_VALUE) {
                totalWeight += dist[i];
            }
        }
        System.out.println("\nWeight of SPT: " + totalWeight + "\n");
        System.out.print("\n------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");

    }

    public void DF(int s) {
        id = 0;
        visited = new int[V + 1];

        for (int v = 1; v <= V; v++) {
            visited[v] = 0;
        }

        System.out.print("\n\nDepth First Traversel Algorithm (Using Recursion)\n\n");

        DFVisit(0, s);

        System.out.print("\n------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");

    }

    public void DFVisit(int prev, int v) {
        int u;
        Node t;
        visited[v] = ++id;

        System.out.println("Visited Vertex " + toChar(v) + " along edge " + toChar(prev) + "---" + toChar(v));

        for (t = adj[v]; t != z; t = t.next) {
            u = t.vert;
            if (visited[u] == 0) {
                DFVisit(v, u);
            }
        }
    }

    public void breadthFirst(int s) {
        boolean[] visited = new boolean[V + 1];
        int[] queue = new int[V + 1];
        int front = 0;
        int back = -1;

        visited[s] = true;
        queue[++back] = s;

        System.out.print("\n\nBreath First Traversel Algorithm (Using a Queue)\n\n");

        while (front <= back) {
            s = queue[front++];

            System.out.print(" " + toChar(s) + " ");

            for (Node n = adj[s]; n != z; n = n.next) {
                int v = n.vert;

                if (!visited[n.vert]) {
                    visited[v] = true;
                    queue[++back] = v;
                }
            }
        }
    }

    public class GraphLists {
        public static void main(String[] args) throws IOException {
            // int s = 12;
            // String fname = "wGraph1.txt";
            int s;
            String fname;

            System.out.println("Enter the name of the input file: ");
            Scanner Name = new Scanner(System.in);
            fname = Name.nextLine();

            System.out.println("Starting Vertex: ");
            Scanner Vertex = new Scanner(System.in);
            s = Vertex.nextInt();

            Graph g = new Graph(fname);

            g.display();

            g.MST_Prim(s);
            g.SPT_Dijkstra(s);
            g.DF(s);
            g.breadthFirst(s);
        }
    }

}
