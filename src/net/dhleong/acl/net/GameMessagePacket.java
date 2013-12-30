package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;

/**
 * "Toast" messages sent by the server.
 */
public class GameMessagePacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    public static final int MSG_TYPE = 0x0a;

    private final String mMessage;

    public GameMessagePacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }

        mMessage = reader.readString();
    }

    /**
     * The contents of the "toast" message.
     */
    public String getMessage() {
        return mMessage;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mMessage);
	}
}