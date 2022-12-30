import java.security.PublicKey;

public class TransactionsOutput {
    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;



    public TransactionsOutput(PublicKey reciepient,float value,String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
    }
    public boolean isMine(PublicKey publicKey){
        return (publicKey==reciepient);
    }
}
