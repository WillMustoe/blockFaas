package blockchain;

import java.sql.Timestamp;

/**
 *
 * @author Will
 */
public class Block {

    private final int index;
    private final String previousHash;
    private final Timestamp timestamp;
    private final BlockData data;
    private final String hash;

    public Block(int index, String previousHash, Timestamp timestamp, BlockData data){
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.hash = Hash.getHash(this.toHashableString());
    }

    @Override
    public String toString() {
        return "Block{" + "index=" + index + ", previousHash=" + previousHash + ", timestamp=" + timestamp + ", data=" + data + ", hash=" + hash + '}';
    }

    public final String toHashableString() {
        return index + previousHash + timestamp + data;
    }

    public BlockData getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

}
