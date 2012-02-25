package test;

public class RandomCharacterGenerator {
	
	/* Generate a random character from a range.
	 * e.g: any character from the range c-h
	*/
	public static char generateRandomCharacter(int min, int max)
	{
		int index = min + (int)(Math.random() * ((max - min) + 1));
		return ReductionFunction.ALPHABET.charAt(index);
	}
	
	// Generate a random size for the plaintext
	public static int generateRandomLength(int min, int max)
	{
		int length = min + (int)(Math.random() * ((max - min) + 1));
		return length;
	}
	
	// Generate a random plaintext (i.e: string with random characters)
	// The length of the string is passed as argument 
	public static String generateRandomPlaintext(int length)
	{
		StringBuffer buf = new StringBuffer();
		
		for (int i=0; i < length; i++)
		{
			buf.append(generateRandomCharacter(0,25));
		}
		
		return buf.toString();
	}
	
//	public static void main (String[] args)
//	{
//		for (int i=0; i < 10000000; i++)
//		{
//			System.out.println(generateRandomPlaintext(generateRandomLength(3,10)));
//			
//		}
//	}

}
