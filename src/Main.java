import datastructures.Graph;

public class Main {
    public static void main(String[] args){
        Graph g = new Graph(false
        );
        g.addNode();
        g.addNode();
        g.addEdge(0,1,3);
        g.addEdge(0,9999,3);

        g.view();
    }
}
