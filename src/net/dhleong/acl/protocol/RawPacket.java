package net.dhleong.acl.protocol;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.util.TextUtil;

/**
 * Any packet that ArtClientLib has not parsed. This may be because it was not
 * recognized by any registered protocol, or because there are no registered
 * packet listeners that are interested in it.
 * @author rjwut
 */
public abstract class RawPacket extends BaseArtemisPacket {
    protected final byte[] mPayload;

    /**
     * @param connectionType The type of connection over which this packet was
     * 		received
     * @param packetType The packet type value specified in the preamble
     * @param payload The bytes from the payload (byte offset 24 onward)
     */
    protected RawPacket(ConnectionType connectionType, int packetType,
    		byte[] payload) {
    	super(connectionType, packetType);
    	mPayload = payload;
    }

    /**
     * Returns the payload for this packet.
     */
    public byte[] getPayload() {
    	return mPayload;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
    	writer.writeBytes(mPayload);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("0x").append(TextUtil.intToHex(getType())).append(' ')
			.append(TextUtil.byteArrayToHexString(mPayload));
	}
}