package blockchain;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Will
 */
public final class Peer {

	private final Socket socket;
	private final PeerData peerData;
	private PublicKey publicKey;
	private String uuid;

	public Peer(String host, int port) throws IOException, InterruptedException {
		this.peerData = new PeerData(host, port);
		socket = attemptConnection(host, port);
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

	public synchronized void sendMessage(Message data) {
		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			byte[] buff = data.toJson().getBytes("UTF-8");
		    dos.writeInt(buff.length);
		    dos.write(buff);
		    dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PeerData getPeerData() {
		return peerData;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
