package projgraph;

import java.util.*;

public class Dictionnaire {

	public HashMap<Integer, Node> nodes;
	public HashMap<Integer, Edge> edges;

	Dictionnaire() {
		nodes = new HashMap<>();
		edges = new HashMap<>();
	}
	
	public void add(int subject, int predicate, int object) {
		// subject = node out
		// object = node in
		// predicate = edge
		Node nodeOut = addNode(subject);
		Node nodeIn = addNode(object);
		Edge edge = addEdge(predicate);		
		nodeOut.addLinkIn(edge,nodeIn);
		nodeIn.addLinkOut(edge,nodeOut);
		
	}
	
	public Node addNode(int nodeId) {
		if(nodes.containsKey(nodeId)) {
			return nodes.get(nodeId);
		} else {
			Node node = new Node(nodeId);
			nodes.put(nodeId,node);
			return node;
		}
	}
	
	public Edge addEdge(int edgeId) {
		if(edges.containsKey(edgeId)) {
			return edges.get(edgeId);
		} else {
			Edge edge = new Edge(edgeId);
			edges.put(edgeId,edge);
			return edge;
		}
	}
}
