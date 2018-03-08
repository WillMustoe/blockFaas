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
public class BlockData {
	
	private final String uuID;
	
	

	public BlockData(String uuID) {
		this.uuID = uuID;
	}

	public String getUUID() {
		// TODO Auto-generated method stub
		return uuID;
	}

	@Override
	public String toString() {
		return "BlockData [uuID=" + uuID + "]";
	}
	
    
}
