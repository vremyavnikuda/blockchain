public class TransactionsInput {

    //TransactionOutputs -> transactionId
    public String transactionOutputId;

    //Объект содержит информацию о выходных транзакций на которую ссылается этот объект
    public TransactionsOutput UTXO;


    //конструктор с идентификатором выходной транзакции и сохраняет ее в поле transactionOutputId
    public TransactionsInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
