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

    public Transaction(PublicKey from, PublicKey to, float value, float ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;

    }
    private String calculateHash(){
        sequence++;
        return Utility.applyEncrption(
                Utility.getStringFromKey(sender) +
                        Utility.getStringFromKey(receiver) +
                        Float.toString(value) + sequence
                        );
    }
    private boolean generateSignature(PrivateKey privateKey){
        String data = Utility.getStringFromKey(sender)+ Utility.getStringFromKey(receiver)+ Float.toString(value);
        return Utility.verifyECDSASig(sender, data, signature);
    }
}
