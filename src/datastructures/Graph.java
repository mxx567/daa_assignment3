package datastructures;

import java.util.PriorityQueue;
import java.util.Stack;

public class Graph {
    Node[] nodes = new Node[0];
    public boolean isDirected = false;
    public int size = 0;
    DynamicArray<Edge> mstEdges = new DynamicArray<>(new Edge[0]);

    DisjointSetUnion dsu = new DisjointSetUnion();


    private void checkVertex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Invalid vertex: " + index + ", vertex count: " + size
            );
        }
    }

    public Graph(boolean isDirected){
        this.isDirected = isDirected;
    }

    public void addNode(){
        if(nodes.length == size){
            Node[] newNodes = new Node[Math.max(1, nodes.length) * 2];
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            nodes = newNodes;
        }
        nodes[size] = new Node();
        dsu.make_set(size);
        size++;
    }

    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);

        get(from).add(new Edge(from, to, weight));

        if (!isDirected) {
            get(to).add(new Edge(to, from, weight));
        }
    }

    Node get(int index){
        checkVertex(index);
        return nodes[index];
    }

    public void view(){
        for(int i = 0; i < nodes.length; i++){
            if(nodes[i] != null){
                System.out.println("From node: " + i);
                nodes[i].getAdjacency();
            }
        }
    }


    public Edge[] getSortedEdges() {
        int count = 0;

        for (int i = 0; i < size; i++) {
            Node node = nodes[i];

            for (int j = 0; j < node.size; j++) {
                Edge edge = node.adjacency[j];

                if (isDirected || i < edge.to) {
                    count++;
                }
            }
        }

        Edge[] edges = new Edge[count];
        int index = 0;

        for (int i = 0; i < size; i++) {
            Node node = nodes[i];

            for (int j = 0; j < node.size; j++) {
                Edge edge = node.adjacency[j];

                if (isDirected || i < edge.to) {
                    edges[index++] = edge;
                }
            }
        }

        java.util.Arrays.sort(edges, (a, b) -> Integer.compare(a.weight, b.weight));

        return edges;
    }

    public int Kruskal(Edge[] e){
        int totalWeight = 0;
        for (Edge edge : e) {
            if (dsu.find_set(edge.from) != dsu.find_set(edge.to)) {
                mstEdges.add(edge);
                totalWeight += edge.weight;

                dsu.union_sets(edge.from, edge.to);
            }
        }
        return totalWeight;
    }

    public long prim(int start) {
        if (isDirected) {
            throw new IllegalStateException("Prim requires an undirected graph");
        }

        if (start < 0 || start >= size) {
            throw new IndexOutOfBoundsException("Invalid starting vertex: " + start);
        }

        boolean[] visited = new boolean[size];

        java.util.PriorityQueue<Edge> queue =
                new java.util.PriorityQueue<>(
                        (a, b) -> Integer.compare(a.weight, b.weight)
                );

        DynamicArray<Edge> mstEdges = new DynamicArray<>(new Edge[0]);
        long totalWeight = 0;
        int visitedCount = 1;

        visited[start] = true;
        addCandidateEdges(start, visited, queue);

        while (!queue.isEmpty() && visitedCount < size) {
            Edge edge = queue.poll();

            if (visited[edge.to]) {
                continue;
            }
            mstEdges.add(edge);
            totalWeight += edge.weight;

            visited[edge.to] = true;
            visitedCount++;

            addCandidateEdges(edge.to, visited, queue);
        }

        if (visitedCount != size) {
            System.out.println("Graph is disconnected. No spanning tree exists.");
            return 0;
        }

        return totalWeight;
    }

    private void addCandidateEdges(
            int vertex,
            boolean[] visited,
            java.util.PriorityQueue<Edge> queue) {

        Node node = nodes[vertex];

        for (int i = 0; i < node.size; i++) {
            Edge edge = node.adjacency[i];

            if (!visited[edge.to]) {
                queue.offer(edge);
            }
        }
    }


    public int[] Kahn(){
        int[] indegree = new int[size];

        for (int i = 0; i < size; i++) {
            Node node = nodes[i];
            for (int j = 0; j < node.size; j++) {
                Edge edge = node.adjacency[j];
                indegree[edge.to]++;
            }
        }

        java.util.PriorityQueue<Integer> pq = new PriorityQueue<>();
        for(int i = 0; i < size; i++){
            if(indegree[i] == 0){
                pq.add(i);
            }
        }


        int index = 0;
        int[] ord = new int[size];
        while(!pq.isEmpty()){
            int at = pq.poll();
            ord[index++] = at;

            Node node = nodes[at];

            for (int i = 0; i < node.size; i++) {
                Edge edge = node.adjacency[i];

                indegree[edge.to]--;

                if (indegree[edge.to] == 0) {
                    pq.add(edge.to);
                }
            }
        }
        if(index != size){
            System.out.println("Graph contains a cycle");
            return null;
        }
        return ord;
    }

    public void Kosaraju(){

        boolean[] visited = new boolean[size];

        java.util.Stack<Integer> stack = new Stack<>();

        for(int v = 0; v < size; v++){
            if(!visited[v]){
                DFSkosaraju(v, visited, stack);
            }
        }

        Graph rev = reverse();

        visited = new boolean[size];

        while(!stack.isEmpty()){
            int v = stack.pop();
            if(!visited[v]){
                DFS(v,rev,visited);
                System.out.println();
            }
        }
    }


    private void DFSkosaraju(int node, boolean[] visited, Stack<Integer> stack) {

        visited[node] = true;
        Node current = nodes[node];

        for (int i = 0; i < current.size; i++) {
            Edge edge = current.adjacency[i];

            if (!visited[edge.to]) {
                DFSkosaraju(edge.to, visited, stack);
            }
        }

        stack.push(node);
    }

    public void DFS(int node, Graph graph, boolean[] visited) {
        visited[node] = true;
        System.out.print(node + " ");

        Node current = graph.nodes[node];

        for (int i = 0; i < current.size; i++) {
            Edge edge = current.adjacency[i];

            if (!visited[edge.to]) {
                DFS(edge.to, graph, visited);
            }
        }
    }


    private Graph reverse() {
        Graph reversed = new Graph(true);

        for (int i = 0; i < size; i++) {
            reversed.addNode();
        }

        for (int i = 0; i < size; i++) {
            Node node = nodes[i];

            for (int j = 0; j < node.size; j++) {
                Edge edge = node.adjacency[j];

                reversed.addEdge(edge.to, i, edge.weight);
            }
        }

        return reversed;
    }

    public void dijkstra(int start) {
        checkVertex(start);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < nodes[i].size; j++) {
                if (nodes[i].adjacency[j].weight < 0) {
                    throw new IllegalArgumentException(
                            "Dijkstra requires non-negative weights"
                    );
                }
            }
        }

        long[] distance = new long[size];
        int[] previous = new int[size];

        java.util.Arrays.fill(distance, Long.MAX_VALUE);
        java.util.Arrays.fill(previous, -1);

        java.util.PriorityQueue<VertexDist> queue =
                new java.util.PriorityQueue<>(
                        (a, b) -> Long.compare(a.cur_dist, b.cur_dist)
                );

        distance[start] = 0;
        queue.offer(new VertexDist(start, 0));

        while (!queue.isEmpty()) {
            VertexDist state = queue.poll();
            int current = state.node;

            if (state.cur_dist != distance[current]) {
                continue;
            }

            Node node = nodes[current];

            for (int i = 0; i < node.size; i++) {
                Edge edge = node.adjacency[i];

                long newDistance = distance[current] + edge.weight;

                if (newDistance < distance[edge.to]) {
                    distance[edge.to] = newDistance;
                    previous[edge.to] = current;

                    queue.offer(
                            new VertexDist(edge.to, newDistance)
                    );
                }
            }
        }

        for (int v = 0; v < size; v++) {
            if (distance[v] == Long.MAX_VALUE) {
                System.out.println(v + ": unreachable");
            } else {
                System.out.print(
                        v + ": distance = " + distance[v] + ", path = "
                );

                printPath(v, previous);
                System.out.println();
            }
        }
    }

    private void printPath(int vertex, int[] previous) {
        java.util.List<Integer> path = new java.util.ArrayList<>();

        for (int at = vertex; at != -1; at = previous[at]) {
            path.add(at);
        }

        java.util.Collections.reverse(path);

        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }

            System.out.print(path.get(i));
        }
    }

    private class VertexDist{
        int node;
        long cur_dist;

        VertexDist(int node, long cur_dist){
            this.node = node;
            this.cur_dist = cur_dist;
        }
    }
}


