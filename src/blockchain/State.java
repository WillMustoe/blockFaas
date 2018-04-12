package blockchain;

import java.util.HashMap;
import java.util.Map;

public class State extends BlockData {
	private final Map<String, Bid> supplierBids;
	private final Map<String, Bid> consumerBids;

	public State() {
		this.supplierBids = new HashMap<>();
		this.consumerBids = new HashMap<>();
	}
	public State(State oldState) {
		this.supplierBids = new HashMap<>(oldState.getSupplierBids());
		this.consumerBids = new HashMap<>(oldState.getConsumerBids());
	}
	
	public void updateSupplierBid(String uuid, Bid bid) {
		supplierBids.put(uuid, bid);
	}
	
	public void updateConsumerBid(String uuid, Bid bid){
		consumerBids.put(uuid, bid);
		
	}

	public Map<String, Bid> getSupplierBids() {
		return supplierBids;
	}

	public Map<String, Bid> getConsumerBids() {
		return consumerBids;
	}

	@Override
	public String toString() {
		return "State [supplierBids=" + supplierBids + ", consumerBids=" + consumerBids + "]";
	}
	
	
	
}
