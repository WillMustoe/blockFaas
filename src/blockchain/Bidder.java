package blockchain;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Bidder implements BlockChainListener {
	
	private final BlockChain blockChain;
	private final String uniqueID;
	private final double maxBid = Math.random() *10f;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean isActive = false;

	public Bidder(BlockChain blockChain, String uniqueID) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
	}
	
	private void Bid(double bidAmount, int auctionID) {
		blockChain.addNewBlock(new Bid(auctionID, bidAmount, uniqueID));
	}

	@Override
	public void onBlockChainChange(int changeMode) {
		BlockData latestBlockData = blockChain.getLatestBlock().getData();
		if(latestBlockData.getUUID() == uniqueID) 
			return;
		if(isActive)
			evaluateNextBid(latestBlockData);
		
	}

	private void evaluateNextBid(BlockData latestBlockData) {
		if(latestBlockData instanceof Bid) {
			double currentBid =((Bid) latestBlockData).getBidAmount();
			int auctionID =((Bid) latestBlockData).getAuctionID();
			if(currentBid >= maxBid) 
				return;
			else {
				double bid = Math.random() *(maxBid - currentBid) + currentBid;
				Bid(bid, auctionID);
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
