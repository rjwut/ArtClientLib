package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.TextUtil;

public class BaseArtemisPacket implements ArtemisPacket {

    protected final byte[] mData;
    protected final int mMode;
    protected final int mType;
    private final byte[] mIntBuffer = new byte[4];

    protected BaseArtemisPacket() {
        this(0, 0, null);
    }

    public BaseArtemisPacket(int mode, int packetType, byte[] bucket) {
        mMode = mode;
        mType = packetType;
        mData = bucket;
    }
    
    public byte[] getData() {
        return mData;
    }

    @Override
    public long getMode() {
        return mMode;
    }
    
    @Override
    public int getType() {
        return mType;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        writeLendInt(os, 0xdeadbeef);
        writeLendInt(os, 24 + mData.length);
        writeLendInt(os, mMode);
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
    	b.append('[').append(mMode == 1 ? "SERVER" : "CLIENT").append(':')
    	.append(getClass().getSimpleName()).append('|')
    	.append(TextUtil.intToHex(mType))
    	.append("] ")
    	.append(TextUtil.byteArrayToHexString(mData));
    	return b.toString();
    }
}
