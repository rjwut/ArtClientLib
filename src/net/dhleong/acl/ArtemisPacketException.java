package net.dhleong.acl;

/**
 * Thrown when ArtClientLib encounters a problem while attempting to parse a
 * packet of a known type. Unknown packets don't throw this exception;
 * ArtClientLib creates UnknownPacket objects for them.
 */
public class ArtemisPacketException extends Exception {
    private static final long serialVersionUID = 6305993950844264082L;

    /**
     * @param string A description of the problem
     */
    public ArtemisPacketException(String string) {
        super(string);
    }

    /**
     * @param t The exception that caused ArtemisPacketException to be thrown
     */
    public ArtemisPacketException(Throwable t) {
        super(t);
    }
}
