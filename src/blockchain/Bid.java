/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 *
 * @author Will
 */
public class Bid extends BlockData {

	private final int auctionID;
	private final double bidAmount;
	private final byte[] signature;

	public Bid(int auctionID, double bidAmount, String uuID, KeyPair keyPair) {
		super(uuID);
		this.auctionID = auctionID;
		this.bidAmount = bidAmount;
		this.signature = sign(keyPair);
	}
	
	public Bid(int auctionID, double bidAmount, String uuID) {
		super(uuID);
		this.auctionID = auctionID;
		this.bidAmount = bidAmount;
		this.signature = null;
	}

	private byte[] sign(KeyPair keyPair) {
		try {
			Signature signature = Signature.getInstance("SHA256WithRSA");
			SecureRandom secureRandom = new SecureRandom();
			signature.initSign(keyPair.getPrivate(), secureRandom);
			signature.update(this.toString().getBytes());
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

	public int getAuctionID() {
		return auctionID;
	}

	public double getBidAmount() {
		return bidAmount;
	}

	public byte[] getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		return "Bid [auctionID=" + auctionID + ", bidAmount=" + bidAmount + "]";
	}
}
