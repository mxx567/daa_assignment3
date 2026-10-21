package datastructures;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Experiments {
    private static final int RUNS = 5, WARMUPS = 3;
    private static final long SEED = 56;
    private static volatile long sink;

    public static void run() throws IOException {
        Files.createDirectories(Paths.get("results/tables"));
        try (PrintWriter raw = new PrintWriter("results/tables/raw.csv");
             PrintWriter summary = new PrintWriter("results/tables/summary.csv")) {
            raw.println("algorithm,graph,V,E,run,time_ns,edge_checks,find_calls,union_calls");
            summary.println("algorithm,graph,V,E,avg_ns,min_ns,max_ns,avg_edge_checks,avg_find_calls,avg_union_calls");
            int[][] sizes = {{10,30},{100,500},{1000,5000}};
            for (int[] size : sizes) {
                int v = size[0], e = size[1];
                Graph undirected = generate(v,e,"connected");
                Graph dag = generate(v,e,"dag");
                Graph cyclic = generate(v,e,"cyclic");
                verify(undirected, dag, cyclic);
                measure("Kruskal","connected",undirected,e,raw,summary);
                measure("Prim","connected",undirected,e,raw,summary);
                measure("Kahn","dag",dag,e,raw,summary);
                measure("Kahn","cyclic",cyclic,e,raw,summary);
                measure("Kosaraju","dag",dag,e,raw,summary);
                measure("Kosaraju","cyclic",cyclic,e,raw,summary);
                measure("Dijkstra","connected",undirected,e,raw,summary);
            }
        }
        Files.write(Paths.get("results/environment.txt"), Arrays.asList(
            "Java: " + System.getProperty("java.version"),
            "VM: " + System.getProperty("java.vm.name"),
            "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"),
            "Available processors: " + Runtime.getRuntime().availableProcessors(),
            "Seed: " + SEED + "; warmups: " + WARMUPS + "; measured runs: " + RUNS));
        System.out.println("Completed: 21 configurations, 105 measured runs. CSV files: results/tables/");
    }

    private static void execute(String algorithm, Graph g) {
        switch (algorithm) {
            case "Kruskal": sink = g.Kruskal(g.getSortedEdges()); break;
            case "Prim": sink = g.prim(0); break;
            case "Kahn": int[] order = g.Kahn(); sink = order == null ? -1 : order.length; break;
            case "Kosaraju": g.Kosaraju(); sink = g.lastComponents.size(); break;
            case "Dijkstra": g.dijkstra(0); sink = g.lastDistances[g.size-1]; break;
            default: throw new IllegalArgumentException(algorithm);
        }
    }

    private static void measure(String algorithm, String kind, Graph g, int e,
                                PrintWriter raw, PrintWriter summary) {
        for (int i=0; i<WARMUPS; i++) execute(algorithm,g);
        long total=0,min=Long.MAX_VALUE,max=0,checks=0,finds=0,unions=0;
        for (int run=1; run<=RUNS; run++) {
            long start=System.nanoTime();
            execute(algorithm,g);
            long elapsed=System.nanoTime()-start;
            long f=algorithm.equals("Kruskal")?g.dsu.findCalls:0;
            long u=algorithm.equals("Kruskal")?g.dsu.unionCalls:0;
            raw.printf(Locale.ROOT,"%s,%s,%d,%d,%d,%d,%d,%d,%d%n",
                algorithm,kind,g.size,e,run,elapsed,g.edgeChecks,f,u);
            total+=elapsed; min=Math.min(min,elapsed); max=Math.max(max,elapsed);
            checks+=g.edgeChecks; finds+=f; unions+=u;
        }
        summary.printf(Locale.ROOT,"%s,%s,%d,%d,%.1f,%d,%d,%.1f,%.1f,%.1f%n",
            algorithm,kind,g.size,e,total/(double)RUNS,min,max,
            checks/(double)RUNS,finds/(double)RUNS,unions/(double)RUNS);
        System.out.printf(Locale.ROOT,"%-9s %-9s V=%4d E=%4d average=%9.3f ms checks=%d%n",
            algorithm,kind,g.size,e,total/(RUNS*1e6),checks/RUNS);
    }

    private static Graph generate(int v,int e,String kind) {
        boolean directed=!kind.equals("connected");
        Graph g=new Graph(directed); g.quiet=true;
        for(int i=0;i<v;i++) g.addNode();
        Random random=new Random(SEED);
        Set<Long> used=new HashSet<>();
        if (!directed) {
            // A random parent for every vertex guarantees connectivity.
            for(int i=1;i<v;i++) add(g,i,random.nextInt(i),random,used);
        } else if (kind.equals("cyclic")) {
            // A directed ring guarantees a cycle and exactly one SCC.
            for(int i=0;i<v;i++) add(g,i,(i+1)%v,random,used);
        }
        while(used.size()<e) {
            int a=random.nextInt(v), b=random.nextInt(v);
            if(a==b) continue;
            if(kind.equals("dag") && a>b) {int t=a;a=b;b=t;}
            add(g,a,b,random,used);
        }
        return g;
    }

    private static void add(Graph g,int a,int b,Random random,Set<Long> used) {
        int x=a,y=b;
        if(!g.isDirected && x>y) {int t=x;x=y;y=t;}
        long key=((long)x<<32)|(y&0xffffffffL);
        if(used.add(key)) g.addEdge(a,b,random.nextInt(100)+1);
    }

    private static void require(boolean ok,String message) {
        if(!ok) throw new AssertionError(message);
    }

    private static void verify(Graph g,Graph dag,Graph cyclic) {
        int k=g.Kruskal(g.getSortedEdges());
        require(k==g.prim(0),"MST weights differ");
        require(k==g.Kruskal(g.getSortedEdges()),"Kruskal is not repeatable");
        int[] order=dag.Kahn();
        require(order!=null && order.length==dag.size,"Missing DAG order");
        int[] positions=new int[dag.size]; boolean[] seen=new boolean[dag.size];
        for(int i=0;i<order.length;i++) {
            require(order[i]>=0 && order[i]<dag.size && !seen[order[i]],"Invalid permutation");
            seen[order[i]]=true; positions[order[i]]=i;
        }
        for(int i=0;i<dag.size;i++) for(int j=0;j<dag.nodes[i].size;j++)
            require(positions[i]<positions[dag.nodes[i].adjacency[j].to],"Invalid topological order");
        require(cyclic.Kahn()==null,"Cycle missed");
        dag.Kosaraju(); checkPartition(dag);
        require(dag.lastComponents.size()==dag.size,"A DAG must have singleton SCCs");
        cyclic.Kosaraju(); checkPartition(cyclic);
        require(cyclic.lastComponents.size()==1,"Ring graph must have one SCC");
        // Independent Bellman-Ford reference; outside all timing.
        long[] ref=new long[g.size]; Arrays.fill(ref,Long.MAX_VALUE); ref[0]=0;
        for(int pass=1;pass<g.size;pass++) {
            boolean changed=false;
            for(int i=0;i<g.size;i++) if(ref[i]!=Long.MAX_VALUE)
                for(int j=0;j<g.nodes[i].size;j++) {
                    Edge edge=g.nodes[i].adjacency[j];
                    if(ref[i]+edge.weight<ref[edge.to]) {
                        ref[edge.to]=ref[i]+edge.weight;changed=true;
                    }
                }
            if(!changed) break;
        }
        g.dijkstra(0);
        require(Arrays.equals(ref,g.lastDistances),"Dijkstra distances differ from Bellman-Ford");
        for(int v=1;v<g.size;v++) {
            int at=v, steps=0;
            while(at!=0) {
                require(++steps<g.size,"Predecessor cycle");
                int prev=g.lastPrevious[at]; require(prev>=0,"Missing predecessor");
                boolean matches=false;
                for(int j=0;j<g.nodes[prev].size;j++) {
                    Edge edge=g.nodes[prev].adjacency[j];
                    if(edge.to==at && ref[prev]+edge.weight==ref[at]) matches=true;
                }
                require(matches,"Invalid predecessor edge"); at=prev;
            }
        }
    }

    private static void checkPartition(Graph g) {
        boolean[] seen=new boolean[g.size]; int count=0;
        for(List<Integer> component:g.lastComponents) for(int v:component) {
            require(v>=0 && v<g.size && !seen[v],"Invalid SCC partition"); seen[v]=true;count++;
        }
        require(count==g.size,"Missing SCC vertex");
    }
}
