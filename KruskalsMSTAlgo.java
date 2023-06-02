/*
 * Author: Ian Hipolito
 * StudentID: C21436494
 * Class: TU856/2 - Algorithms & Data Structures
 * Assignment: Graph Traversal, MST & SPT Algorithm Assignment
 */

// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.Scanner;

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
    int[] parent;
    
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
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;

            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;
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

        for (v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }

    //Edge class to store edges and their weights
    class Edge{
        public int u;
        public int v;
        public int wgt;

        //constructor
        public Edge(){
            u = 0;
            v = 0;
            wgt = 0;
        }

        public Edge(int wgt, int u, int v){
            this.u = u;
            this.v = v;
            this.wgt = wgt;
        }

        //display edge
        public void show() {
            System.out.println("Edge {" + toChar(u) + "--(" + wgt + ")--" + toChar(v) + "}");
        }
    }

    //QuickSort method takes an array of edges, left and right integers
    public void QS(Edge[] arr, int left, int right){
        //if left is less than right
        if (left < right){
            int pivot = partition(arr, left, right); //declares pivot and assigns it to partition method
            QS(arr, left, pivot - 1); //recursively calls QuickSort method
            QS(arr, pivot + 1, right); //recursively calls QuickSort method
        }
    }

    //Partition method takes an array of edges, left and right integers
    private int partition(Edge[] arr, int left, int right){
        Edge pivot = arr[right]; //assigns pivot to to right index of array
        int i = left - 1; //assigns i to left - 1

        //for loop to iterate from left to right
        for (int j = left; j < right; j++){ 

            //checks if weight of edge is less than or equal to pivot
            if (arr[j].wgt <= pivot.wgt){
                i++; 
                //swaps arr[i] and arr[j]
                Edge temp = arr[i]; 
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        //swaps arr[i + 1] and arr[right]
        Edge temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;

        return i + 1; //returns index of pivot after partition
    }

    //findRoot the root of a set using path compression
    public int FindRoot(int u) {
        //checks of u is in the parent array
        if (parent[u] != u) {
            parent[u] = FindRoot(parent[u]); //recursively calls FindRoot method
        }
        return parent[u]; //returns value of parent[u] after path compression
    }

    //Union By Rank method takes an array of parents, an array of ranks, and two integers
    public void UBR(int[] parent, int[] rank, int uSet, int vSet) {
        //checks if rank of uSet is less than rank of vSet
        if (rank[uSet] > rank[vSet]) {
            parent[vSet] = uSet; //assigns parent of vSet to uSet
        } 
        //checks if rank of uSet is less than rank of vSet
        else if (rank[uSet] < rank[vSet]) {
            parent[uSet] = vSet; //assigns parent of uSet to vSet
        } 
        //checks if rank of uSet is equal to rank of vSet
        else {
            parent[vSet] = uSet; //assigns parent of vSet to uSet
            rank[uSet] = rank[uSet] + 1; //increments rank of uSet
        }
    }

    //showSets method displays the sets
    public void showSets()
    {
        int u, root; //u is vertex, root is root of set
        int[] shown = new int[V+1]; //array to keep track of shown sets
        //for loop to iterate through vertices
        for (u=1; u<=V; ++u)
        {   
            root = FindRoot(u); //assigns root to FindRoot method
            //checks if shown[root] is not equal to 1
            if(shown[root] != 1) {
                showSet(root); //calls showSet method
                shown[root] = 1; //assigns shown[root] to 1
            }            
        }   
        System.out.print("\n");
    }

    //showSet method takes int root as parameter and displays the set 
    private void showSet(int root)
    {
        int v;
        System.out.print("Set{");

        //for loop to iterate through vertices
        for(v=1; v<=V; ++v)
            //checks if FindRoot of v is equal to root
            if(FindRoot(v) == root){
                System.out.print(toChar(v) + " ");
            }
    
        System.out.print("}  ");
    
    }

    //KruskalMST method finds the minimum spanning tree
    public void KruskalMST() {
        Edge[] edges = new Edge[E]; //array of edges
        Edge[] mst = new Edge[V - 1]; //array of edges in MST

        int i = 0;
        
        //for loop to iterate through vertices
        for(int u = 1; u <= V; u++){
            //for loop to iterate through adjacent vertices
            for(Node n = adj[u]; n != z; n = n.next){
                int v = n.vert; //assigns v to vertex
                int wgt = n.wgt; //assigns wgt to weight

                //checks if u is less than v
                if(u < v){
                    edges[i++] = new Edge(wgt, u, v); //assigns edges[i] to new Edge
                }
            }
        }

        QS(edges, 0, edges.length - 1); //calls QuickSort method

        System.out.println("Sorted edges");

        //for loop to iterate through edges
        for(i = 0; i < edges.length; i++)
        {
            edges[i].show(); //calls show method
        }
        
        parent = new int[V + 1]; //array of parents
        int[] rank = new int[V + 1]; //array of ranks
        int mstWeight = 0; //weight of MST

        //for loop to iterate through vertices
        for(int v = 1; v <= V; v++){
            parent[v] = v; //assigns parent[v] to v
        }

        System.out.println("\n\n");

        int counter = 0; //counter for mst array

        //for loop to iterate through edges
        for(int j = 0; j < E; j++){
            Edge e = edges[j]; //assigns e to edges[j]
            int u = e.u; //assigns u to e.u
            int v = e.v; //assigns v to e.v
            int wgt = edges[j].wgt; //assigns wgt to edges[j].wgt
            int uSet = FindRoot(u); //assigns uSet to FindRoot of u
            int vSet = FindRoot(v); //assigns vSet to FindRoot of v

            //checks if uSet is not equal to vSet
            if(uSet != vSet){
                showSets(); //calls showSets method
                UBR(parent, rank, uSet, vSet); //calls UBR method
                mstWeight += wgt; //increments mstWeight by wgt
                mst[counter++] = e; //assigns mst[counter] to e
            }
        }

        //for loop to iterate through mst array
        for(i = 0; i < mst.length; i++){
            mst[i].show(); //calls show method
        }

        System.out.println("\nWeight of MST: " + mstWeight);
    }


    public class KruskalMSTAlgo {
        public static void main(String[] args) throws IOException {
            // String fname = "wGraph1.txt";

            
            String fname;

            System.out.println("Enter the name of the input file: ");
            Scanner Name = new Scanner(System.in);
            fname = Name.nextLine();
            
            Graph g = new Graph(fname);

            g.display();

            g.KruskalMST();

        }
    }

}