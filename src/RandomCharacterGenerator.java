import java.util.Random;


public class RandomCharacterGenerator {
	
	public static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	
	// Generate a random plaintext (i.e: string with random characters)
		// The length of the string is passed as argument 
		public static String generateRandomPlaintext(int length, int start, int end, Random rand)
		{
			StringBuffer buf = new StringBuffer();
			int randomIndex;
			for (int i=0; i < length; i++)
			{
				randomIndex = randomInteger(start, end,rand);
				buf.append(ALPHABET.charAt(randomIndex));
			}
			
			return buf.toString();
		}
	
	public static int randomInteger(int start, int end, Random rand){
	    if ( start > end ) {
	      throw new IllegalArgumentException("start cannot go beyond end.");
	    }
	    long range = (long)end - (long)start + 1;
	    long fraction = (long)(range * rand.nextDouble());
	    int randomNumber =  (int)(fraction + start);    
	    return randomNumber;
	  }
	

}

