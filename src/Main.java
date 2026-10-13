import datastructures.DisjointSetUnion;
import datastructures.Graph;

public class Main {
    public static void main(String[] args){
        Graph g = new Graph(false
        );
        g.addNode();
        g.addNode();
        g.addEdge(0,1,3);
        //g.addEdge(0,9999,3);

        g.view();

        DisjointSetUnion a = new DisjointSetUnion();
        a.make_set(0);
        a.make_set(1);
        a.make_set(2);
        a.union_sets(0,1);
        a.union_sets(1,2);
        System.out.println(a.find_set(2));
    }
}
