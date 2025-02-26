import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

//import javafx.util.Pair;

public class CompleteBinaryTreeSecond {
    private Node root;
    private TimelineSecond timeline;

    //private Graph graph;

    public CompleteBinaryTreeSecond(int[] interval, TimelineSecond timeline) {
        root = new Node(interval);
        this.timeline=timeline;
    }
    public void constructTree(int[] interval) {
        root = buildTree(interval);
    }

    // Recursive method to build the binary tree
    private Node buildTree(int[] interval) {
        int start = interval[0];
        int end = interval[interval.length - 1];
        if (start > end) {
            return null;
        }

        int mid = start + (end - start) / 2; 

        int[] interval1 = new int[]{start, mid}; 
        int[] interval2 = new int[]{mid + 1, end}; 
        Node node = new Node(interval);

        if (start != end) {
            node.setLeft(buildTree(interval1));
            node.setRight(buildTree(interval2));
        }

        return node;
    }

    public Graph reduceGraph(Graph graph, int start, int end) { //krataei ta vertices apo ta edges poy allazoyn kai ta vertices apo to intersection
        int totalVertices = graph.getV(); boolean[] active = new boolean[totalVertices];
        for (int i = start; i <= Math.min(end, timeline.getTimeList().size() - 1); i++) {
            Object[] operation = timeline.getTimeList().get(i);
            int v1 = (int) operation[0];
            int v2 = (int) operation[1];
            active[v1] = true;
            active[v2] = true;
        }


        for (String edge : graph.getEdges()) {
            String[] parts = edge.split("-");
            int v1 = Integer.parseInt(parts[0]);
            int v2 = Integer.parseInt(parts[1]);
            active[v1] = true;
            active[v2] = true;
        }

        Map<Integer, Integer> mapping = new HashMap<>();
        int newIndex = 0;
        for (int v = 0; v < totalVertices; v++) {
            if (active[v]) {
                mapping.put(v, newIndex);
                newIndex++;
            }
        }

        Graph reducedGraph = new Graph(newIndex);
        for (String edge : graph.getEdges()) {
            String[] parts = edge.split("-");
            int v1 = Integer.parseInt(parts[0]);
            int v2 = Integer.parseInt(parts[1]);
            if (active[v1] && active[v2]) {
                int newV1 = mapping.get(v1);
                int newV2 = mapping.get(v2);
                reducedGraph.addEdge(newV1, newV2);
            }
        }
        System.out.println("✅ Reduced Graph for interval [" + start + "," + end + "]: " + reducedGraph);
        return reducedGraph;
    }




    //Vriskei ta components meta to reduction
    private Pair<Graph, Map<Integer, Integer>> contractGraph(Graph graph) {
    ArrayList<ArrayList<Integer>> components = graph.findComponents();

    Map<Integer, Integer> mapping = new HashMap<>();
    for (int compID = 0; compID < components.size(); compID++) {
        for (int v : components.get(compID)) {
            mapping.put(v, compID);
        }
    }

    Graph contractedGraph = new Graph(components.size());
    Set<String> addedEdges = new HashSet<>();

    for (String edge : graph.getEdges()) {
        String[] tokens = edge.split("-");
        int u = Integer.parseInt(tokens[0]);
        int v = Integer.parseInt(tokens[1]);
        int compU = mapping.get(u);
        int compV = mapping.get(v);
        if (compU != compV) {
            // Create a canonical representation so that edge order does not matter.
            String canonicalEdge = (compU < compV) ? compU + "-" + compV : compV + "-" + compU;
            if (!addedEdges.contains(canonicalEdge)) {
                contractedGraph.addEdge(compU, compV);
                addedEdges.add(canonicalEdge);
            }
        }
    }

    System.out.println("✅ Contracted Graph (components contracted): " + contractedGraph);
    return new Pair<>(contractedGraph, mapping);
}
    
    
    


    
    


private void createG(Node node) {
    if (node == null) return;

    Graph intersectionGraph = this.timeline.intersection(node.getInterval()[0], node.getInterval()[1]);

    Graph reducedGraph = reduceGraph(intersectionGraph, node.getInterval()[0], node.getInterval()[1]);


    Pair<Graph, Map<Integer, Integer>> contractResult = contractGraph(reducedGraph);
    Graph componentGraph = contractResult.first;
    Map<Integer, Integer> contractMapping = contractResult.second;

    node.setG(intersectionGraph);
    node.setS(componentGraph);
    node.setMapping(contractMapping);  

    createG(node.getLeft());
    createG(node.getRight());

    if (node.getLeft() != null && node.getLeft().getMapping() != null) {
        node.setLeftMapping(node.getLeft().getMapping());
    } else {
        Map<Integer, Integer> identity = new HashMap<>();
        int size = componentGraph.getV();
        for (int i = 0; i < size; i++) {
            identity.put(i, i);
        }
        node.setLeftMapping(identity);
    }
    
    if (node.getRight() != null && node.getRight().getMapping() != null) {
        node.setRightMapping(node.getRight().getMapping());
    } else {
        Map<Integer, Integer> identity = new HashMap<>();
        int size = componentGraph.getV();
        for (int i = 0; i < size; i++) {
            identity.put(i, i);
        }
        node.setRightMapping(identity);
    }
}


    

    private void printTree(Node node) {
        if (node == null) {
            return;
        }

        System.out.print(node.getInterval()[0] + "-" + node.getInterval()[1] + " ");
        printTree(node.getLeft());
        printTree(node.getRight());
    }
    private void printTreeG(Node node) {
        if (node == null) {
            return;
        }
        System.out.println(node.getInterval()[0]+" "+node.getInterval()[1]);
        System.out.print(node.getS());
        printTreeG(node.getLeft());
        printTreeG(node.getRight());
    }

    

  
//Fingerprint gia sygkekrimenh koryfh S sto node
private int computeFingerprint(Node node, int s) {
    int[] interval = node.getInterval();
    // basikh periptvsh fyllo
    if (interval[0] == interval[1]) {
        return mapToInt(s, 0);
    }
    
    // mapping gia ta paidia
    Map<Integer, Integer> leftMap = node.getLeftMapping();
    Map<Integer, Integer> rightMap = node.getRightMapping();
    
    // an leipei kapoio mapping vazoyme to vasiko
    if (leftMap == null || rightMap == null || !leftMap.containsKey(s) || !rightMap.containsKey(s)) {
        return mapToInt(s, 0);
    }
    
    int leftId = leftMap.get(s);
    int rightId = rightMap.get(s);
    
    // Anadromika ypologizoyme ta fingerprints dejia kai aristera
    int leftFingerprint = computeFingerprint(node.getLeft(), leftId);
    int rightFingerprint = computeFingerprint(node.getRight(), rightId);
    
    // Dyo fingerptints se akeraio
    return mapPairToInt(leftFingerprint, rightFingerprint);
}
//to idio alla gia na einai eyanagnwsto
private int mapToInt(int a, int b) {
    int PRIME = 31;
    return a * PRIME + b;
}

private int mapPairToInt(int x, int y) {
    int PRIME = 31;
    return x * PRIME + y;
}

// diatrexei to dentro kai krataei ta fingerprint se maps
private void computeFingerprints(Node node) {
    if (node == null) return;
    Graph S = node.getS();
    if (S == null) return;
    
    Map<Integer, Integer> fp = new HashMap<>();
    for (int i = 0; i < S.getV(); i++) {
         int f = computeFingerprint(node, i);
         fp.put(i, f);
    }
    node.setFingerprints(fp);
    computeFingerprints(node.getLeft());
    computeFingerprints(node.getRight());
}

public Pair<Integer, Node> compId(int v, Node node, int queryTime) {//sygkekrimenh xronikh stigmh sto timeline για μια κορυφη του γραφήματος σε συγκεκριμένο κομβο βρίσκει fingerprint
    int a = node.getInterval()[0];
    int b = node.getInterval()[1];
    

    if (a == b) {
        return new Pair<>(v, node);
    }
    
    int mid = (a + b) / 2;
    
    if (queryTime <= mid) {
        Map<Integer, Integer> leftMap = node.getLeftMapping();

        if (leftMap == null || !leftMap.containsKey(v)) {
            return new Pair<>(v, node);
        } else {
            
            int newV = leftMap.get(v);
            
            return compId(newV, node.getLeft(), queryTime);
        }
    } else {
        
        Map<Integer, Integer> rightMap = node.getRightMapping();
        if (rightMap == null || !rightMap.containsKey(v)) {
            return new Pair<>(v, node);
        } else {
            int newV = rightMap.get(v);
            return compId(newV, node.getRight(), queryTime);
        }
    }
}
    
    public boolean isConnected(int u, int v, int queryTime) {
        Pair<Integer, Node> repU = compId(u, root, queryTime);
        Pair<Integer, Node> repV = compId(v, root, queryTime);
        System.out.println("At queryTime " + queryTime + ": rep(" + u + ") = " + repU + ", rep(" + v + ") = " + repV);
        return repU.first.equals(repV.first) && repU.second.equals(repV.second);
    }
    public Node getRoot(){
        return this.root;
    }
    
    
    public List<Node> getCoveringNodes(Node node, int qStart, int qEnd) {
        List<Node> coveringNodes = new ArrayList<>();
        
        // Base case
        if (node == null) {
            return coveringNodes;
        }
        
        int a = node.getInterval()[0];
        int b = node.getInterval()[1];
        
        // an den exei epikalych epistrefoyme th lista
        if (b < qStart || a > qEnd) {
            return coveringNodes;
        }
        
        // An to node einai entos prosuetoyme kai termatizoyme.
        if (qStart <= a && b <= qEnd) {
            coveringNodes.add(node);
            return coveringNodes;
        }
        
        // Anadromh sta paidia
        coveringNodes.addAll(getCoveringNodes(node.getLeft(), qStart, qEnd));
        coveringNodes.addAll(getCoveringNodes(node.getRight(), qStart, qEnd));

        for (Node n : coveringNodes) {
            int queryTime = n.getInterval()[1]; // ή δοκίμασε και με το μέσο του interval
            Pair<Integer, Node> repU = compId(1, n, queryTime);
            Pair<Integer, Node> repV = compId(3, n, queryTime);
            System.out.println("Node interval: " + Arrays.toString(n.getInterval()) +
                               " repU: " + repU + ", repV: " + repV);
        }
        return coveringNodes;
    }
    


public boolean isConnectedInterval(int u, int v, int intervalStart, int intervalEnd) {
    List<Node> coveringNodes = getCoveringNodes(root, intervalStart, intervalEnd);
    
    for (Node n : coveringNodes) {
        int queryTime = (n.getInterval()[0] + n.getInterval()[1]) / 2;        
        Pair<Integer, Node> repU = compId(u, n, queryTime);
        Pair<Integer, Node> repV = compId(v, n, queryTime);
        
        Map<Integer, Integer> fpMap = n.getFingerprints();
        if (fpMap == null || !fpMap.containsKey(repU.first) || !fpMap.containsKey(repV.first)) {
            if (!repU.first.equals(repV.first) || !repU.second.equals(repV.second)) {
                return false;
            }
        } else {
            int fpU = fpMap.get(repU.first);
            int fpV = fpMap.get(repV.first);
            if (fpU != fpV) return false;
        }
    }
    return true;
}

public static void main(String[] args) {
    // ena arxiko grafhma 5 koryfes 0 ews 4
    Graph graph = new Graph(5);
    //arxikes akmes
    graph.addEdge(0, 1);
    graph.addEdge(1, 2);
    graph.addEdge(2, 3);
    
    System.out.println("Initial Graph:");
    System.out.println(graph);
    
    List<Object[]> timelineOps = new ArrayList<>();
    timelineOps.add(new Object[]{0, 2, "+"});  // 0: add (0,2)
    timelineOps.add(new Object[]{1, 2, "-"});  // 1: remove (1,2)
    timelineOps.add(new Object[]{1, 3, "+"});  // 2: add (1,3)
    timelineOps.add(new Object[]{0, 1, "+"});  // 3: add (0,1)
    timelineOps.add(new Object[]{3, 4, "+"});  // 4: add (3,4)
    timelineOps.add(new Object[]{2, 3, "-"});  // 5: remove (2,3)
    timelineOps.add(new Object[]{1, 3, "-"});  // 6: remove (1,3)
    
    int[] interval = {0, timelineOps.size()};
    
    TimelineSecond timeline = new TimelineSecond(graph, timelineOps);
    
    CompleteBinaryTreeSecond tree = new CompleteBinaryTreeSecond(interval, timeline);
    tree.constructTree(interval);
    
    tree.createG(tree.getRoot());
    
    tree.computeFingerprints(tree.getRoot());
    
    System.out.println("Processed Tree with Contracted Graphs and Mappings:");
    tree.printTreeMapping(tree.getRoot());
    
    System.out.println("\n🔍 Testing Interval Connectivity Queries:");
    
    boolean res1 = tree.isConnectedInterval(0, 1, 0, 3);
    System.out.println("isConnectedInterval(0, 1, [0,3]) -> " + res1);
    
    boolean res2 = tree.isConnectedInterval(0, 2, 0, 3);
    System.out.println("isConnectedInterval(0, 2, [0,3]) -> " + res2);
    
    boolean res3 = tree.isConnectedInterval(3, 4, 4, 6);
    System.out.println("isConnectedInterval(3, 4, [4,6]) -> " + res3);
    
    boolean res4 = tree.isConnectedInterval(0, 4, 0, 7);
    System.out.println("isConnectedInterval(0, 4, [0,7]) -> " + res4);
    
    boolean res5 = tree.isConnectedInterval(1, 3, 2, 5);
    System.out.println("isConnectedInterval(1, 3, [2,5]) -> " + res5);


 
    
    System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    
}



    
     
    
   
    public void printTreeMapping(Node node) {
        if (node == null) return;
        
        Graph intersectionGraph = timeline.intersection(node.getInterval()[0], node.getInterval()[1]);
        Graph reducedGraph = reduceGraph(intersectionGraph, node.getInterval()[0], node.getInterval()[1]);
        Pair<Graph, Map<Integer, Integer>> contractResult = contractGraph(reducedGraph);
        Map<Integer, Integer> mapping = contractResult.second;
        
        System.out.println("Mapping for node " + Arrays.toString(node.getInterval()) + ": " + mapping);
        
        printTreeMapping(node.getLeft());
        printTreeMapping(node.getRight());
    }
    



}
class Pair<U, V> {
    public final U first;
    public final V second;
    
    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
    
    public U getFirst() {
        return first;
    }
    
    public V getSecond() {
        return second;
    }  
    
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
