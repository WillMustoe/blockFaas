/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.util.UUID;

/**
 *
 * @author Will
 */
public class Node {

	private final BlockChain blockChain;
	private HttpServerWrapper httpServer = null;
	private final P2PServer p2pServer;
	private final Thread p2pThread;
	private final String uniqueID;
	private final Bidder bidder;

	public Node(int port, boolean hasHttpServer) {
		blockChain = new BlockChain();

		System.out.println("Creating Node bound to : " + port);
		p2pServer = new P2PServer(blockChain, port);
		p2pThread = new Thread(p2pServer);
		p2pThread.start();

		if (hasHttpServer)
			httpServer = new HttpServerWrapper(blockChain, p2pServer);
		
		uniqueID = UUID.randomUUID().toString();
		bidder = new Bidder(blockChain, uniqueID);
		blockChain.registerBlockChainListener(bidder);
	}

	public void addPeer(String remoteHost, int remotePort) {
		p2pServer.addPeer(remoteHost, remotePort);
	}
	
	public void startBidding() {
		bidder.setActive(true);
	}
}
