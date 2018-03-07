/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

/**
 *
 * @author Will
 */
public class blockFaas {

    private static BlockChain blockChain;
    private static HttpServerWrapper httpServer;
    private static P2PServer p2pServer;

    public static void main(String[] args) {
        blockChain = new BlockChain();
        httpServer = new HttpServerWrapper(blockChain);
        p2pServer = new P2PServer(blockChain, httpServer, 4000);
        Thread p2pThread = new Thread(p2pServer);
        p2pThread.start();
        P2PServer p2pServer1 = new P2PServer(blockChain, httpServer, 4001);
        Thread p2pThread1 = new Thread(p2pServer1);
        p2pThread1.start();
        p2pServer.addPeer("127.0.0.1", 4001);
        p2pServer1.addPeer("127.0.0.1", 4000);
    }
}
