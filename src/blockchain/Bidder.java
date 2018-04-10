package blockchain;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.analysis.function.Gaussian;

public class Bidder implements BlockChainListener {
	
	private final BlockChain blockChain;
	private final String uniqueID;
	private double maxBid = Math.random()*10 + 1.0f;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean isActive = false;
	private boolean isNormal = Math.random() > 0.5;

	public Bidder(BlockChain blockChain, String uniqueID) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
	}
	
	private void Bid(double bidAmount, int auctionID) {
		blockChain.addNewBlock(new Bid(auctionID, bidAmount, uniqueID, blockChain.getKeyPair()));
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
		setMaxBid();
		if(latestBlockData instanceof Bid) {
			double currentBid =((Bid) latestBlockData).getBidAmount();
			int auctionID =((Bid) latestBlockData).getAuctionID();
			if(currentBid >= maxBid) 
				return;
			else {
				double bid = Math.random()/10 + currentBid;
				Bid(bid, auctionID);
			}
		}
		else {
			logger.log(Level.WARNING, "Unrecognised blockdata type : " + latestBlockData.toString());
		}
		
	}

	private void setMaxBid() {
		if(!isNormal) return;
		else this.maxBid = getNormalMax();
		
	}

	private double getNormalMax() {
		Gaussian gauss = new Gaussian(30, 30);
		Calendar calendar = Calendar.getInstance();
		float timeInMinutes = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / (float) 60;
		return gauss.value(timeInMinutes)*600 + Math.random();
		
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		evaluateNextBid(blockChain.getLatestBlock().getData());
		this.isActive = isActive;
	}

}
