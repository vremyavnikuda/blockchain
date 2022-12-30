import com.google.gson.GsonBuilder;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class StringUtil {

    //Применяем алгоритм шифрования Sha256
    public static String applySha256(String input){

        try {
            // Создаем экземпляр MessageDigest с алгоритмом SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Применяем хэш-функцию SHA-256 к входной строке
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            // Создаем буфер для хранения результата хэширования в шестнадцатеричном виде
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            // Возвращаем результат
            return hexString.toString();
        }
        catch(Exception e) {
            // Перехватываем все исключения и выбрасываем новое исключение с сообщением об ошибке
            throw new RuntimeException(e);
        }
    }

    //Применяет подпись ECDSA и возвращает результат (в виде байтов).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            // Создаем экземпляр Signature с использованием алгоритма ECDSA
            dsa = Signature.getInstance("ECDSA", "BC");
            // Инициализируем Signature нашим секретным ключом
            dsa.initSign(privateKey);
            // Преобразуем входную строку в массив байтов
            byte[] strByte = input.getBytes();
            // Обновляем объект Signature этими байтами
            dsa.update(strByte);
            // Создаем цифровую подпись
            byte[] realSig = dsa.sign();
            // Сохраняем результат в массив output
            output = realSig;
        } catch (Exception e) {
            // Перехватываем все исключения и выбрасываем новое исключение с сообщением об ошибке
            throw new RuntimeException(e);
        }
        // Возвращаем результат
        return output;
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            // Создаем экземпляр Signature с использованием алгоритма ECDSA
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            // Инициализируем Signature нашим открытым ключом
            ecdsaVerify.initVerify(publicKey);
            // Преобразуем входную строку в массив байтов
            ecdsaVerify.update(data.getBytes());
            // Проверяем цифровую подпись
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            // Перехватываем все исключения и выбрасываем новое исключение с сообщением об ошибке
            throw new RuntimeException(e);
        }
    }


    public static String getJson(Object o) {
        // Создаем экземпляр GsonBuilder, устанавливаем опцию "форматирование вывода" и создаем экземпляр Gson
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

    // Возвращает строку сложности, чтобы сравнить с хешем. Например, сложность 5 вернет "00000"
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // Возвращает строковое представление ключа
    public static String getStringFromKey(Key key) {
        // Кодируем ключ в байтовый массив и возврашем результат
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // getMerkleRoot - это метод, который принимает список транзакций ArrayList и возвращает строку.
    // представляющую корень Меркла транзакций.
    public static String getMerkleRoot(ArrayList<Transactions> transactions) {
        // count - это количество транзакций в списке ArrayList
        int count = transactions.size();


        // previousTreeLayer - это список строк, которые будут использоваться для хранения хэшей транзакций в списке ArrayList
        // Изначально в списке хранятся идентификаторы транзакций всех транзакций в ArrayList
        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transactions transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        // treeLayer - это список, который будет использоваться для хранения хэшей предыдущего TreeLayer во время каждой итерации цикла
        List<String> treeLayer = previousTreeLayer;

        // Если в списке больше одного хэша, продолжаем цикл
        while(count > 1) {
            // Создаем новый список для хранения хэшей текущей итерации
            treeLayer = new ArrayList<String>();

            // Для каждой пары хэшей в предыдущем слое дерева вычислите хэш их конкатенации
            // и добавьте его в список treeLayer
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            // Обновляем количество хэшей в списке и устанавливаем previousTreeLayer в текущий treeLayer
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        // Если в списке treeLayer остался только один хэш, установите его в качестве корня Меркла.
        // В противном случае возвращается пустая строка
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}

