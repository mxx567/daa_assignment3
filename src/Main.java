import datastructures.DisjointSetUnion;
import datastructures.Graph;

public class Main {
    public static void main(String[] args){
//        Graph g = new Graph(false
//        );
//        g.addNode();
//        g.addNode();
//        g.addNode();
//        g.addNode();
//
//        g.addEdge(0,1,4);
//        g.addEdge(0,2,1);
//        g.addEdge(2,1,2);
//        g.addEdge(1,3,1);
//        g.addEdge(2,3,5);
//
//
//        System.out.println(g.Kruskal(g.getSortedEdges()));
//        g.prim(0);
//        g.view();
//

        Graph g = new Graph(true);

        for (int i = 0; i < 6; i++) {
            g.addNode();
        }
        g.addEdge(0, 2, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 4, 1);
        g.addEdge(3, 4, 1);
        g.addEdge(4, 5, 1);

        g.addEdge(5, 1, 1);

        int[] ord = g.Kahn();
        for(int o: ord){
            System.out.print(o + " ");
        }
    }
}
