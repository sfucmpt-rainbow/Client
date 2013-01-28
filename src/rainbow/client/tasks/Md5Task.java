package rainbow.client.tasks;

import rainbowpc.node.messages.WorkMessage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Task {
	private static final int MD5LENGTH = 128 / 8;
	private static final int NOT_FOUND = -1;

	public static WorkMessage md5BruteForce(String alphabet, WorkMessage msg) {
		try {
			MessageDigest hasher = MessageDigest.getInstance("md5");
			String max = getMax(alphabet, msg.getStringLength());			

			for (long i = msg.getStartIndex(); i < msg.getEndIndex(); i++) {
				String current = getStringFromIndex(i, msg.getStringLength(), alphabet);
				System.out.println(current);
				hasher.reset();
				byte[] buffer = hasher.digest(current.getBytes());
				if (matchesTarget(buffer, msg.getTarget())) {
					System.out.println("found! " + current);
					msg.markFound(current);
					break;
				}
				else if (current.equals(max)) {
					break;
				}
			}

		} catch (NoSuchAlgorithmException e) {
			
		}
		return msg;
	}

	private static String getMax(String alphabet, int length) {
		StringBuilder b = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			b.append(alphabet.charAt(alphabet.length() - 1));
		}
		return b.toString();
	}

	private static String getStringFromIndex(long index, int size, String alphabet) {
		StringBuilder builder = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int charIndex = (int)(index % alphabet.length());
			index /= alphabet.length();
			builder.append(alphabet.charAt(charIndex));
		}
		builder.reverse();
		return builder.toString();
	}

	private static boolean matchesTarget(byte[] buffer, String target) {
		for (int i = 0; i < buffer.length; i++) {
			if (!String.format("%02x", buffer[i]).equals(target.substring(i * 2, i * 2 + 2))) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		WorkMessage dummy = new WorkMessage(
			"fake",
			"fbade9e36a3f36d3d676c1b808451dd7",
			0, 
			0, 
			0,
			15000, 
			1
		);
		md5BruteForce(alphabet, dummy);
		System.out.println(dummy.targetFound());
	}
}
