package blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
public class BlockChainValidator {

	private static final Logger logger = Logger.getLogger(BlockChainValidator.class.getName());

	public static Boolean isValidNextBlock(blockchain.Block nextBlock, blockchain.Block previousBlock) {
		if (previousBlock.getIndex() != nextBlock.getIndex() - 1) {
			logger.log(Level.FINE, "Invalid Block Index:{0} : {1}",
					new Object[] { previousBlock.getIndex(), nextBlock.getIndex() });
			return false;
		} else if (!Hash.getHashFromBlock(previousBlock).equals(nextBlock.getPreviousHash())) {
			logger.log(Level.FINE, "Invalid Previous Block Hash:{0} : {1}",
					new Object[] { previousBlock.getIndex(), nextBlock.getIndex() });
			return false;
		} else if (!blockchain.Hash.getHashFromBlock(nextBlock).equals(nextBlock.getHash())) {
			logger.log(Level.FINE, nextBlock.getIndex() + " : " + blockchain.Hash.getHashFromBlock(nextBlock) + " : "
					+ nextBlock.getHash());
			return false;
		}

		return true;
	}

	public static Boolean isValidChain(List<Block> chain, Map<String, PublicKey> publicKeys) {
	
	
		if (!Hash.getHashFromBlock(chain.get(0)).equals(Hash.getHashFromBlock(BlockChain.getGenesisBlock()))) {
			System.out.println(chain.get(0) + " : " + BlockChain.getGenesisBlock());
			logger.log(Level.WARNING, "First Block Is Not Genesis");
			return false;
		}
		if (!StateValidator.isValidState(chain.get(chain.size()-1).getData(), publicKeys)) {
			logger.log(Level.FINE, "Invalid State");
			return false;
		}

		for (int i = chain.size() - 1; i > 1 ; i--) {
			if (!isValidNextBlock(chain.get(i), chain.get(i - 1))) {
				logger.log(Level.FINE, "Invalid Block");
				return false;
			}
		}
		
		
		return true;
	}

}
