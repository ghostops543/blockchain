import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions= new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;

    //Block Constructor.
    public Block(String previousHash ) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = getHash();
    }
    public String getHash() {
        return Utility.applyEncrption(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }
    public void mine(int difficulty) {
        merkleRoot = Utility.getMerkleRoot(transactions);
        String target = Utility.getDificultyString(difficulty);
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = getHash();
        }
        System.out.println("Mined: " + hash);
    }
    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return false;
        if(previousHash != "0"){
            if(!transaction.processTransaction()){
                System.out.println("Transaction process failed");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction added");
        return true;
    }
}
