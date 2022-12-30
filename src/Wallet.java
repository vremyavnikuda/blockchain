import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String,TransactionsOutput> UTXOs = new HashMap<String,TransactionsOutput>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {


            // Генерирует новую пару ключей (открытый и закрытый) с помощью эллиптической кривой "prime192v1"
            // и алгоритма случайных чисел "SHA1PRNG".

            // Создаем объект KeyPairGenerator с алгоритмом "ECDSA" и провайдером "BC"
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");

            // Создаем объект SecureRandom с алгоритмом "SHA1PRNG"
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Создаем экземпляр ECGenParameterSpec с названием эллиптической кривой "prime192v1"
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Инициализируем KeyPairGenerator с параметрами ecSpec и random
            keyGen.initialize(ecSpec, random);
            // Генерируем новую пару ключей с помощью KeyPairGenerator
            KeyPair keyPair = keyGen.generateKeyPair();

            // Сохраняем закрытый ключ в переменную privateKey
            privateKey = keyPair.getPrivate();
            // Сохраняем открытый ключ в переменную
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            // Если во время генерации ключей произошло исключение,
            // то сгенерируем исключение типа RuntimeException с переданным объектом исключения в качестве аргумента
            throw new RuntimeException(e);
        }
    }


    // Возвращает баланс указанного адреса (публичного ключа)
    public float getBalance() {
        float total = 0;
        // Перебираем все непотраченные транзакционные выходы (UTXO) в системе
        for (Map.Entry<String, TransactionsOutput> item: java_blockchain.UTXOs.entrySet()){
            // Получаем текущий UTXO
            TransactionsOutput UTXO = item.getValue();
            // Если UTXO принадлежит указанному адресу (публичному ключу)
            if(UTXO.isMine(publicKey)) {
                // Добавляем UTXO в список непотраченных транзакционных выходов (UTXO) указанного адреса
                UTXOs.put(UTXO.id,UTXO);
                // Добавляем сумму UTXO к общей сумме непотраченных транзакций указанного адреса
                total += UTXO.value ;
            }
        }
        // Возвращаем общую сумму непотраченных транзакций указанного адреса
        return total;
    }



    //отправляем указанную сумму на указанный адресс (публичный ключ)
    public Transactions sendFunds(PublicKey _recipient,float value ) {
        // Проверяем, хватает ли нам средств для отправки транзакции
        if(getBalance() < value) {
            System.out.println("#Недостаточно средств для отправки транзакции. Транзакция отклонена.");
            // Если средств не хватает, то возвращаем null
            return null;
        }
        // Создаем список транзакционных входов
        ArrayList<TransactionsInput> inputs = new ArrayList<TransactionsInput>();
        float total = 0;
        // Перебираем все непотраченные транзакционные выходы (UTXO) указанного адреса
        for (Map.Entry<String, TransactionsOutput> item: UTXOs.entrySet()){
            // Получаем текущий UTXO
            TransactionsOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionsInput(UTXO.id));
            if(total > value) break;
        }
        //создаем новую транзакцию с отправителем (publicKey),получателем (_recipient) ,суммой (value) и списком входов (inputs)
        Transactions newTransaction = new Transactions(publicKey, _recipient , value, inputs);
        //генерируем подпись транзакции с использованием закрытого ключа
        newTransaction.generateSignature(privateKey);
        //перебираем все входные транзакции
        for(TransactionsInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        //возвращаем новую транзакцию
        return newTransaction;
    }
}
