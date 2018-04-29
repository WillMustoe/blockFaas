package blockchain;

public class Output implements BlockChainListener{
	
	private final BlockChain blockchain;
	private final MarketGraph marketGraph; 

	public Output(BlockChain blockchain) {
		this. blockchain = blockchain;
		this.marketGraph = new MarketGraph("Market Value", "Price Vs Time of Day");
		marketGraph.display();
	}

	@Override
	public void onBlockChainChange(int changeMode) {
		handleLatestState(blockchain.getLatestBlock().getData());
	}

	private void handleLatestState(BlockData data) {
		if(!(data instanceof State)) return;
		 State state = (State) data;
		 double currentPrice = Auctioneer.calculatePrice(state);
		 marketGraph.addValue(currentPrice);
	}

}
