package net.dhleong.acl.net.comms;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * Received when an incoming COMMs audio message arrives.
 * @author dhleong
 */
public class IncomingAudioPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xae88e058;

    public enum Mode {
    	PLAYING,	// server is playing the message
    	INCOMING	// message is available
    }

    private final int mId;
    private final String mTitle;
    private final String mFile;
    private final Mode mMode;

    public IncomingAudioPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        mId = reader.readInt();
        mMode = Mode.values()[reader.readInt() - 1];

        if (mMode == Mode.INCOMING) {
            mTitle = reader.readString();
            mFile = reader.readString();
        } else {
        	mTitle = null;
        	mFile = null;
        }
    }

    /**
     * The ID assigned to this audio message.
     */
    public int getAudioId() {
        return mId;
    }

    /**
     * The file name for this audio message. This will only be populated if
     * getAudioMode() returns IncomingAudioPacket.Mode.INCOMING; otherwise, it
     * returns null.
     */
    public String getFileName() {
        return mFile;
    }
    
    /**
     * The title for this audio message. This will only be populated if
     * getAudioMode() returns IncomingAudioPacket.Mode.INCOMING; otherwise, it
     * returns null.
     */
    public String getTitle() {
        return mTitle;
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.SERVER;
    }
    
    /**
     * Indicates whether this packet indicates that the message is available or
     * playing.
     */
    public Mode getAudioMode() {
        return mMode;
    }
    
    /**
     * Convenience to check if this is a new message.
     */
    public boolean isIncoming() {
        return mMode == Mode.INCOMING;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mId).append(' ').append(mMode);

		if (mMode == Mode.INCOMING) {
			b.append(": ").append(mTitle);
		}
	}
}