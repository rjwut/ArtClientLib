package net.dhleong.acl.util;

public enum BoolState {
    TRUE, FALSE, UNKNOWN;

    public boolean getBooleanValue() {
        // hacks? meh
        return ordinal() == 0;
    }

    public static BoolState from(boolean isTrue) {
        return isTrue ? TRUE : FALSE;
    }

    /**
     * Returns false if state is null or {@link #UNKNOWN},
     *  else True
     * @param state
     * @return
     */
    public static boolean isKnown(BoolState state) {
        return state == TRUE || state == FALSE;
    }

    /**
     * Safely get the value, handling null or
     *  "unknown" as false
     * @param reverseState
     * @return
     */
    public static boolean safeValue(BoolState reverseState) {
        return reverseState == TRUE;
    }
}