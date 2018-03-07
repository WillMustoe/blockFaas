/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

/**
 *
 * @author Will
 */
public class Bid extends BlockData{

    @Override
    public String toString() {
        return "Bid{" + "auctionID=" + auctionID + ", bidAmount=" + bidAmount + '}';
    }

    private final int auctionID;
    private final double bidAmount;
    
    public Bid(int auctionID, double bidAmount) {
        this.auctionID = auctionID;
        this.bidAmount = bidAmount;
    }

    public int getAuctionID() {
        return auctionID;
    }

    public double getBidAmount() {
        return bidAmount;
    }
}
