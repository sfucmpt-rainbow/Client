import java.security.NoSuchAlgorithmException;


public class Chains {
    
    public static void permute(String block) 
    { 
    	permute("", block); 
    	System.out.println(); 
    }
    
    private static void permute(String prefix, String block) {
        int strLength = block.length();
        if (strLength == 0){
        	try {
				System.out.print(prefix+"-"+HashAlgorithms.makeMD5Hash(block)+"-");
				//System.out.print(prefix+"-"+HashAlgorithms.makeSHA1Hash(block)+"-");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
        }
        else {
            for (int i = 0; i < strLength; i++)
               permute(prefix + block.charAt(i), block.substring(0, i) + block.substring(i+1, strLength));
        }

    }
    
    public static void main(String[] args) {
        long x = System.currentTimeMillis();
        
        int blockLength = 5;
        
        if (args.length > 0)
        	blockLength = Integer.parseInt(args[0]);
        
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String blockCharacters = alphabet.substring(0, blockLength);
        permute(blockCharacters);
        
        long x2 = System.currentTimeMillis();
        System.out.println("Duration: " + (x2-x));

     }

}
