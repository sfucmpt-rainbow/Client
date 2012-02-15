
import java.math.BigInteger;
import java.security.MessageDigest;

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
    /*
     * Reduction function, just as a test will go from cccccc to hhhhhh Will
     * return a string from cccccc to hhhhhh
     */

    public static String reductionFunction(byte[] hash) {
        return reductionFunction(hash, 0);
    }
    public static String begin = "cccccc";
    public static String end = "hhhhhh";

    public static String reductionFunction(byte[] hash, int reductionNumber) {
        int[] start = new int[6];
        // Add the beginning string into the start array
        long beginValue = toNumber(begin.toCharArray());
        int index = 5;
        while (beginValue > 0) {
            start[index--] = (int) (beginValue % ALPHABET.length());
            beginValue /= ALPHABET.length();
        }
        //calculate how many strings there are from aaaaa to mmmmm        
        long blockLength = toNumber(end.toCharArray()) - toNumber(begin.toCharArray());
        // convert hash to 128bit integer
        BigInteger value = new BigInteger(hash);
        // Add in the reduction number to have different functions
        value = value.add(BigInteger.valueOf(reductionNumber));
        // Mod by the length of the block so we have values from 0 to blockLength
        value = value.mod(BigInteger.valueOf(blockLength));
        // Convert back to long for easier calculations
        long newValue = value.longValue();
        // Do a divide/mod loop to pull out individual letters
        for (int i = 0; i < 6; i++) {
            int offset = (int) (newValue % ALPHABET.length());
            newValue = newValue / ALPHABET.length();
            // Add to start array
            start[5 - i] += offset;
            // Check if we have to "carry" to the next significant bit
            if (start[5 - i] >= ALPHABET.length()) {
                start[5 - i] -= ALPHABET.length();
                start[5 - i - 1]++;
            }
        }
        // Generate the output string
        String output = "";
        for (int i = 0; i < 6; i++) {
            output += ALPHABET.charAt(start[i]);
        }

        return output;
    }

    public static void main(String[] s) {
        try {

            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            // Run 1000 tests, should not get anything where the first letter is past h or before c
            // Since the range is from cccccc to hhhhhh
            for (int i = 0; i < 1000; i++) {
                algorithm.reset();
                algorithm.update(("teststring" + i).getBytes());
                byte msgDigest[] = algorithm.digest();
                String value = reductionFunction(msgDigest);
                System.out.println(value);
                if (value.charAt(0) > 'h' || value.charAt(0) < 'c') {
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}