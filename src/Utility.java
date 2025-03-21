import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Utility {
    public static String applyEncrption(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try{
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }
    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count = 0;
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions){
            previousTreeLayer.add(transaction.transactionID);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1){
            treeLayer = new ArrayList<String>();
            for(int i = 0; i <previousTreeLayer.size(); i++){
                treeLayer.add(applyEncrption(previousTreeLayer.get(i-1))+ previousTreeLayer.get(i));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
