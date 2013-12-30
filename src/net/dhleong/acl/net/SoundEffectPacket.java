package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;

/**
 * Indicates that the client should play the indicated sound file.
 * @author rjwut
 */
public class SoundEffectPacket extends BaseArtemisPacket {
	public static final int TYPE = 0xf754c8fe;
	public static final int MSG_TYPE = 0x03;

	private String mFilename;

	public SoundEffectPacket(PacketReader reader) throws ArtemisPacketException {
		super(ConnectionType.SERVER, TYPE);
		int subtype = reader.readInt();

		if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
		}

		mFilename = reader.readString();
	}

	/**
	 * Returns the path of the file to play, relative to the Artemis install
	 * directory.
	 */
	public String getFilename() {
		return mFilename;
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mFilename);
	}
}