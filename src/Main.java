import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static int difficulty = 3;

    public static void main(String[] args) {
        blockChain.add(new Block("the first block", "0"));
        System.out.println("mining block 1...");
        blockChain.get(0).mine(difficulty);

        blockChain.add(new Block("the second block", blockChain.get(blockChain.size() - 1).hash));
        System.out.println("mining block 2...");
        blockChain.get(1).mine(difficulty);

        blockChain.add(new Block("the second block", blockChain.get(blockChain.size() - 1).hash));
        System.out.println("mining block 3...");
        blockChain.get(2).mine(difficulty);

        System.out.println("\n block chain is valid: " +isChainValid());

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println("\n block chain json: ");
        System.out.println(blockChainJson);
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.getHash())) {
                System.out.println("current block hash does not match previous block hash");
                return false;
            }
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("previous block hash does not match current block hash");
                return false;
            }
            if(!currentBlock.hash.substring(0 ,difficulty).equals(hashTarget)){
                System.out.println("current block hasnt been mined");
                return false;
            }
        }
        return true;
    }
}