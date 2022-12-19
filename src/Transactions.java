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
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
                        );
    }

    public void generateSingnature(PrivateKey privateKey){
        String data=StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value);
        signature=StringUtil.applyECDSASig(privateKey,data);
    }
    public boolean verifiySignature(){
        String data =StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value);
        return StringUtil.verifyECDSASing(sender,data,signature);
    }
}
