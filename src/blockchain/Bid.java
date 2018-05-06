package blockchain;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Comparator;

/**
 *
 * @author Will
 */
public class Bid{

	private final double bidAmount;
	private final byte[] signature;

	public Bid(double bidAmount, KeyPair keyPair) {
		this.bidAmount = bidAmount;
		this.signature = sign(keyPair);
	}

	private byte[] sign(KeyPair keyPair) {
		try {
			Signature signature = Signature.getInstance("SHA256WithRSA");
			SecureRandom secureRandom = new SecureRandom();
			signature.initSign(keyPair.getPrivate(), secureRandom);
			signature.update(this.toSignatureString().getBytes());
			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}


	public double getBidAmount() {
		return bidAmount;
	}

	public byte[] getSignature() {
		return signature;
	}

	public String toSignatureString() {
		return "Bid [bidAmount:" + bidAmount + "]";
	}

	@Override
	public String toString() {
		return "Bid [bidAmount:" + bidAmount + "]";
	}
}
