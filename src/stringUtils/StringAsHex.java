package stringUtils;

public class StringAsHex {
	
	static final String HEXVALUES = "0123456789abcdef";
	  public static String getHex( byte [] msgDigest ) {
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


}
