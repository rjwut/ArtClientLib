package net.dhleong.acl.util;

public class TextUtil {

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b);
        if (hex.length() >= 2)
            return hex.substring(hex.length()-2);
        else
            return String.format("0%s", hex);
//        if (b <= 0x0F && b >= 0){
//            buf.append('0');
//        }
//        final String hex = Integer.toHexString(b);
//        //            System.out.println("Read: " + hex + "(" + (b <= 0x0F));
//        final int len = hex.length();
//        if (len > 2 && (b > 0x0F || b < 0))
//            return hex.substring(len-2);
//        else if (len > 2)
//            return String.format("0%s", hex.substring(len-1));
//        else
//            return hex;
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
        if (hex.length() >= 8)
            return hex.substring(hex.length()-8);
        else
            return String.format("%8s", hex).replace(' ', '0');
    }

}
