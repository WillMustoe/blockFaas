package blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
class P2PServer implements Runnable, BlockChainListener {

    private final BlockChain blockChain;
    private static final Logger logger = Logger.getLogger(P2PServer.class.getName());
    private final List<Peer> peers;
    private PeerData myPeerData;
    private final int socketServerPort;
    private final Gson gson;
    private KeyPair keyPair;
    private final String uniqueID;

    P2PServer(BlockChain blockChain, int socketServerPort, String uniqueID) {
    	
    	final RuntimeTypeAdapterFactory<BlockData> typeFactory = RuntimeTypeAdapterFactory  
    	        .of(BlockData.class, "type")
    	        .registerSubtype(State.class, "state");
    	
    	gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
        this.blockChain = blockChain;
        blockChain.registerBlockChainListener(this);
        peers = new ArrayList<>();
        this.socketServerPort = socketServerPort;
        this.uniqueID = uniqueID;
        generateKeyPair();
        
    }

	private void generateKeyPair() {
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			logger.log(Level.SEVERE, "No Such Algorithm Execption in Key Generation");
			System.exit(1);
		}
		
		blockChain.setKeyPair(keyPair);
	}

    public Peer addPeer(String host, int port) {
        try {
            Peer newPeer = new Peer(host, port);
            peers.add(newPeer);
            newPeer.sendMessage(new Message(Message.PEERS_QUERY_ALL, "", socketServerPort));
            newPeer.sendMessage(new Message(Message.CHAIN_QUERY_LATEST, "", socketServerPort));
            newPeer.sendMessage(new Message(Message.QUERY_PUBLICKEY, "", socketServerPort));
            newPeer.sendMessage(new Message(Message.QUERY_UUID, "", socketServerPort));
            return newPeer;
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Peer not found: " + host + ":" + port, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(P2PServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(socketServerPort);
            this.myPeerData = new PeerData(ss.getInetAddress().getHostAddress(), socketServerPort);
            while (ss.isBound()) {
                socketStream(ss.accept());
            }
            ss.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void socketStream(final Socket s) {
        final InputStream is;
        try {
            is = s.getInputStream();
        } catch (IOException ex) {
            return;
        }
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (s.isConnected()) {
                    try {
                    	InputStream is = s.getInputStream();
                    	DataInputStream dis = new DataInputStream(is);
                    	int len = dis.readInt();
                    	byte[] buff = new byte[len];
                    	dis.readFully(buff);
                    	String response = new String(buff, "UTF-8");
                    	handleMessage(response, s);
                    } catch (IOException ex) {
                        return;
                    }
                }
            }
        }).start();
    }
    
    private synchronized void  broadcast(Message data){
        peers.forEach(p -> p.sendMessage(data));
    }
    
    private void handleMessage(String messageJson, Socket s){
        Message message;
        try {
            message = Message.fromJson(messageJson);
        } catch (JsonSyntaxException e) {
            logger.log(Level.INFO, "JSON recived was not parseable into message: {0}", messageJson);
            return;
        }
        String returnAddress = s.getInetAddress().getHostAddress();
        int returnPort = message.getReturnPort();
        
        switch(message.getType()){
            case Message.PEERS_QUERY_ALL :
                peersQueryAllHandler(returnAddress, returnPort);
                 break;
            case Message.CHAIN_QUERY_ALL :
            	chainQueryAllHandler(returnAddress, returnPort);
            case Message.CHAIN_QUERY_LATEST:
            	chainQueryLatestHandler(returnAddress, returnPort);
            case Message.RESPONSE_PEERS :
                parsePeers(message.getMessageData());
                break;
            case Message.RESPONSE_CHAIN_LATEST :
            	parseChainLatest(message.getMessageData());
            	break;
            case Message.RESPONSE_CHAIN_ALL :
            	parseChainAll(message.getMessageData());
            	break;
            case Message.QUERY_PUBLICKEY :
            	sendPublicKey(returnAddress, returnPort);
            	break;
            case Message.RESPONSE_PUBLICKEY :
            	updatePublicKey(returnAddress, returnPort, message.getMessageData());
            	break;
            case Message.QUERY_UUID :
            	sendUUID(returnAddress, returnPort);
            	break;
            case Message.RESPONSE_UUID :
            	updateUUID(returnAddress, returnPort, message.getMessageData());
            	break;
                
            default:
                logger.log(Level.WARNING, "Unsupported message type recieved :{0}", message.toString());
                break;
                
        }  
    }


	private void updateUUID(String remoteHost, int port, String messageData) {
		Peer peer = findPeer(remoteHost, port);
		peer.setUuid(messageData);
		updatePublicKeys();
	}

	private void updatePublicKeys() {
		Map<String, PublicKey> publicKeys = new HashMap<>();
		peers.forEach(peer -> {
			publicKeys.put(peer.getUuid(), peer.getPublicKey());
		});
		blockChain.setPublicKeys(publicKeys);
	}

	private void sendUUID(String remoteHost, int port) {
		Peer peer = findPeer(remoteHost, port);
        peer.sendMessage(new Message(Message.RESPONSE_UUID, uniqueID, socketServerPort));
		
	}

	private void updatePublicKey(String remoteHost, int port, String messageData) {
		Peer peer = findPeer(remoteHost, port);
		PublicKey publicKey;
		try {
			publicKey = deserialisePublicKey(messageData);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			logger.log(Level.SEVERE, "Error in public key deserialisation");
			return;
		}
		peer.setPublicKey(publicKey);
		updatePublicKeys();
	}


	private void sendPublicKey(String remoteHost, int port) {
		Peer peer = findPeer(remoteHost, port);
        String publicKeySerialised = serialisePublicKey(keyPair.getPublic());
        peer.sendMessage(new Message(Message.RESPONSE_PUBLICKEY, publicKeySerialised, socketServerPort));
	}

	private String serialisePublicKey(PublicKey publicKey) {
		RSAPublicKey publicKeyRSA = (RSAPublicKey)publicKey;
		return publicKeyRSA.getModulus().toString() + "|" +
		publicKeyRSA.getPublicExponent().toString();
	}
	
	private PublicKey deserialisePublicKey(String messageData) throws InvalidKeySpecException, NoSuchAlgorithmException {
		String []Parts = messageData.split("\\|");
		RSAPublicKeySpec Spec = new RSAPublicKeySpec(
		        new BigInteger(Parts[0]),
		        new BigInteger(Parts[1]));
		return KeyFactory.getInstance("RSA").generatePublic(Spec);
	}

	private void parseChainAll(String messageData) {
    	List<Block> newBlockChain;
        try {
           Type type = new TypeToken<List<Block>>() {}.getType();
           newBlockChain = gson.fromJson(messageData, type); 
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Recived malformed blockchain {0}", messageData);
            return;
        }
        handleBlockChainResponse(newBlockChain);
	}

	private void handleBlockChainResponse(List<Block> newBlockChain) {
		int resp = blockChain.handleChain(newBlockChain);
		
		switch (resp) {
		case BlockChain.BROADCAST_LATEST : 
			 broadcastLatest();
			break;
		case BlockChain.QUERY_ALL :
			broadcast(new Message(Message.CHAIN_QUERY_ALL, "", socketServerPort));
		case BlockChain.DO_NOTHING :
			break;
		default:
			logger.log(Level.WARNING, "Unrecognised response from blockchain");
			break;
		}
		
	}

	private synchronized void broadcastLatest() {
		broadcast(new Message(Message.RESPONSE_CHAIN_LATEST, getLatestBlockJson(), socketServerPort));
	}

	private void parseChainLatest(String messageData) {
		Block newBlock;
        try {
           Type type = new TypeToken<Block>() {}.getType();
           newBlock = gson.fromJson(messageData, type); 
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Recived malformed block {0}", messageData);
            return;
        }
        List<Block> blockListWrapper = new ArrayList<>();
        blockListWrapper.add(newBlock);
		 handleBlockChainResponse(blockListWrapper);
	}

	private void chainQueryLatestHandler(String returnAddress, int returnPort) {
    	Peer remotePeer = getPeerElseAdd(returnAddress, returnPort);
    	if(remotePeer == null){
            logger.log(Level.WARNING, "Recieved chainQueryLATEST from {0} : {1} could not find host", new Object[]{returnAddress, returnPort});
        }
    	sendLatest(remotePeer);
	}

	private void sendLatest(Peer remotePeer) {
		String blockJson = getLatestBlockJson();
		remotePeer.sendMessage(new Message(Message.RESPONSE_CHAIN_LATEST, blockJson, socketServerPort));
		
	}

	private String getLatestBlockJson() {
		Type type = new TypeToken<Block>() {}.getType();
		String blockJson = gson.toJson(blockChain.getLatestBlock(), type);
		return blockJson;
	}

	private void chainQueryAllHandler(String returnAddress, int returnPort) {
    	Peer remotePeer = getPeerElseAdd(returnAddress, returnPort);
    	if(remotePeer == null){
            logger.log(Level.WARNING, "Recieved chainQueryALL from {0} : {1} could not find host", new Object[]{returnAddress, returnPort});
        }
    	else {
    		sendChain(remotePeer);
    	}
	}

	private void sendChain(Peer remotePeer) {
		Type type = new TypeToken<List<Block>>() {}.getType();
		List<Block> blockChainState = new ArrayList<>(blockChain.getBlockChain());
        String blocksJson = gson.toJson(blockChainState, type);
        remotePeer.sendMessage(new Message(Message.RESPONSE_CHAIN_ALL, blocksJson, socketServerPort));
	}

	private void peersQueryAllHandler(String remoteHost, int port) {
        Peer remotePeer = getPeerElseAdd(remoteHost, port);
        if(remotePeer == null){
            logger.log(Level.WARNING, "Recieved peerQueryALL from {0} : {1} could not find host", new Object[]{remoteHost, port});
        }
        else{
            sendPeers(remotePeer);
        }
    }

	private Peer getPeerElseAdd(String remoteHost, int port) {
		Peer remotePeer = findPeer(remoteHost, port);
        if(remotePeer == null) remotePeer = addPeer(remoteHost, port);
		return remotePeer;
	}

    private Peer findPeer(String remoteHost, int port) {
        PeerData remotePeerData = new PeerData(remoteHost, port);
        for (Peer peer : peers) {
            if(peer.getPeerData().equals(remotePeerData)){
                return peer;
            }
        }
        return null;
    }
    
    private Peer findPeer(PeerData peerData) {
        return findPeer(peerData.getHost(), peerData.getPort());
    }

    private void sendPeers(Peer peer) {
        Type type = new TypeToken<List<PeerData>>() {}.getType();
        String peersJson = gson.toJson(getPeersData(), type);
        peer.sendMessage(new Message(Message.RESPONSE_PEERS, peersJson, socketServerPort));
    }
    
    public  synchronized List<PeerData> getPeersData(){
        List<PeerData> peerDataList = new ArrayList<>();
        peers.forEach((peer) -> {
            peerDataList.add(peer.getPeerData());
        });
        return peerDataList;
    }

    private void parsePeers(String messageData) {
        List<PeerData> peerDataList;
        try {
           Type type = new TypeToken<List<PeerData>>() {}.getType();
            peerDataList = gson.fromJson(messageData, type);
            if(peerDataList == null) {
            	logger.log(Level.FINE, "Recived null peer list");
            	return;
            }
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Recived malformed peer list {0}", messageData);
            return;
        }
        peerDataList.stream().filter(peer -> isValidNewPeer(peer)).forEach((peerData) -> {
            addPeer(peerData.getHost(), peerData.getPort());
        });
        
    }
    
    private boolean isValidNewPeer(PeerData peerData){
        if(peerData.equals(myPeerData)) return false;
        if(findPeer(peerData) != null) return false;
        return true;
    }

	@Override
	public void onBlockChainChange(int changeMode) {
		if(changeMode == BlockChain.LOCAL_CHANGE) {
			broadcastLatest();
		}
		
	}
}
