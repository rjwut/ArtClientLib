package net.dhleong.acl.util;

public class Util {
    /** stupid linear search */
    public static final int findInArray(float[] haystack, float needle) {
        for (int i=0; i<haystack.length; i++) {
            if (haystack[i] == needle)
                return i;
        }
        return -1;
    }
}
