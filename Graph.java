import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Graph {

    private int V;
    private int E;
    private ArrayList<ArrayList<Integer>> bag;

    private ArrayList<ArrayList<Integer>> componentsOfS;
    private static final String NEWLINE = System.getProperty("line.separator");

    public Graph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
        this.V = V;
        this.E = 0;
        bag = new ArrayList<>();
        for (int v = 0; v < V; v++) {
            bag.add(new ArrayList<Integer>());
        }
    }

    public Graph(int V,ArrayList<ArrayList<Integer>> componentsOfS) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
        this.V = V;
        this.E = 0;
        bag = new ArrayList<>();
        for (int v = 0; v < V; v++) {
            bag.add(new ArrayList<Integer>());
        }
        this.componentsOfS=componentsOfS;
    }
    public int V() {
        return V;
    }



    public Graph(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("Vertex:")) {
                this.V = Integer.parseInt(line.substring(7));
                bag = new ArrayList<>();
                for (int v = 0; v < V; v++) {
                    bag.add(new ArrayList<>());
                }
            } else if (line.startsWith("Edges:")) {
                String[] edges = line.substring(7).split("\\),\\(");
                for (String edge : edges) {
                    edge = edge.replaceAll("\\(|\\)", ""); 
                    String[] vertices = edge.split(",");
                    int v = Integer.parseInt(vertices[0]);
                    int w = Integer.parseInt(vertices[1]);
                    addEdge(v, w);
                }
            }
        }
        br.close();
    }


    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        
        if (!bag.get(v).contains(w)) { 
            E++;
            bag.get(v).add(w);
            bag.get(w).add(v);
        }
    }

    public void removeEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if (bag.get(v).contains(w)) {
            bag.get(v).remove(Integer.valueOf(w)); 
            E = E - 1;

        }
        if (bag.get(w).contains(v)) {
            bag.get(w).remove(Integer.valueOf(v)); 


        }
    }


    public int degree(int v) {
        return bag.size();
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (int w : bag.get(v)) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }


    public ArrayList<ArrayList<Integer>> findComponents() {
        //Ta components einai mia arraylist me Arraylist. Kaue eswterkko arraylist einai ena component. Etsi kaue component exei integer poy kaue integer antiproswpeuei ena vertex


        ArrayList<ArrayList<Integer>> components = new ArrayList<>();
        ArrayList<Integer> visited = new ArrayList<>();

        for (int v = 0; v < V; v++) {
            if (!visited.contains(v)) {
                ArrayList<Integer> component = new ArrayList<Integer>();
                dfs(v, visited, component);
                components.add(component);

            }
        }

        return components;
    }

    private void dfs(int v, ArrayList<Integer> visited, ArrayList<Integer> component) {
        visited.add(v);
        component.add(v);
        for (int neighbor : bag.get(v)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, component);
            }
        }
    }

    public Set<String> getEdges() {
        Set<String> edges = new HashSet<>();
        for (int v = 0; v < V; v++) {
            for (int w : bag.get(v)) {
                if (v < w) { 
                    edges.add(v + "-" + w);
                }
            }
        }
        return edges;
    }

    public int getV() {
        return V;
    }
    public boolean hasEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return bag.get(v).contains(w);
    }

    /*public static void main(String[] args) throws IOException {
        int a = 4;
        Graph G = new Graph(a);
        G.addEdge(1, 2);
        G.addEdge(1, 2);
        System.out.println(G);


        String filename = "D:\\Gitihub\\Fast and simple connectivity in graph timelines\\Fast-and-simple-connectivity-in-graph-timelines\\GraphReader.txt";
        Graph G2 = new Graph(filename);
        System.out.println(G2);
        System.out.println(G2.findComponents());
        System.out.println();
        System.out.println(G.findComponents());


        

    }*/


}






















