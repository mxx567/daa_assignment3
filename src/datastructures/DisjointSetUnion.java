package datastructures;

public class DisjointSetUnion {
    public long findCalls, unionCalls;

    public int[] parent = new int[0];
    int[] size = new int[0];
    int len;


    private void ensureCapacity(int v) {
        if (v < parent.length) {
            return;
        }

        int newCapacity = Math.max(v + 1, Math.max(1, parent.length * 2));

        int[] newParent = new int[newCapacity];
        int[] newSize = new int[newCapacity];

        System.arraycopy(parent, 0, newParent, 0, parent.length);
        System.arraycopy(size, 0, newSize, 0, size.length);

        parent = newParent;
        size = newSize;
    }

    public void make_set(int v) {
        if (v < 0) {
            throw new IllegalArgumentException("Element ID cannot be negative");
        }

        ensureCapacity(v);

        if (size[v] != 0) {
            throw new IllegalArgumentException("Element already exists: " + v);
        }

        parent[v] = v;
        size[v] = 1;
    }

    public void union_sets(int a, int b) {
        unionCalls++;
        a = find_set(a);
        b = find_set(b);
        if (a != b) {
            if (size[a] < size[b]) {
                int t = a;
                a = b;
                b = t;
            }
            parent[b] = a;
            size[a] += size[b];
        }
    }

    public int find_set(int v) {
        findCalls++;
        if (v == parent[v]) {
            return v;
        }
        return parent[v] = find_set(parent[v]);
    }



}
