package Transaction;

public class TransactionsInput {
    public String transactionOutputId;
    public TransactionsInput UTXO;

    public TransactionsInput(String transactionOutputId){
        this.transactionOutputId=transactionOutputId;
    }
}
