package datastructures;

import java.util.*;


public class Tests {
    private static int passed, failed;
    private Tests() {}

    public static void run() {
        passed=0; failed=0;
        test("DSU: singleton initialization and growth", () -> {
            DisjointSetUnion d=dsu(1000);
            for(int i=0;i<1000;i++) check(d.find_set(i)==i && d.size[i]==1,"Incorrect singleton");
            expect(IllegalArgumentException.class,()->d.make_set(0));
            expect(IllegalArgumentException.class,()->d.make_set(-1));
        });
        test("DSU: union by size, smaller root supplied first", () -> {
            DisjointSetUnion d=dsu(5); d.union_sets(1,2); d.union_sets(1,3);
            int large=d.find_set(1); d.union_sets(0,1);
            check(d.find_set(0)==large,"Smaller tree became parent");
            check(d.size[large]==4,"Wrong group size");
            d.union_sets(0,3); check(d.size[large]==4,"Repeated union changed size");
            check(d.find_set(4)!=large,"Unrelated vertex merged");
        });
        test("DSU: path compression", () -> {
            DisjointSetUnion d=dsu(4);
            d.union_sets(0,1); d.union_sets(2,3); d.union_sets(0,2);
            check(d.parent[3]==2,"Test must start with a two-hop path");
            int root=d.find_set(3);
            check(d.parent[3]==root && root==d.find_set(0),"Path not compressed");
        });
        test("Graph: directed and undirected edge storage", () -> {
            Graph g=graph(2,true); g.addEdge(0,1,7);
            check(g.nodes[0].size==1 && g.nodes[1].size==0,"Directed edge reversed");
            Graph u=graph(2,false); u.addEdge(0,1,7);
            Edge e=u.nodes[1].adjacency[0];
            check(e.from==1 && e.to==0 && e.weight==7,"Wrong reverse edge");
            check(u.getSortedEdges().length==1,"Undirected edge counted twice");
        });
        test("Graph: invalid indices do not partially insert edges", () -> {
            Graph g=graph(2,false);
            expect(IndexOutOfBoundsException.class,()->g.addEdge(0,2,1));
            expect(IndexOutOfBoundsException.class,()->g.addEdge(-1,0,1));
            check(g.nodes[0].size==0 && g.nodes[1].size==0,"Partial insertion");
        });
        test("MST: known graph and repeated calls", () -> {
            Graph g=sample(false); mst(g,4); mst(g,4);
        });
        test("MST: single vertex", () -> mst(graph(1,false),0));
        test("MST: multiple valid MSTs and equal weights", () -> {
            Graph g=graph(4,false);
            g.addEdge(0,1,1);g.addEdge(1,2,1);g.addEdge(2,3,1);g.addEdge(3,0,1);
            g.addEdge(0,2,1); mst(g,3);
        });
        test("MST: negative weights, parallel edges and self-loop", () -> {
            Graph g=graph(3,false);
            g.addEdge(0,0,-100);g.addEdge(0,1,5);g.addEdge(0,1,-2);g.addEdge(1,2,0);
            mst(g,-2);
        });
        test("MST: disconnected graph rejected", () -> {
            Graph g = graph(4, false);

            g.addEdge(0, 1, 2);
            g.addEdge(2, 3, 3);

            expect(
                    IllegalStateException.class,
                    () -> g.Kruskal(g.getSortedEdges())
            );

            expect(
                    IllegalStateException.class,
                    () -> g.prim(0)
            );
        });
        test("MST: rejects directed graph", () -> {
            Graph g=graph(2,true);g.addEdge(0,1,1);
            expect(IllegalStateException.class,()->g.prim(0));
            expect(IllegalStateException.class,()->g.Kruskal(g.getSortedEdges()));
        });
        test("Kahn: DAG, isolated vertex and repeated calls", () -> {
            Graph g=graph(6,true);
            g.addEdge(0,2,1);g.addEdge(1,2,1);g.addEdge(2,3,1);g.addEdge(1,4,1);
            topo(g,g.Kahn());topo(g,g.Kahn());
        });
        test("Kahn: cycle with an independent zero-indegree vertex", () -> {
            Graph g=graph(3,true);g.addEdge(1,2,1);g.addEdge(2,1,1);
            check(g.Kahn()==null,"Cycle missed");
        });
        test("Kahn: self-loop and parallel edges", () -> {
            Graph g=graph(1,true);g.addEdge(0,0,1);check(g.Kahn()==null,"Self-loop missed");
            Graph h=graph(2,true);h.addEdge(0,1,1);h.addEdge(0,1,2);topo(h,h.Kahn());
        });
        test("Kosaraju: four SCCs, isolated vertex, repeated calls", () -> {
            Graph g=graph(8,true);
            g.addEdge(0,1,1);g.addEdge(1,2,1);g.addEdge(2,0,1);g.addEdge(2,3,1);
            g.addEdge(3,4,1);g.addEdge(4,5,1);g.addEdge(5,3,1);g.addEdge(5,6,1);
            scc(g,new int[][]{{0,1,2},{3,4,5},{6},{7}});
            scc(g,new int[][]{{0,1,2},{3,4,5},{6},{7}});
            g.addEdge(5,0,1);scc(g,new int[][]{{0,1,2,3,4,5},{6},{7}});
        });
        test("Kosaraju: DAG and self-loop", () -> {
            Graph g=graph(3,true);g.addEdge(0,1,1);g.addEdge(1,2,1);
            scc(g,new int[][]{{0},{1},{2}});
            g.addEdge(1,1,1);scc(g,new int[][]{{0},{1},{2}});
        });
        test("Dijkstra: known distances, paths, unreachable vertex", () -> {
            Graph g=sample(true);g.addNode();
            distances(g,0,new long[]{0,3,1,4,Long.MAX_VALUE});
            distances(g,0,new long[]{0,3,1,4,Long.MAX_VALUE});
            distances(g,3,new long[]{Long.MAX_VALUE,Long.MAX_VALUE,Long.MAX_VALUE,0,Long.MAX_VALUE});
        });
        test("Dijkstra: zero cycle, parallel edges and self-loop", () -> {
            Graph g=graph(3,true);g.addEdge(0,0,0);g.addEdge(0,1,5);g.addEdge(0,1,0);
            g.addEdge(1,0,0);g.addEdge(1,2,2);
            distances(g,0,new long[]{0,0,2});
        });
        test("Dijkstra: long distances exceed int range", () -> {
            Graph g=graph(3,true);g.addEdge(0,1,Integer.MAX_VALUE);g.addEdge(1,2,Integer.MAX_VALUE);
            distances(g,0,new long[]{0,Integer.MAX_VALUE,2L*Integer.MAX_VALUE});
        });
        test("Dijkstra: negative edge and invalid source rejected", () -> {
            Graph g=graph(2,true);g.addEdge(0,1,-1);
            expect(IllegalArgumentException.class,()->g.dijkstra(0));
            expect(IndexOutOfBoundsException.class,()->g.dijkstra(-1));
            expect(IndexOutOfBoundsException.class,()->g.dijkstra(2));
        });
        test("Empty graph and singleton traversal contracts", () -> {
            Graph g=graph(0,true);topo(g,g.Kahn());scc(g,new int[0][]);
            expect(IndexOutOfBoundsException.class,()->g.dijkstra(0));
            expect(IndexOutOfBoundsException.class,()->graph(0,false).prim(0));
            Graph one=graph(1,true);topo(one,one.Kahn());scc(one,new int[][]{{0}});
            distances(one,0,new long[]{0});
        });
        test("Large graph: 1000 vertices with independently known answers", () -> {
            Graph u=graph(1000,false),d=graph(1000,true);
            long[] expected=new long[1000];int[][] singles=new int[1000][1];
            for(int i=0;i<1000;i++) {
                expected[i]=i;singles[i][0]=i;
                if(i>0){u.addEdge(i-1,i,1);d.addEdge(i-1,i,1);}
            }
            mst(u,999);topo(d,d.Kahn());scc(d,singles);distances(d,0,expected);
        });
        System.out.printf("%nTests: %d passed, %d failed%n",passed,failed);
        if(failed>0) throw new AssertionError(failed+" test group(s) failed");
    }

    private static void test(String name,Runnable action) {
        try {action.run();passed++;System.out.println("PASS: "+name);}
        catch(AssertionError | RuntimeException ex) {failed++;System.out.println("FAIL: "+name+" — "+ex);}
    }
    private static void check(boolean ok,String message) {if(!ok)throw new AssertionError(message);}
    private static void expect(Class<? extends Throwable> type,Runnable action) {
        try {action.run();} catch(Throwable ex) {
            if(type.isInstance(ex))return;
            throw new AssertionError("Expected "+type.getSimpleName()+", got "+ex,ex);
        }
        throw new AssertionError("Expected "+type.getSimpleName());
    }
    private static Graph graph(int n,boolean directed) {
        Graph g=new Graph(directed);g.quiet=true;
        for(int i=0;i<n;i++)g.addNode();return g;
    }
    private static DisjointSetUnion dsu(int n) {
        DisjointSetUnion d=new DisjointSetUnion();for(int i=0;i<n;i++)d.make_set(i);return d;
    }
    private static Graph sample(boolean directed) {
        Graph g=graph(4,directed);
        g.addEdge(0,1,4);g.addEdge(0,2,1);g.addEdge(2,1,2);g.addEdge(1,3,1);g.addEdge(2,3,5);
        return g;
    }
    private static void mst(Graph g,long expected) {
        check(g.Kruskal(g.getSortedEdges())==expected,"Wrong Kruskal weight");
        for(int i=0;i<g.size;i++)check(g.dsu.find_set(i)==g.dsu.find_set(0),"MST did not connect vertices");
        for(int start=0;start<Math.min(g.size,3);start++)
            check(g.prim(start)==expected,"Wrong Prim weight from "+start);
    }
    private static void topo(Graph g,int[] order) {
        check(order!=null && order.length==g.size,"Missing full ordering");
        boolean[] seen=new boolean[g.size];int[] position=new int[g.size];
        for(int i=0;i<order.length;i++) {
            int v=order[i];check(v>=0 && v<g.size && !seen[v],"Invalid permutation");
            seen[v]=true;position[v]=i;
        }
        for(int i=0;i<g.size;i++)for(int j=0;j<g.nodes[i].size;j++)
            check(position[i]<position[g.nodes[i].adjacency[j].to],"Edge violates ordering");
    }
    private static void scc(Graph g,int[][] expected) {
        g.Kosaraju();boolean[] seen=new boolean[g.size];int count=0;
        Set<Set<Integer>> actual=new HashSet<>(),wanted=new HashSet<>();
        for(List<Integer> list:g.lastComponents) {
            check(!list.isEmpty(),"Empty SCC");Set<Integer> group=new HashSet<>();
            for(int v:list) {
                check(v>=0 && v<g.size && !seen[v],"Repeated or invalid SCC vertex");
                seen[v]=true;count++;group.add(v);
            }
            actual.add(group);
        }
        for(int[] row:expected) {Set<Integer> group=new HashSet<>();for(int v:row)group.add(v);wanted.add(group);}
        check(count==g.size && actual.equals(wanted),"Incorrect SCC partition");
    }
    private static void distances(Graph g,int source,long[] expected) {
        g.dijkstra(source);check(Arrays.equals(expected,g.lastDistances),"Wrong shortest distances");
        check(g.lastPrevious[source]==-1,"Source has predecessor");
        for(int v=0;v<g.size;v++) {
            if(expected[v]==Long.MAX_VALUE) {check(g.lastPrevious[v]==-1,"Unreachable vertex has predecessor");continue;}
            int at=v,steps=0;long weight=0;
            while(at!=source) {
                check(++steps<g.size,"Predecessor chain cycles");
                int prev=g.lastPrevious[at];check(prev>=0 && prev<g.size,"Invalid predecessor");
                boolean found=false;
                for(int j=0;j<g.nodes[prev].size;j++) {
                    Edge e=g.nodes[prev].adjacency[j];
                    if(e.to==at && expected[prev]+e.weight==expected[at]) {weight+=e.weight;found=true;break;}
                }
                check(found,"Predecessor edge missing or wrong weight");at=prev;
            }
            check(weight==expected[v],"Reconstructed path has wrong weight");
        }
    }
}
