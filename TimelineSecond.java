import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimelineSecond {

    private Graph G;



    private List<Object[]> timeList;

    public TimelineSecond(Graph G, List<Object[]> timeList) {
        this.G = G;
        this.timeList = new ArrayList<>();
        for (Object[] element : timeList) {
            if (element.length != 3) {
                throw new IllegalArgumentException("Each element must be represented by an array of size 3 (two numbers and an operator)");
            }
            int num1 = (int) element[0];
            int num2 = (int) element[1];
            String operator = (String) element[2];
            if (!operator.equals("+") && !operator.equals("-")) {
                throw new IllegalArgumentException("Operator must be '+' or '-'");
            }
            this.timeList.add(new Object[]{num1, num2, operator});
        }
    }

    public TimelineSecond(Graph G, String filename) throws IOException {
        this.G = G;
        this.timeList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("Timeline:")) {
                // Parse the timeline
                String[] events = line.substring(9).split("\\),\\(");
                for (String event : events) {
                    event = event.replaceAll("\\(|\\)", ""); 
                    String[] data = event.split(",");
                    int v = Integer.parseInt(data[0]);
                    int w = Integer.parseInt(data[1]);
                    String operation = data[2];
                    timeList.add(new Object[]{v, w, operation});
                }
            }
        }
        br.close();
    }



    private void applyOperation(Graph graph, Object[] operation) {
        int num1 = (int) operation[0];
        int num2 = (int) operation[1];
        String operator = (String) operation[2];

        if (operator.equals("+")) {
            graph.addEdge(num1, num2);
        } else if (operator.equals("-")) {
            graph.removeEdge(num1, num2);
        }
    }

    private Graph cloneGraph(Graph original) {
        Graph clone = new Graph(original.getV());
        for (String edgeStr : original.getEdges()) {
            String[] vertices = edgeStr.split("-");
            if (vertices.length == 2) {
                int v = Integer.parseInt(vertices[0]);
                int w = Integer.parseInt(vertices[1]);
                clone.addEdge(v, w);
            }
        }
        return clone;
    }

    public void changeGraph(Graph graph, int counter) {
        //apo thn arxh mexri ton counter
        for (int i = 0; i < counter; i++) {

            int num1 = (int) timeList.get(i)[0];
            int num2 = (int) timeList.get(i)[1];
            String operator = (String) timeList.get(i)[2];


            if (operator.equals("+")) {
                graph.addEdge(num1, num2);
            } else if (operator.equals("-")) {
                graph.removeEdge(num1, num2);
            }
        }
    }


    public Graph intersection(int a, int b) {
        // Validate indices.
        if (a < 0 || b > timeList.size() || a > b) {
            throw new IllegalArgumentException("Invalid interval: [" + a + ", " + b + "]");
        }
        
        Graph versionA = cloneGraph(G);
        for (int i = 0; i < a; i++) {
            applyOperation(versionA, timeList.get(i));
        }
        
        Set<String> commonEdges = new HashSet<>(versionA.getEdges());
        

        Graph currentVersion = versionA;
        for (int i = a + 1; i <= b; i++) {
            applyOperation(currentVersion, timeList.get(i - 1));
            Set<String> currentEdges = currentVersion.getEdges();
            commonEdges.retainAll(currentEdges); // krataei mono ayta sta current
        }
        
        Graph intersectGraph = new Graph(G.V());
        for (String edge : commonEdges) {
            String[] parts = edge.split("-");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            intersectGraph.addEdge(u, v);
        }
        
        return intersectGraph;
    }

    public List<Object[]> getTimeList() {
        return timeList;
    }

}
