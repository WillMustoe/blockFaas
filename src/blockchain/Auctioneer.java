package blockchain;

public class Auctioneer implements Runnable {
	private final BlockChain blockChain;
	private final String uniqueID;
	private int currentAuction;
	private final long timeOut;
	private final MarketGraph marketGraph;

	public Auctioneer(BlockChain blockChain, String uniqueID, long timeOut) {
		this.blockChain = blockChain;
		this.uniqueID = uniqueID;
		this.timeOut = timeOut;
		this.marketGraph =  new MarketGraph( "BlockFaas" , "Price vs Time of Day" );
		marketGraph.display();
	}

	public void watchChain() {
		while (true) {
			Block latestBlock = blockChain.getLatestBlock();
			BlockData latestBlockData = latestBlock.getData();
			if (System.currentTimeMillis() - latestBlock.getTimestamp() > timeOut
					&& latestBlockData instanceof Bid
					&& !latestBlock.getHash().equals(BlockChain.getGenesisBlock().getHash())) {
				Bid winningBid = (Bid) latestBlockData;
				if(winningBid.getBidAmount() > 1.0f) {
					System.out.println("Auction: " + winningBid.getAuctionID() + " is over");
					System.out.println("Wining Bid: " + winningBid.getBidAmount() + " From: " + winningBid.getUUID());
					marketGraph.addValue(winningBid.getBidAmount());
					currentAuction = winningBid.getAuctionID() + 1;
					blockChain.addNewBlock(new Bid(currentAuction, 0.0f, uniqueID));
				}
				else {
					blockChain.addNewBlock(new Bid(currentAuction, winningBid.getBidAmount(), uniqueID));
				}

				
			}
			try {
				Thread.sleep(100);
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
