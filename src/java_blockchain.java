import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class java_blockchain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionsOutput> UTXOs = new HashMap<String,TransactionsOutput>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transactions genesisTransaction;

    public static void main(String[] args) {

        //Настройка Bouncey в качестве поставщика услуг безопасности
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Создаем локальные кошельки walletA и walletB
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet vremyvnikuda = new Wallet();

        //Создаем транзакцию genesis которая отправляет 100 coin на кошелек A
        genesisTransaction = new Transactions(vremyvnikuda.publicKey, walletA.publicKey, 100f, null);

        //Вручную подписываем транзакцию genesis
        genesisTransaction.generateSignature(vremyvnikuda.privateKey);

        //В ручную устанавливаем идентификатор транзакции
        genesisTransaction.transactionId = "0";

        //Вручную добавляем вывод транзакции
        genesisTransaction.outputs.add(new TransactionsOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));

        //сохраняем нашу первую транзакцию в списке UTXOs
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Создание и добыча блока Genesis... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //Тестим
        Block block1 = new Block(genesis.hash);
        System.out.println("\nБаланс WalletA: " + walletA.getBalance());
        System.out.println("\nWalletA пытается отправить средства (40) на WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nБаланс WalletA: " + walletA.getBalance());
        System.out.println("Баланс WalletB: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Попытка отправить больше средств (1000), чем есть...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nБаланс WalletA: " + walletA.getBalance());
        System.out.println("Баланс WalletB: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB пытается отправить средства (20) на WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nБаланс WalletA: " + walletA.getBalance());
        System.out.println("Баланс WalletB: " + walletB.getBalance());

        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        //Временный рабочий список неизрасходованных транзакций в данном состоянии блока
        HashMap<String,TransactionsOutput> tempUTXOs = new HashMap<String,TransactionsOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //цикл через блокчейн для проверки хэшей
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Текущие хэши не равны");
                return false;
            }
            //Сравниваем предыдущий хэш и зарегистрированный предыдущий хэш
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Предыдущий Хэши не равны");
                return false;
            }
            //проверяем равны ли хэши
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#Этот блок не был добыт");
                return false;
            }

            //Задаем цикл через транзакции блокчейн
            TransactionsOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transactions currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Подпись на сделке(" + t + ") недействительна");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Входы примечания равны выходам при транзакции(" + t + ")");
                    return false;
                }

                for(TransactionsInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Ссылающийся вход на транзакцию(" + t + ") отсутствует");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Ссылающийся вход транзакции(" + t + ") недействителен");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionsOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Транзакция (" + t + ") выходной получатель не тот, кем он должен быть");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Транзакция(" + t + ") выход 'change' не является отправителем.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
