
import java.math.BigInteger;
import java.security.MessageDigest;

import rainbow.scheduler.partition.AlphabetGenerator;
import rainbow.scheduler.partition.PlaintextSpace;

public class ReductionFunction {

    public static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static long toNumber(char[] value) {
        long number = 0;
        for (int i = value.length - 1; i >= 0; i--) {
            number *= ALPHABET.length();
            number += ALPHABET.indexOf(value[i]);
        }
        return number;
    }

    public static String reductionFunction(byte[] hash, int reductionNumber, String begin, String end) {
    	int chars_count = begin.length();
    	int lastIndex = chars_count-1;
        int[] start = new int[begin.length()];
        // Add the beginning string into the start array
        long beginValue = toNumber(begin.toCharArray());
        int index = begin.length()-1;
        while (beginValue > 0) {
            start[index--] = (int) (beginValue % ALPHABET.length());
            beginValue /= ALPHABET.length();
        }
        //calculate how many strings there are from begin to end      
        long blockLength = Math.abs(toNumber(end.toCharArray()) - toNumber(begin.toCharArray()));
        // convert hash to 128bit integer
        BigInteger value = new BigInteger(hash);
        // Add in the reduction number to have different functions
        value = value.add(BigInteger.valueOf(reductionNumber));
        // Mod by the length of the block so we have values from 0 to blockLength
        value = value.mod(BigInteger.valueOf(blockLength));
        
        // Convert back to long for easier calculations
        long newValue = value.longValue();
        // Do a divide/mod loop to pull out individual letters
        for (int i = 0; i < chars_count; i++) {
            int offset = (int) (newValue % ALPHABET.length());
            newValue = newValue / ALPHABET.length();
            // Add to start array
            start[lastIndex - i] += offset;
            // Check if we have to "carry" to the next significant bit
            if (start[lastIndex - i] >= ALPHABET.length()) {
                start[lastIndex - i] -= ALPHABET.length();
                start[lastIndex - i - 1]++;
            }
        }
        // Generate the output string
        String output = "";
        for (int i = 0; i < chars_count; i++) {
            output += ALPHABET.charAt(start[i]);
        }

        return output;
    }
}
