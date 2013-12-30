package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;

/**
 * Sent by the server when the game starts.
 */
public class GameStartPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    public static final int MSG_TYPE = 0x00;

    private final int mOffset;
    
    public GameStartPacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }

        reader.readUnknown("Unknown", 4);
        mOffset = reader.readInt();
    }

    /**
     * IDs starting offset...?
     */
    public int getOffset() {
        return mOffset;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Object ID offset = ").append(mOffset);
	}
}