package blockchain;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
public final class Peer {

	private final Socket socket;
	private final PeerData peerData;

	public Peer(String host, int port) throws IOException, InterruptedException {
		this.peerData = new PeerData(host, port);
		socket = attemptConnection(host, port);
	}

	public PeerData getPeerData() {
		return peerData;
	}

	public Socket attemptConnection(String host, int port) throws ConnectException {
		Socket newSocket;
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(500);
				newSocket = new Socket(host, port);
				return newSocket;
			} catch (IOException ex) {
				Logger.getLogger(Peer.class.getName()).log(Level.FINE, null, ex);
			} catch (InterruptedException ex) {
				Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		throw new java.net.ConnectException("Peer not found: " + host + ":" + port);
	}

	public void sendMessage(Message data) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(socket.getOutputStream());
			pw.print(data.toJson() + "\n");
			pw.flush();
		} catch (IOException ex) {
			Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
