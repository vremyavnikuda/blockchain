

public class java_blockchain {
    public static  void main (String[]args){
        Block genesusBlock=new Block("Hi im the first block","0");
        System.out.println("Hash for block 1: "+genesusBlock.hash);

        Block secondBlock=new Block("Yo im the second block",genesusBlock.hash);
        System.out.println("Hash for block 2: "+secondBlock.hash);

        Block thirdBlock=new Block("Hey im the third block", secondBlock.hash);
        System.out.println("Hash for block 3: "+secondBlock.hash);
    }
}
