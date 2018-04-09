package blockchain;

import java.sql.Timestamp;

/**
 *
 * @author Will
 */
public class Block {

    private final int index;
    private final String previousHash;
    private final long timestamp;
    private final BlockData data;
    private final String hash;

    public Block(int index, String previousHash, Timestamp timestamp, BlockData data){
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp.getTime();
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

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

}
