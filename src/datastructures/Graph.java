package datastructures;

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
}


