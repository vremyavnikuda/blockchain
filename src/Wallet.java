import Transaction.Transactions;
import Transaction.TransactionsInput;
import Transaction.TransactionsOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public Wallet() {
        generateKeyPair();
    }

    //только utxos принадлежащие этому кошельку!!!!
    public HashMap<String, TransactionsOutput> UTXOs = new HashMap<String, TransactionsOutput>();


    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //метод возвращает баланс и сохраняет UTXO принадлежащие этому кошельку в UTXOs
    public float getBalance() {
        float total = 0;

        for (Map.Entry<String, TransactionsOutput> item : java_blockchain.UTXOs.entrySet()) {
            TransactionsOutput UTXO = item.getValue();
                /*если выходная транзакция принадлежит мне
                (если денежные средства на кошельке принадлежат мне)*/
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO); //добавляем его в наш список неизрасходованных транзакций
                total += UTXO.value;
            }
        }
        return total;
    }

    //генерируем и возвращаем новую транзакцию из данного кошелька
    public Transactions sendFunds(PublicKey _recipient, float value) {

        //Если баланс меньше отправляемой транзакции
        if (getBalance() < value) {
            System.out.println("#Недостаточно средств для отправки транзакции .Транзакция отклонена");
            return null;
        }
        //создаем массив входов (тип этого массива будет списки)
        ArrayList<TransactionsInput> inputs = new ArrayList<TransactionsInput>();
        float total = 0;
        for (Map.Entry<String, TransactionsOutput> item : UTXOs.entrySet()) {
            TransactionsOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionsInput(UTXO.id));
            if (total > value) {
                break;
            }
        }
        Transactions newTransactions = new Transactions(publicKey, _recipient, value, inputs);
        newTransactions.generateSignature(privateKey);
        for (TransactionsInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransactions;
    }
}
