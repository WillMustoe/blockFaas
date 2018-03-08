/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
public class BlockChain {

    public static final int DO_NOTHING = 0;
	public static final int BROADCAST_LATEST = 1;
	public static final int QUERY_ALL = 2;
	
	public static final int EXTERNAL_CHANGE = 0;
	public static final int LOCAL_CHANGE = 1;
	
	private List<Block> blockChain;
	private List<BlockChainListener> listeners = new ArrayList<>();
	
    public BlockChain() {
        blockChain = new ArrayList<>();
        blockChain.add(getGenesisBlock());
        Logger.getLogger(this.getClass().getName());
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public static Block getGenesisBlock() {
        return new Block(0, "", new Timestamp(0), new Bid(0, 0, null));
    }
    
    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() -1);
    }
    
    public void printBlockChain(){
        blockChain.forEach(b -> {
            System.out.println(b.toString());
        });
    }
    
    public void addNewBlock(Bid data){
    	System.out.println(data);
        Block previousBlock = getLatestBlock();
        int index = previousBlock.getIndex()+1;
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        blockChain.add(new Block(index, previousBlock.getHash(), timeStamp, data));
        notifyListeners(LOCAL_CHANGE);
    }

	public int handleChain(List<Block> newBlockChain) {
		Block newLatest = newBlockChain.get(newBlockChain.size() - 1);
		Block oldLatest = getLatestBlock();
		
		if(newLatest.getIndex() > oldLatest.getIndex()) {
			//Blockchain behind
			if(oldLatest.getHash().equals(newLatest.getPreviousHash())) {
				//Append block to chain
				blockChain.add(newLatest);
				System.out.println("New block appended");
				notifyListeners(EXTERNAL_CHANGE);
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
			notifyListeners(EXTERNAL_CHANGE);
			return BROADCAST_LATEST;
		}
		else {
			return DO_NOTHING;
		}
	}
	
	public void registerBlockChainListener(BlockChainListener listener) {
		listeners.add(listener);
	}
	
	public void unRegisterBlockChainListener(BlockChainListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(int changeMode) {
		this.listeners.forEach(listener -> listener.onBlockChainChange(changeMode));
	}

}
