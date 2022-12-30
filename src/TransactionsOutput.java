import java.security.PublicKey;

public class TransactionsOutput {

    //уникальный идентификатор выходной транзакции
    public String id;

    //содержит открытый ключ получателя транзакции
    public PublicKey reciepient;

    //содержит сумму транзакции
    public float value;

    //идентификатор транзакции в которой была создана эта выходная транзакция
    public String parentTransactionId;

    //Конструктор

    //собираем информацию о выходной транзакции
    //проверяем принадлежит ли эта транзакция определенному пользователю
    //(по совпадению его открытого ключа с ключом получателя транзакции)
    public TransactionsOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    //проверяем ключ с ключем получателя выходной транзакции
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }

}
