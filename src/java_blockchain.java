import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class java_blockchain {

    public static ArrayList<Block>blockchain=new ArrayList<Block>();


    public static  void main (String[]args){
        blockchain.add(new Block("Hi im the first block","0"));
        blockchain.add(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).hash));
        blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).hash));

        String blockchainFix=new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainFix);
    }
}
