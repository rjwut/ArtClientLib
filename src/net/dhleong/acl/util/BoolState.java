package net.dhleong.acl.util;

public enum BoolState {
    TRUE, FALSE,
    /** Not specified in packet */
    UNKNOWN;

    public boolean getBooleanValue() {
        // hacks? meh
        return ordinal() == 0;
    }

    public static BoolState from(boolean isTrue) {
        return isTrue ? TRUE : FALSE;
    }
}