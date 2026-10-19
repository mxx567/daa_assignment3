package datastructures;

public class Edge {
    int from;
    int to;
    int weight;
    public boolean isVisited;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}
