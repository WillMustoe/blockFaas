package blockchain;

public class Auctioneer implements Runnable {
	private final BlockChain blockChain;
	private final String uniqueID;
	private int currentAuction;
	private final long timeOut;

	public Auctioneer(BlockChain blockChain, String uniqueID, long timeOut) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
		this.timeOut = timeOut;
	}

	public void watchChain() {
		while (true) {
			Block latestBlock = blockChain.getLatestBlock();
			BlockData latestBlockData = latestBlock.getData();

			if (System.currentTimeMillis() - latestBlock.getTimestamp().getTime() > timeOut
					&& latestBlockData instanceof Bid
					&& !latestBlock.getHash().equals(BlockChain.getGenesisBlock().getHash())) {
				Bid winningBid = (Bid) latestBlockData;
				System.out.println("Auction: " + winningBid.getAuctionID() + "is over");
				System.out.println("Wining Bid: " + winningBid.getBidAmount() + "From: " + winningBid.getUUID());
				currentAuction = winningBid.getAuctionID() + 1;
				blockChain.addNewBlock(new Bid(currentAuction, 0, uniqueID));
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		watchChain();
		
	}

}
