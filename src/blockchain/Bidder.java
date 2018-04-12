package blockchain;

import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.analysis.function.Gaussian;

public class Bidder implements BlockChainListener {

	private final BlockChain blockChain;
	private final String uniqueID;
	private double maxBid = Math.random() * 10 + 1.0f;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean isActive = false;
	private boolean isNormal = Math.random() > 0.5;
	private boolean isConsumer = Math.random() > 0.5;

	public Bidder(BlockChain blockChain, String uniqueID) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
	}

	private void pushState(State state) {
		blockChain.addNewBlock(state);
	}

	@Override
	public void onBlockChainChange(int changeMode) {
		BlockData latestBlockData = blockChain.getLatestBlock().getData();
		if (isActive)
			evaluateState(latestBlockData);

	}

	private void evaluateState(BlockData latestBlockData) {
		setMaxBid();
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

	private Bid getNextBid() {
		return new Bid(maxBid, blockChain.getKeyPair());
	}

	private void setMaxBid() {
		if (!isNormal)
			return;
		else
			this.maxBid = getNormalMax();

	}

	private double getNormalMax() {
		Gaussian gauss = new Gaussian(30, 30);
		Calendar calendar = Calendar.getInstance();
		float timeInMinutes = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / (float) 60;
		return gauss.value(timeInMinutes) * 600 + Math.random();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		evaluateState(blockChain.getLatestBlock().getData());
		this.isActive = isActive;
	}

}
