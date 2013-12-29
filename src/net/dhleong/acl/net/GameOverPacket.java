package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;

public class GameOverPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    public static final int MSG_TYPE = 0x06;

    public GameOverPacket(PacketReader reader) throws ArtemisPacketException {
    	super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}