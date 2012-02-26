package test;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class RainbowTable {
	
	// The rainbow table is stored in a HashMap
	private HashMap microTable;
	
	public RainbowTable(String startingPlaintext)
	{
		microTable = new HashMap();
		try {

            MessageDigest algorithm = MessageDigest.getInstance("MD5");
	        String tempPlaintext = startingPlaintext;
	        // Generate rainbow table with 1000 chains
            for (int i = 0; i < 1000; i++) {
            	// Each chain contain 1000 pairs of plaintext and hash
            	for (int j = 0; j < 1000; j++) {
	                algorithm.reset();
	                algorithm.update((tempPlaintext + i).getBytes());
	                byte msgDigest[] = algorithm.digest();
	                String value = ReductionFunction.reductionFunction(msgDigest);

	                if (value.charAt(0) > 'h' || value.charAt(0) < 'c') {
	                    throw new RuntimeException();
	                }
	                
	                if (j == 999)
	                {
	                	 // Add first plaintext of chain along with the last hash
	                	microTable.put((tempPlaintext+0), msgDigest);
	                }
	            }
            	// Generate a random length for the new plaintext
            	int plaintextLength = RandomCharacterGenerator.generateRandomLength(3,10);
            	// Generate a new starting plaintext for the next chain
            	tempPlaintext = RandomCharacterGenerator.generateRandomPlaintext(plaintextLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public HashMap getMicroTable()
	{
		return microTable;
	}
	
	  // Convert byte array to hex values and store it in a string
	  public static String getHex( byte [] msgDigest ) {
		
		  String HEXVALUES = "0123456789abcdef";
	    if ( msgDigest == null ) {
	      return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * msgDigest.length );
	    for ( final byte b : msgDigest ) {
	      hex.append(HEXVALUES.charAt((b & 0xF0) >> 4))
	         .append(HEXVALUES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	  }
	
	public static void main (String[]args)
	{
		// Create a new rainbow table with "helloworld" as starting plaintext
		HashMap rt = new RainbowTable("helloworld").getMicroTable();
		
		Set set = rt.entrySet();
	   // Get an iterator
	   Iterator i = set.iterator();
	   // Display the rainbow table
	   while(i.hasNext()) {
	   
		    Map.Entry chain = (Map.Entry)i.next();
		    // Display first word of the chain
		    System.out.print(chain.getKey() + ": ");
		    byte[] msgDigest = (byte[]) chain.getValue();
		    // Display the chain's last hash in Hexadecimal
			System.out.println(getHex(msgDigest));
	   }
	   
	   System.out.println("Micro table length: "+rt.size());
	}

}
