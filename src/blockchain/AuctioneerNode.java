package blockchain;

import java.util.UUID;

public class AuctioneerNode{
	private final BlockChain blockChain;
	private HttpServerWrapper httpServer;
	private final P2PServer p2pServer;
	private final Thread p2pThread;
	private final String uniqueID;
	private final Thread auctioneerThread;
	private final Auctioneer auctioneer;

	public AuctioneerNode(int port) {
		blockChain = new BlockChain();
		uniqueID = UUID.randomUUID().toString();

		System.out.println("Creating AuctioneerNode bound to : " + port);
		p2pServer = new P2PServer(blockChain, port, uniqueID);
		p2pThread = new Thread(p2pServer);
		p2pThread.start();
		
		httpServer = new HttpServerWrapper(blockChain, p2pServer);
		
		auctioneer = new  Auctioneer(blockChain, uniqueID, 3000);
		auctioneerThread = new Thread(auctioneer);
		auctioneerThread.start();
		
	}
	
	

}
