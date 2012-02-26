import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashAlgorithms {
	
	 public static String makeMD5Hash(String block) throws NoSuchAlgorithmException
	    {
				MessageDigest algorithm = MessageDigest.getInstance("MD5");
				algorithm.reset();
				algorithm.update(block.getBytes());
				byte msgDigest[] = algorithm.digest();
				
				return stringUtils.StringAsHex.getHex(msgDigest);
	    }
	    public static String makeSHA1Hash(String block) throws NoSuchAlgorithmException
	    {
				MessageDigest algorithm = MessageDigest.getInstance("SHA1");
				algorithm.reset();
				algorithm.update(block.getBytes());
				byte msgDigest[] = algorithm.digest();
				
				return stringUtils.StringAsHex.getHex(msgDigest);
	    }

}
