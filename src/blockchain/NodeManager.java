package blockchain;

import java.util.ArrayList;
import java.util.List;

public class NodeManager {
	
	public static List<Node> nodes;
	public static Node genesisNode;
	
	public static void main(String[] args) {
		System.out.println("Main");
		nodes = new ArrayList<>();
		genesisNode = new Node(4000, true);
		nodes.add(genesisNode);
		
		for (int i = 0; i < 2; i++) {
			Node newNode = new Node(4001 + i, false);
			nodes.add(newNode);
			newNode.addPeer("127.0.0.1", 4000);
		}
		for (int i = 0; i < nodes.size(); i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nodes.get(i).startBidding();
		}
	}

}
