package datastructures;

public class Graph {
    Node[] nodes = new Node[0];
    public boolean isDirected = false;
    public int size = 0;


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
        size++;
    }

    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);

        get(from).add(new Edge(to, weight));

        if (!isDirected) {
            get(to).add(new Edge(from, weight));
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
}


