/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
public class BlockChain {

    public static final int DO_NOTHING = 0;
	public static final int BROADCAST_LATEST = 1;
	public static final int QUERY_ALL = 2;
	private static List<Block> blockChain;
    private static Logger logger;

    public BlockChain() {
        blockChain = new ArrayList<>();
        blockChain.add(getGenesisBlock());
        printBlockChain();
        logger = Logger.getLogger(this.getClass().getName());
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public static Block getGenesisBlock() {
        return new Block(0, "", new Timestamp(System.currentTimeMillis()), new Bid(0, 0));
    }
    
    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() -1);
    }
    
    public void printBlockChain(){
        blockChain.forEach(b -> {
            System.out.println(b.toString());
        });
    }
    
    public Block generateNewBlock(BlockData data){
        Block previousBlock = getLatestBlock();
        int index = previousBlock.getIndex();
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        return new Block(index, previousBlock.getHash(), timeStamp, data);
    }

	public int handleChain(List<Block> newBlockChain) {
		Block newLatest = newBlockChain.get(newBlockChain.size() - 1);
		Block oldLatest = getLatestBlock();
		if(newLatest.getIndex() > oldLatest.getIndex()) {
			//Blockchain behind
			if(oldLatest.getHash().equals(newLatest.getPreviousHash())) {
				//Append block to chain
				blockChain.add(newLatest);
				return BROADCAST_LATEST;
			}
			else if(newBlockChain.size() == 1) {
				//Need to query whole chain
				return QUERY_ALL;
			}
			else {
				//Received chain is longer than current
				return replaceChain(newBlockChain);
			}
		}
		else {
			return DO_NOTHING;
		}

	}

	private int replaceChain(List<Block> newBlockChain) {
		if(BlockChainValidator.isValidChain(newBlockChain)) {
			//Replace current chain
			blockChain = newBlockChain;
			return BROADCAST_LATEST;
		}
		else {
			return DO_NOTHING;
		}
	}

}
