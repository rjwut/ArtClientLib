package net.dhleong.acl.util;

/**
 * Miscellaneous unloved stuff that doesn't have a home right now. But don't
 * worry, we love you and will find you a home.
 */
public class Util {
	public static boolean debug = false;

	/** stupid linear search */
    public static final int findInArray(float[] haystack, float needle) {
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i] == needle) {
                return i;
            }
        }

        return -1;
    }
}
