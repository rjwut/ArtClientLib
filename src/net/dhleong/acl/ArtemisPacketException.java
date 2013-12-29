package net.dhleong.acl;

public class ArtemisPacketException extends Exception {
    private static final long serialVersionUID = 6305993950844264082L;

    public ArtemisPacketException(String string) {
        super(string);
    }

    public ArtemisPacketException(Throwable t) {
        super(t);
    }
}
