package Transaction;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transactions {
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[]signature;

    public ArrayList<TransactionsInput> inputs=new ArrayList<TransactionsInput>();
    public ArrayList<TransactionsOutput>outputs=new ArrayList<TransactionsOutput>();

    private static int sequence=0;

    public Transactions(PublicKey from,PublicKey to,float value,ArrayList<TransactionsInput>inputs){
        this.sender=from;
        this.reciepient=to;
        this.value=value;
        this.inputs=inputs;
    }

    public String calulateHash(){
        sequence++;
        return Transaction.StringUtil.applySha256(
            Transaction.StringUtil.getStringFromKey(sender) +
                        Transaction.StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
                        );
    }

    //Signs all the data we don't wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = Transaction.StringUtil.getStringFromKey(sender) + Transaction.StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = Transaction.StringUtil.applyECDSASig(privateKey,data);
    }
    //Verifies the data we signed haunts been tampered with
    public boolean verifiySignature() {
        String data = Transaction.StringUtil.getStringFromKey(sender) + Transaction.StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        return Transaction.StringUtil.verifyECDSASig(sender, data, signature);
    }
}
