import datastructures.DisjointSetUnion;
import datastructures.Graph;

public class Main {
    public static void main(String[] args){
        Graph g = new Graph(false
        );
        g.addNode();
        g.addNode();
        g.addNode();
        g.addNode();
        g.addNode();
        g.addNode();
        g.addNode();

        g.addEdge(0,1,2);
        g.addEdge(1,2,4);
        g.addEdge(0,2, 3);
        g.addEdge(2,3,5);
        g.addEdge(0,3,3);
        g.addEdge(1,4, 3);
        g.addEdge(4,2,1);
        g.addEdge(3,5, 7);
        g.addEdge(5,4, 8);
        g.addEdge(5,6,9);

        System.out.println(g.Kruskal(g.getSortedEdges()));

        g.view();
    }
}
