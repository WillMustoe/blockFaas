package blockchain;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bidder extends TimerTask {

	private final BlockChain blockChain;
	private final String uniqueID;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private final boolean isConsumer;

	public Bidder(BlockChain blockChain, String uniqueID, boolean isConsumer) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
		this.isConsumer = isConsumer;
	}
	
	public void run() {
		BlockData latestBlockData = blockChain.getLatestBlock().getData();
		if (!(latestBlockData instanceof State)) {
			logger.log(Level.WARNING, "Unrecognised blockdata type : " + latestBlockData.toString());
			return;
		}
		State state = (State) latestBlockData;
		State newState = new State(state);
		if(isConsumer)
		newState.updateConsumerBid(uniqueID, getNextBid());
		else
			newState.updateSupplierBid(uniqueID, getNextBid());
		pushState(newState);

	}

	private void pushState(State state) {
		blockChain.addNewBlock(state);
	}

	private Bid getNextBid() {
		return new Bid(Math.random() * 10 + 1.0f, blockChain.getKeyPair());
	}

}
