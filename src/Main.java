import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();

    public static void main(String[] args) {
        blockChain.add(new Block("the first block", "0"));
        blockChain.add(new Block("the second block",blockChain.get(blockChain.size()-1).hash));
        blockChain.add(new Block("the second block",blockChain.get(blockChain.size()-1).hash));

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);
    }
}