package net.dhleong.acl.net.comms;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * Received when an incoming COMMs message arrives.
 */
public class CommsIncomingPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xD672C35F;
    
    private final int mPriority;
    private final String mFrom;
    private final String mMessage;

    public CommsIncomingPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        mPriority = reader.readInt();
        mFrom = reader.readString();
        mMessage = reader.readString().replace('^', '\n');
    }

    /**
     * Returns the message priority, with lower values having higher priority.
     * @return An integer between 0 and 8, inclusive
     */
    public int getPriority() {
        return mPriority;
    }

    public String getFrom() {
        return mFrom;
    }
    
    public String getMessage() {
        return mMessage;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("from ").append(mFrom).append(": ").append(mMessage);
	}
}