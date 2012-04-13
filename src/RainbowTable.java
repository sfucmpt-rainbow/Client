import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import rainbow.scheduler.partition.AlphabetGenerator;
import rainbow.scheduler.partition.PlaintextSpace;


public class RainbowTable {

	// The rainbow table is stored in a TreeMap
	private TreeMap<String, String> microTable;
	private int chainLength;
	private int chainCount;
	private String startingPlaintext;
	private String endingPlaintext;
	// For generating random integers
	private Random randomGenerator;
	
	private static final int MINLENGTH_PLAINTEXT = 3;
	private static final int MAXLENGTH_PLAINTEXT = 10;
	// Using only lowercase letters for testing
	private static final int ALPHABETSIZE = 26;
	
	PlaintextSpace pSpace;
	int blockSize;
	
	public RainbowTable(int chainLength, int chainCount, String alphabet, int blockNumber, int textLength)
	{
		this.chainCount = chainCount;
		this.chainLength = chainLength;
		//this.startingPlaintext = startingPlaintext;
		
		microTable = new TreeMap<String,String>();
		//visitedPlaintexts = new HashMap<String,Integer>();
		randomGenerator = new Random();
		pSpace = new PlaintextSpace(alphabet,blockNumber, textLength);
		startingPlaintext = pSpace.getText(0);
		//System.out.println("Starting plaintext --------------> "+startingPlaintext);
		int lastIndex = PlaintextSpace.BLOCK_SIZE-1;
		//System.out.println(lastIndex);
		assert(lastIndex!=-1);
		endingPlaintext = pSpace.getText(lastIndex);
		//System.out.println("Ending plaintext --------------> "+endingPlaintext);
	}
	
	public void generateMicroTable() throws NoSuchAlgorithmException
	{
		byte msgDigest[] = null;
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		String value;
		int blockCount = 1;
		for (int i = 0; i < chainCount; i++) {
			// Store startingPlaintext in another variable to avoid getting modified
			value = startingPlaintext;
			for (int j = 0; j < chainLength; j++) {
				algorithm.reset();
				algorithm.update(value.getBytes());
				msgDigest= algorithm.digest();
				value = ReductionFunction.reductionFunction(msgDigest, j, pSpace.getText(0), endingPlaintext);
			}
			// Add first plaintext of chain along with the last hash
			microTable.put(startingPlaintext, getHex(msgDigest));
			int randomIndex = RandomCharacterGenerator.randomInteger(1, PlaintextSpace.BLOCK_SIZE, randomGenerator);
			//System.out.println("Random Index: "+randomIndex);
			startingPlaintext = pSpace.getText(randomIndex);
			// Keep generating random plaintexts until it finds one that haven't been used
			while (microTable.containsKey(startingPlaintext))
			{
				//blockCount++;
				randomIndex = RandomCharacterGenerator.randomInteger(1, PlaintextSpace.BLOCK_SIZE, randomGenerator);
				//System.out.println("Random Index: "+randomIndex);
				startingPlaintext = pSpace.getText(randomIndex);
				//startingPlaintext = randomPlaintext();
			}
			blockCount++;
		
		}
	}
	
	// Generate a random plaintext
	public String randomPlaintext()
	{
		int newPlaintextLength = RandomCharacterGenerator.randomInteger(MINLENGTH_PLAINTEXT, MAXLENGTH_PLAINTEXT, randomGenerator);
		return RandomCharacterGenerator.generateRandomPlaintext(newPlaintextLength,0,ALPHABETSIZE-1,randomGenerator);
	}
	
	
	public TreeMap getMicroTable()
	{
		return microTable;
	}
	
	// Convert byte array to hex values and store it in a string
	
	public static String getHex( byte [] msgDigest ) {
		final StringBuilder hex = new StringBuilder( 2 * msgDigest.length );
		for (byte b : msgDigest) {
			hex.append(String.format("%02x", b));
		}
		return hex.toString();
	}
	
	/*
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
	*/
	
	// Search a hash from the micro table
	public String getPlaintextFromHash(String hash) throws NoSuchAlgorithmException
	{
		// Storing unique plaintexts
		HashSet visitedPlaintexts = new HashSet();
		visitedPlaintexts.add(hash);
		
		byte msgDigest[] = null;
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		
		boolean found = false;
		
		Set set = microTable.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		
		String foundValue = null;
		// Store hash in a new variable to avoid getting modified
		String newHash = hash;
		int count = 0;
		while (!found){
			
			while(i.hasNext()) {
			
				Map.Entry chain = (Map.Entry)i.next();
				// Check if the hash corresponds to on the last hashes of each chain
				if (chain.getValue().equals(newHash))
				{
					// Store the first plaintext of the chain
					String value = (String) chain.getKey();
					// Using the reduction function to find the right plaintext from the chain
					for (int j = 0; j < chainLength; j++) {
						algorithm.reset();
						algorithm.update(value.getBytes());
						msgDigest= algorithm.digest();
						//System.out.println(getHex(msgDigest) + "--------------->"+value);
						if (getHex(msgDigest).equals(hash))
						{
							found = true;
							foundValue = value;
							break;
						}
							value = ReductionFunction.reductionFunction(msgDigest, j, pSpace.getText(0), endingPlaintext);
						}
					}
				}
				if (!found) // Hash wasn't found among last hashes from the chains
				{
					// Reduce the hash
					if (count>=chainLength)
					{
						count = 0;
					}
					String reducedHash = ReductionFunction.reductionFunction(newHash.getBytes(),count,pSpace.getText(0), endingPlaintext);
					algorithm.reset();
					algorithm.update(reducedHash.getBytes());
					msgDigest= algorithm.digest();
					// Generate a new hash
					newHash = getHex(msgDigest);	
					int count2 = count;
					// Check if the new hash is not already stored in the HashSet
					while (!visitedPlaintexts.add(newHash))
					{
						reducedHash = ReductionFunction.reductionFunction(newHash.getBytes(), count2, pSpace.getText(0), endingPlaintext);
						
						algorithm.reset();
						algorithm.update(reducedHash.getBytes());
						msgDigest= algorithm.digest();
						
						newHash = getHex(msgDigest);
						count2++;
						if (count2>=chainLength)
						{
							count2 = 0;
						}
					}
				
					// Reset the iterator
					set = microTable.entrySet();
					i = set.iterator();
				}
				count++;
			}
			return foundValue;
	}
	
	public static void main (String[]args) throws NoSuchAlgorithmException
	{
	
		final int CHAIN_LENGTH = 1000;
		final int CHAIN_COUNT = 1000;
		
		// Create a new rainbow table with "helloworld" as starting plaintext
		// and 1000 chains, with length 1000
		RainbowTable rt = new RainbowTable(CHAIN_LENGTH,CHAIN_COUNT,(AlphabetGenerator.generateAlphabet(
				AlphabetGenerator.Types.LOWER_CASE)), 5, 5);
		
		long start = 0, end = 0;
		try {
		
			start = System.currentTimeMillis();
			
			rt.generateMicroTable();
			
			end = System.currentTimeMillis();
			
		} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Format duration to display: xx min yy sec
		String duration = String.format("%d min, %d sec",
		TimeUnit.MILLISECONDS.toMinutes(end - start),
		TimeUnit.MILLISECONDS.toSeconds(end - start) -
		TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end - start))
		);
		
		System.out.println("Duration : " + duration);
		
		TreeMap microTable = rt.getMicroTable();
		Set set = microTable.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display the micro table
		while(i.hasNext()) {
			Map.Entry chain = (Map.Entry)i.next();
			System.out.print(chain.getKey() + ": ");
			System.out.println(chain.getValue());
		}
		
		assert(microTable.size() == CHAIN_COUNT);
		
		System.out.println("Micro table length: "+microTable.size());
		
		System.out.println("Searching hash: "+"f6a94670a01d5eabd5132818a1957d5f");
		
		// Get plaintext from hash (last hash of chain 1)
		String plaintext = rt.getPlaintextFromHash("f6a94670a01d5eabd5132818a1957d5f");
		
		System.out.println("Found plaintext: "+plaintext);
	
	}

}


