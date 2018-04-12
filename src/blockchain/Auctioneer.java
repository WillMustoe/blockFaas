package blockchain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Auctioneer {

	public static double calculatePrice(State state) {
		Map<String, Bid> supplierBidMap = state.getSupplierBids();
		Map<String, Bid> consumerBidMap = state.getConsumerBids();
		
		List<Bid> supplierBids = new ArrayList<>(supplierBidMap.values());
		List<Bid> consumerBids = new ArrayList<>(consumerBidMap.values());
		
		supplierBids.sort(Comparator.comparing(Bid::getBidAmount));
		consumerBids.sort(Comparator.comparing(Bid::getBidAmount).reversed());
		int k =0;
		
		if(supplierBids.isEmpty() || consumerBids.isEmpty()) return 0;
		
		for (int i = 0; i < Integer.min(supplierBids.size() , consumerBids.size()); i++) {
			if(consumerBids.get(i).getBidAmount() >= supplierBids.get(i).getBidAmount()) k =i;
		}
		
		double p = consumerBids.get(k).getBidAmount() + supplierBids.get(k).getBidAmount() /2;
		return p;
	}

}
