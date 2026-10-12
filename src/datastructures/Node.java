package datastructures;

class Node{
    Edge[] adjacency = new Edge[0];
    public int size = 0;

    void add(Edge e){
        if(adjacency.length == size){
            Edge[] newAdj = new Edge[Math.max(1, adjacency.length) * 2];
            System.arraycopy(adjacency, 0, newAdj, 0, adjacency.length);
            adjacency = newAdj;
        }
        adjacency[size] = e;
        size++;
    }

    public void getAdjacency() {
        for(Edge e : adjacency){
            if(e != null){
                System.out.println("to: " + e.to + " weight: " + e.weight);
            }
        }
    }
}