package net.dhleong.acl.net;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.util.TextUtil;

/**
 * Any packet received that isn't of a type recognized by ArtClientLib will be
 * returned as this class. If you disable packet parsing (by calling
 * ThreadedArtemisNetworkInterface.setParsePackets(false)), all packets will be
 * of this type. This is mainly intended as a debugging mechanism.
 * @author rjwut
 */
public class UnknownPacket extends BaseArtemisPacket {
    protected final byte[] mPayload;

    /**
     * @param connectionType The type of connection over which this packet was
     * 		received
     * @param packetType The packet type value specified in the preamble
     * @param payload The bytes from the payload (byte offset 24 onward)
     */
    public UnknownPacket(ConnectionType connectionType, int packetType,
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
    public void write(PacketWriter writer) throws IOException {
    	writer.start(getType());
    	writer.writeBytes(mPayload);
    	writer.flush();
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("0x").append(TextUtil.intToHex(getType())).append(' ')
			.append(TextUtil.byteArrayToHexString(mPayload));
	}
}