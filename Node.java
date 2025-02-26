import java.util.HashMap;
import java.util.Map;

public class Node {

    private Graph S;
    private Graph G;
    private Node left, right;  
    private Node parent;     
    private int[] interval;

    private Map<Integer, Integer> mapping; 

    private Map<Integer, Integer> leftMapping = new HashMap<>();
    private Map<Integer, Integer> rightMapping = new HashMap<>();

    private Map<Integer, Integer> fingerprints; 

    public Map<Integer, Integer> getFingerprints() {
        return fingerprints;
    }
    
    public void setFingerprints(Map<Integer, Integer> fingerprints) {
        this.fingerprints = fingerprints;
    }
    
    public Node(int[] interval) {
        this.interval=interval;
    }

    public void setLeft(Node left){
        this.left=left;
    }        

    public void setRight(Node right){
        this.right=right;
    }        


    public void setParent(Node parent){
        this.parent=parent;
    }

	public int[] getInterval() {
		return interval;
	}

	public Node getLeft() {
		return left;
	}

    public Node getRight() {
        return right;
    } 
    
    public Graph getS(){return S;}
    public Graph getG(){return G;}

    public void setS(Graph S){ this.S=S;}

    public void setG(Graph intersection) {
        this.G=intersection;
    }

    public Map<Integer, Integer> getMapping() {
        return mapping;
    }
    
    public void setMapping(Map<Integer, Integer> mapping) {
        this.mapping = mapping;
    }





    public Map<Integer, Integer> getLeftMapping() {
        return leftMapping;
    }

    public void setLeftMapping(Map<Integer, Integer> leftMapping) {
        this.leftMapping = leftMapping;
    }

    public Map<Integer, Integer> getRightMapping() {
        return rightMapping;
    }

    public void setRightMapping(Map<Integer, Integer> rightMapping) {
        this.rightMapping = rightMapping;
    }
}