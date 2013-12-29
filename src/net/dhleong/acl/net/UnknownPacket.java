package net.dhleong.acl.net;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.util.TextUtil;

public class UnknownPacket extends BaseArtemisPacket {
    protected final byte[] mPayload;

    public UnknownPacket(ConnectionType connectionType, int packetType, byte[] payload) {
    	super(connectionType, packetType);
    	mPayload = payload;
    }

    public byte[] getPayload() {
    	return mPayload;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("0x").append(TextUtil.intToHex(getType())).append(' ')
			.append(TextUtil.byteArrayToHexString(mPayload));
	}
}