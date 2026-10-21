import datastructures.DisjointSetUnion;
import datastructures.Experiments;
import datastructures.Graph;

import java.io.IOException;

public class Main {
    public static void main(String[] args){

        try {
            Experiments.run();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
