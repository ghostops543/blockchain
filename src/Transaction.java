import jdk.jshell.execution.Util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionID;
    public PublicKey sender;
    public PublicKey receiver;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;

    }
    public String calculateHash(){
        sequence++;
        return Utility.applyEncrption(
                Utility.getStringFromKey(sender) +
                        Utility.getStringFromKey(receiver) +
                        Float.toString(value) + sequence
                        );
    }
    public void generateSignature(PrivateKey privateKey){
        String data = Utility.getStringFromKey(sender)+ Utility.getStringFromKey(receiver)+ Float.toString(value);
        signature = Utility.applyECDSASig(privateKey, data);
    }
    public boolean verifySignature(){
        String data = Utility.getStringFromKey(sender)+ Utility.getStringFromKey(receiver)+ Float.toString(value);
        return Utility.verifyECDSASig(sender, data, signature);
    }
    public boolean processTransaction(){
        if(!verifySignature()){
            System.out.println("Transaction Signature verification failed");
            return false;
        }
        for(TransactionInput input : inputs){
            input.UTXO = Main.UTXOs.get(input.transactionOutputID);
        }
        if(getInputsValue() < Main.minimumTransaction){
            System.out.println("Transaction value less than minimum transaction");
            return false;
        }
        float leftOver = getInputsValue() - value;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.receiver, value, transactionID));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

        for(TransactionOutput output : outputs){
            Main.UTXOs.put(output.ID, output);
        }

        for(TransactionInput input : inputs){
            if(input.UTXO == null) continue;
            Main.UTXOs.remove(input.UTXO.ID);
        }
        return true;
    }
    public float getInputsValue(){
        float total = 0;
        for(TransactionInput input : inputs){
            if(input.UTXO == null) continue;
            total += input.UTXO.value;
        }
        return total;
    }
    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput output : outputs){
            total += output.value;
        }
        return total;
    }
}
class TransactionInput {
    public String transactionOutputID;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputID) {
        this.transactionOutputID = transactionOutputID;
    }
}
class TransactionOutput {
    public String ID;
    public PublicKey reciver;
    public float value;
    public String parentTransactionID;

    public TransactionOutput(PublicKey reciver, float value, String parentTransactionID) {
        this.reciver = reciver;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.ID = Utility.applyEncrption(Utility.getStringFromKey(reciver)+ Float.toString(value) + parentTransactionID);
    }
    public boolean isMine(PublicKey publicKey){
        return(publicKey == reciver);
    }
}
