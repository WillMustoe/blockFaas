package blockchain;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Bidder implements BlockChainListener {
	
	private final BlockChain blockChain;
	private final String uniqueID;
	private final double maxBid = 5.0f;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean isActive = false;

	public Bidder(BlockChain blockChain, String uniqueID) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
	}
	
	private void Bid(double bidAmount) {
		blockChain.addNewBlock(new Bid(0, bidAmount, uniqueID));
	}

	@Override
	public void onBlockChainChange(int changeMode) {
		BlockData latestBlockData = blockChain.getLatestBlock().getData();
		System.out.println(latestBlockData.toString());
		if(latestBlockData.getUUID() == uniqueID) 
			return;
		if(isActive)
			evaluateNextBid(latestBlockData);
		
	}

	private void evaluateNextBid(BlockData latestBlockData) {
		if(latestBlockData instanceof Bid) {
			double currentBid =((Bid) latestBlockData).getBidAmount();
			if(currentBid >= maxBid) 
				return;
			else {
				System.out.println("bidding: " +  uniqueID);
				double bid = Math.random() *(maxBid - currentBid) + currentBid;
				Bid(bid);
			}
		}
		else {
			logger.log(Level.WARNING, "Unrecognised blockdata type : " + latestBlockData.toString());
		}
		
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		evaluateNextBid(blockChain.getLatestBlock().getData());
		this.isActive = isActive;
	}

}
