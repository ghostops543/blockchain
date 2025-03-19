import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 3;
    public static float minimumTransaction = 0.01f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionID = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver, genesisTransaction.value, genesisTransaction.transactionID));
        UTXOs.put(genesisTransaction.outputs.get(0).ID, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOS = new HashMap<String, TransactionOutput>();
        tempUTXOS.put(genesisTransaction.outputs.get(0).ID, genesisTransaction.outputs.get(0));

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

            TransactionOutput tempOutput;
            for(int j = 0; j < currentBlock.transactions.size(); j++) {
                Transaction currentTransaction = currentBlock.transactions.get(j);

                if(!currentTransaction.verifySignature()){
                    System.out.println("signature verification failed on transaction " + j);
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("input values do not match on transaction: " + j);
                    return false;
                }
                for(TransactionInput input: currentTransaction.inputs){
                    tempOutput = tempUTXOS.get(input.transactionOutputID);

                    if(tempOutput == null){
                        System.out.println("input transaction output not found on transaction " + j);
                        return false;
                    }
                    if(input.UTXO.value != tempOutput.value){
                        System.out.println("input tranaction value do not match on transaction " + j);
                        return false;
                    }
                    tempUTXOS.remove(input.transactionOutputID);
                }
                for(TransactionOutput output: tempUTXOS.values()){
                    tempUTXOS.put(output.ID, output);
                }
                if(currentTransaction.outputs.get(0).reciver != currentTransaction.receiver){
                    System.out.println("reciver does not match");
                    return false;
                }
                if(currentTransaction.outputs.get(1).reciver != currentTransaction.sender){
                    System.out.println("sender does not match");
                    return false;
                }
            }
        }
        System.out.println("blockchain verified");
        return true;
    }
    public static void addBlock(Block newBlock) {
        newBlock.mine(difficulty);
        blockChain.add(newBlock);
    }
}