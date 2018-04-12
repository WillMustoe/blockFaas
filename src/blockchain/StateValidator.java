package blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateValidator {
	private static final Logger logger = Logger.getLogger(StateValidator.class.getName());

	public static boolean isValidState(BlockData data, Map<String, PublicKey> publicKeys) {
		if (!(data instanceof State)) {
			logger.log(Level.WARNING, "Unrecognised block data type");
			return false;
		}
		State state = (State) data;

		Map<String, Bid> consumerBids = state.getConsumerBids();
		Map<String, Bid> supplierBids = state.getSupplierBids();
		if (!validBids(consumerBids, publicKeys))
			return false;
		if (!validBids(supplierBids, publicKeys))
			return false;
		return true;
	}

	private static boolean validBids(Map<String, Bid> bids, Map<String, PublicKey> publicKeys) {
		boolean retVal = true;
		for (Map.Entry<String, Bid> entry : bids.entrySet()) {
			String uuid = entry.getKey();
			Bid bid = entry.getValue();
			retVal &= isValidBid(publicKeys, uuid, bid);
		}
		return retVal;
	}

	private static boolean isValidBid(Map<String, PublicKey> publicKeys, String uuid, Bid bid) {
		PublicKey publicKey = publicKeys.get(uuid);
		if (publicKey == null) {
			logger.log(Level.WARNING, "Public Key not found");
			return false;
		}
		if (bid.getSignature() == null) {
			logger.log(Level.WARNING, "Bid did not contain signature");
			return false;
		}
		return isValidSignature(publicKey, bid.toString(), bid.getSignature());
	}

	private static boolean isValidSignature(PublicKey publicKey, String data, byte[] sig) {
		Signature signature;
		try {
			signature = Signature.getInstance("SHA256WithRSA");
			signature.initVerify(publicKey);
			signature.update(data.getBytes());
			return signature.verify(sig);
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			logger.log(Level.SEVERE, "Error in State Signature Checking");
			e.printStackTrace();
		}
		return false;
	}

}
