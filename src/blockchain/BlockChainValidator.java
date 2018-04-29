package blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
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

	public static Boolean isValidNewBlock(blockchain.Block nextBlock, blockchain.Block previousBlock) {
		if (previousBlock.getIndex() != nextBlock.getIndex() - 1) {
			logger.log(Level.WARNING, "Invalid Block Index:{0} : {1}",
					new Object[] { previousBlock.getIndex(), nextBlock.getIndex() });
			return false;
		} else if (!previousBlock.getHash().equals(nextBlock.getPreviousHash())) {
			logger.log(Level.WARNING, "Invalid Previous Block Hash:{0} : {1}",
					new Object[] { previousBlock.getIndex(), nextBlock.getIndex() });
			return false;
		} else if (!blockchain.Hash.getHashFromBlock(nextBlock).equals(nextBlock.getHash())) {
			logger.log(Level.WARNING, nextBlock.getIndex() + " : " + blockchain.Hash.getHashFromBlock(nextBlock) + " : "
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

		for (int i = 2; i < chain.size(); i++) {
			if (!isValidNewBlock(chain.get(i), chain.get(i - 1))) {
				logger.log(Level.WARNING, "Invalid Block");
				return false;
			}
			else if (!StateValidator.isValidState(chain.get(i).getData(), publicKeys)) {
				logger.log(Level.WARNING, "Invalid State");
				return false;
			}
		}
		return true;
	}

}
