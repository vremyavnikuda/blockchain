import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


public class Transactions {

    //хэш транзакции
    public String transactionId;

    //открытый ключ отправителя транзакции
    public PublicKey sender;

    //открытый ключ получателя транзакции
    public PublicKey reciepient;

    //сумма транзакции
    public float value;

    //цифровая подпись, которая используется для проверки подлинности транзакции
    public byte[] signature;


    //inputs список объектов TransactionsInput, которые представляют входные данные для транзакции
    public ArrayList<TransactionsInput> inputs = new ArrayList<TransactionsInput>();

    //список объектов TransactionsOutput, которые представляют выходы для транзакции
    public ArrayList<TransactionsOutput> outputs = new ArrayList<TransactionsOutput>();


    //приблизительный подсчет того, сколько транзакций было создано
    private static int sequence = 0;

    //Конструктор для класса Transactions
    //принимает четыре аргумента: открытый ключ отправителя, открытый ключ получателя, значение транзакции,
    //список объектов TransactionsInput, представляющих входные данные для транзакции.
    public Transactions(PublicKey from, PublicKey to, float value,  ArrayList<TransactionsInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        //Проверка подписи транзакции. Если подпись не прошла проверку, выводится сообщение и функция возвращает false.
        if(verifySignature() == false) {
            System.out.println("#Подпись транзакции не удалось проверить");
            return false;
        }

        //Сбор входных транзакций
        for(TransactionsInput i : inputs) {
            i.UTXO = java_blockchain.UTXOs.get(i.transactionOutputId);
        }

        //Проверка валидности транзакции
        if(getInputsValue() < java_blockchain.minimumTransaction) {
            System.out.println("Транзакционные входы слишком малы: " + getInputsValue());
            System.out.println("Пожалуйста, введите сумму, большую, чем " + java_blockchain.minimumTransaction);
            return false;
        }

        //Генерация выходных транзакций

        //считываем остаток средств
        float leftOver = getInputsValue() - value;
        //вычисляем хэш транзакции
        transactionId = calulateHash();
        //создаем первую выходную транзакция, которая отправляет сумму value получателю (this.recipient)
        outputs.add(new TransactionsOutput( this.reciepient, value,transactionId));
        //создаем вторую выходную транзакцию, которая отправляет остаток средств (leftOver) отправителю (this.sender)
        outputs.add(new TransactionsOutput( this.sender, leftOver,transactionId));

        //перебираем все входные транзакции (outputs) и добавляем их в список UTXOs
        for(TransactionsOutput o : outputs) {
            java_blockchain.UTXOs.put(o.id , o);
        }

        //перебираем все входные транзакции (inputs)
        for(TransactionsInput i : inputs) {
            //если не нашел транзакцию в списке UTXO ,то пропускаем итерацию
            if(i.UTXO == null) {
                continue;
            }
            //и удаляем их из списка UTXOs
            java_blockchain.UTXOs.remove(i.UTXO.id);
        }
        //возвращаем true если транзакция прошла успешно
        return true;
    }

    public float getInputsValue() {
        //создаем переменную total которая будет содержать в себе сумму всех входных транзакций
        //изначально присвоим ей значение 0
        float total = 0;
        //перебираем все входные транзакции (inputs) для кажной из них
        for(TransactionsInput i : inputs) {
            if(i.UTXO == null) continue;
            //каждую найденную выходящую транзакцию суммируем её значение (value) в переменную total.
            total += i.UTXO.value;
        }
        //возвращаем значение total
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }


    //генерируем подпись для текущей транзакции
    public boolean verifySignature() {
        //создаем строку data в которую закладываем строковое представление отправителя, получателя и суммы текущей транзакции
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;

        //полученную подпись сохраняем в поле signature текущей транзакции
        return StringUtil.verifyECDSASig(sender, data, signature);
    }


    public float getOutputsValue() {
        //создаем переменную total которая будет содержать сумму всех выходных транзакций
        float total = 0;
        //перебираем все входные транзакции (outputs)
        for(TransactionsOutput o : outputs) {
            //суммируем значение в переменную
            total += o.value;
        }
        //возвращаем значение переменной
        return total;
    }


    //считаем хэш текущий транзакции
    private String calulateHash() {
        //увеличиваем значение sequence текущей транзакции,когда у двух разных транзакций окажется одинаковый хэш
        sequence++;
        return StringUtil.applySha256(
                //считаем хэш для строки data //полученный хэш возвращаем в качестве результата работы
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }
}
