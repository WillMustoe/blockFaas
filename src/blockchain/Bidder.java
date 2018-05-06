package blockchain;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.analysis.function.Gaussian;

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
		try {
			BlockData latestBlockData = blockChain.getLatestBlock().getData();
			if (!(latestBlockData instanceof State)) {
				logger.log(Level.WARNING, "Unrecognised blockdata type : " + latestBlockData.toString());
				return;
			}
			State state = (State) latestBlockData;
			State newState = new State(state);
			if (isConsumer)
				newState.updateConsumerBid(uniqueID, getNextBid(state));
			else
				newState.updateSupplierBid(uniqueID, getNextBid(state));
			pushState(newState);

		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "Uncaught Runtime Exception", e);
			return; // Keep working
		} catch (Throwable e) {
			logger.log(Level.WARNING, "Unrecoverable error", e);
			throw e;
		}

	}

	private void pushState(State state) {
		blockChain.addNewBlock(state);
	}

	private Bid getNextBid(State lastState) {
		double nextBid;

		if (isConsumer) {
			double maxBid = getMaxBid(lastState.getConsumerBids());
			if (maxBid < getNormalMax()) {
				nextBid = maxBid + 0.1f;
			} else {
				nextBid = getNormalMax();
			}

		} else {
			double minBid = getMinBid(lastState.getSupplierBids());
			if (minBid > getNormalMax()) {
				nextBid = minBid - 0.1f;
			} else {
				nextBid = getNormalMax();
			}
		}

		return new Bid(nextBid, blockChain.getKeyPair());
	}

	private double getNormalMax() {
		Gaussian gauss = new Gaussian(30, 30);
		Calendar calendar = Calendar.getInstance();
		float timeInMinutes = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) / (float) 60;
		return gauss.value(timeInMinutes) * 600 + Math.random() / 2;

	}

	private double getMaxBid(Map<String, Bid> bids) {
		try {
			return bids.values().stream().max(new Comparator<Bid>() {
				@Override
				public int compare(Bid b1, Bid b2) {
					if (b1.getBidAmount() > b2.getBidAmount())
						return 1;
					else if (b1.getBidAmount() < b2.getBidAmount())
						return -1;
					return 0;
				}
			}).get().getBidAmount();
		} catch (NoSuchElementException e) {
			return 0;
		}

	}

	private double getMinBid(Map<String, Bid> bids) {
		try {
			return bids.values().stream().min(new Comparator<Bid>() {
				@Override
				public int compare(Bid b1, Bid b2) {
					if (b1.getBidAmount() > b2.getBidAmount())
						return 1;
					else if (b1.getBidAmount() < b2.getBidAmount())
						return -1;
					return 0;
				}
			}).get().getBidAmount();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}

}
