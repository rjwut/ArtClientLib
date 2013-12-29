package net.dhleong.acl.util;

public class TextUtil {
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b);
        if (hex.length() >= 2) {
            return hex.substring(hex.length()-2);
        }

        return String.format("0%s", hex);
    }

    public static String byteArrayToHexString(byte[] data) {
        return byteArrayToHexString(data, 0, data.length);
    }
    
    public static String byteArrayToHexString(byte[] data, int offset, int length) {
        StringBuilder buf = new StringBuilder();
        final int end = offset + length;

        for (int i=offset; i < end; i++) {
            byte b = data[i];
            
            buf.append(byteToHex(b));
        }
        return buf.toString();
    }

    public static String intToHex(int val) {
        String hex = Integer.toHexString(val);

        if (hex.length() >= 8) {
            return hex.substring(hex.length()-8);
        }

        return String.format("%8s", hex).replace(' ', '0');
    }

    public static int hexToInt(char byte1, char byte2) {
        return Integer.parseInt(String.format("%c%c", byte1, byte2), 16);
    }

    public static byte[] hexToByteArray(String hex) {
    	int len = hex.length();

    	if (len % 2 == 1) {
    		throw new IllegalArgumentException(
    				"Hex strings must contain two characters per byte"
    		);
    	}

    	byte[] bytes = new byte[len / 2];

    	for (int i = 0; i < len; i += 2) {
    		String hexByte = hex.substring(i, i + 2);
    		bytes[i / 2] = (byte) Integer.parseInt(hexByte, 16);
    	}

    	return bytes;
    }
}
