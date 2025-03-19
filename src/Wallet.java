import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public Wallet(){
        generateKeyPair();
    }
    public void generateKeyPair(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyPairGenerator.initialize(ecSpec, random);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance(){
        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO =item.getValue();
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.ID,UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }
    public Transaction sendFunds(PublicKey reciver, float value){
        if(getBalance() < value){
            System.out.println("Insufficient funds");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for(Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO =item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.ID));
            if(total >= value)break;
        }
        Transaction newTransaction = new Transaction(publicKey, reciver, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputID);
        }
        return newTransaction;
    }
}

