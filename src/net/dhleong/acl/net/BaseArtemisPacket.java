package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.util.TextUtil;

public class BaseArtemisPacket implements ArtemisPacket {
    protected final byte[] mData;
    protected final ConnectionType mConnectionType;
    protected final int mType;
    private final byte[] mIntBuffer = new byte[4];

    public BaseArtemisPacket(ConnectionType connectionType) {
    	this(connectionType, 0, null);
    }

    public BaseArtemisPacket(ConnectionType connectionType, int packetType, byte[] bucket) {
        mConnectionType = connectionType;
        mType = packetType;
        mData = bucket;
    }
    
    public byte[] getData() {
        return mData;
    }

    @Override
    public ConnectionType getConnectionType() {
        return mConnectionType;
    }
    
    @Override
    public int getType() {
        return mType;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        writeLendInt(os, ArtemisPacket.HEADER);
        writeLendInt(os, 24 + mData.length);
        writeLendInt(os, mConnectionType.toInt());
        writeLendInt(os, 0);
        writeLendInt(os, 4 + mData.length);
        writeLendInt(os, mType);
        os.write(mData);
        return true;
    }

    private void writeLendInt(OutputStream os, int value) throws IOException {
        PacketParser.putLendInt(value, mIntBuffer);
        os.write(mIntBuffer);
    }

    @Override
    public String toString() {
    	StringBuilder b = new StringBuilder();
    	b.append('[').append(mConnectionType).append(':')
    	.append(getClass().getSimpleName()).append('|')
    	.append(TextUtil.intToHex(mType))
    	.append("] ")
    	.append(TextUtil.byteArrayToHexString(mData));
    	return b.toString();
    }
}
