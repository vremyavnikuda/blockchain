import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    //обратные данные будут простыми сообщениями
    public ArrayList<Transactions> transactions = new ArrayList<Transactions>();

    //время создание первого блока
    public long timeStamp;
    public int nonce;

    /*
    Конструктор класса Block
    Этот класс реализует блок в блокчейне.
    Он содержит информацию о предыдущем блоке,
    времени создания и хэше текущего блока.
    */
    public Block(String previousHash ) {

        //Сохраняем хэщ предыдушего блока в поле previousHash
        this.previousHash = previousHash;

        // Получаем текущее время и сохраняем его в поле timeStamp
        this.timeStamp = new Date().getTime();

        //Вычисляем хэш текущего блока и сохраняем его в поле hash
        this.hash = calculateHash();
    }

    /*
    Этот метод вычисляет хэш блока,
    конкатенируя строки, составляющие его:
    - хэш предыдущего блока
    - время создания блока
    - номер nonce
    - корневой хэш дерева Меркла
    Затем применяется хэш-функция SHA-256 к результату.
    */
    public String calculateHash() {
        // Конкатенируем строки, составляющие хэш
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        // Возвращаем результат
        return calculatedhash;
    }


    public void mineBlock(int difficulty) {

        // Вычисляем корневой хэш дерева Меркла на основе транзакций
        merkleRoot = StringUtil.getMerkleRoot(transactions);

        // Создаем целевую строку с "0" в количестве, равном уровню сложности
        String target = StringUtil.getDificultyString(difficulty);

        // Пока хэш блока не равен целевой строке, выполняем цикл
        while(!hash.substring( 0, difficulty).equals(target)) {
            // Увеличиваем nonce и вычисляем новый хэш
            nonce ++;
            hash = calculateHash();
        }
        // Выводим сообщение о том, что блок был добыт
        System.out.println("Блок найден : " + hash);
    }

    //Добавляем транзакцию в блок
    public boolean addTransaction(Transactions transaction) {


        // Если транзакция нулевая, возвращаем false
        if(transaction == null) return false;
        // Если это genesis block, проверяем транзакцию на валидность
        if((!"0".equals(previousHash))) {
            if((transaction.processTransaction() != true)) {
                // Выводим сообщение о том, что транзакция не прошла валидацию и отбрасываем ее
                System.out.println("Транзакцию не удалось обработать. Операция отклонена.");
                return false;
            }
        }
        // Добавляем транзакцию в список транзакций блока
        transactions.add(transaction);
        // Выводим сообщение
        System.out.println("Транзакция успешно добавлена в блок");
        return true;
    }

}