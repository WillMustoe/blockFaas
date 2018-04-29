/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.util.Timer;
import java.util.UUID;

/**
 *
 * @author Will
 */
public class Node {

	private final BlockChain blockchain;
	private HttpServerWrapper httpServer = null;
	private final P2PServer p2pServer;
	private final Thread p2pThread;
	private final String uniqueID;
	private final Bidder bidder;
	private  Output output;

	public Node(int port, boolean hasHttpServer) {
		blockchain = new BlockChain();
		uniqueID = UUID.randomUUID().toString();

		System.out.println("Creating Node bound to : " + port);
		p2pServer = new P2PServer(blockchain, port, uniqueID);
		p2pThread = new Thread(p2pServer);
		p2pThread.start();

		if (hasHttpServer) {
			output = new Output(blockchain);
			blockchain.registerBlockChainListener(output);
			httpServer = new HttpServerWrapper(blockchain, p2pServer);
		}
			
		bidder = new Bidder(blockchain, uniqueID, port%2 == 0 );
		Timer timer = new Timer();
		timer.schedule(bidder, 0, 5000);
	}

	public void addPeer(String remoteHost, int remotePort) {
		p2pServer.addPeer(remoteHost, remotePort);
	}
}
