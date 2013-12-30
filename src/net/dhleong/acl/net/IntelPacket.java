package net.dhleong.acl.net;

import net.dhleong.acl.enums.ConnectionType;

/**
 * Provides intel on another vessel, typically as the result of a level 2 scan.
 * @author rjwut
 */
public class IntelPacket extends BaseArtemisPacket {
	public static final int TYPE = 0xee665279;

	private final int mId;
	private final String mIntel;

	public IntelPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
    	mId = reader.readInt();
    	reader.readUnknown("Unknown", 1);
        mIntel = reader.readString();
    }

	/**
	 * The ID of the ship in question
	 */
	public int getId() {
		return mId;
	}

	/**
	 * The intel on that ship, as human-readable text
	 */
	public String getIntel() {
		return mIntel;
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Obj #").append(mId).append(": ").append(mIntel);
	}
}