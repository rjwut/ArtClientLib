package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;

public class BaseArtemisPacket implements ArtemisPacket {

    protected final byte[] mData;
    private final int mMode;
    private final int mType;
    protected final int mFlags;
    private final byte[] mIntBuffer = new byte[4];

    protected BaseArtemisPacket() {
        this(0, 0, 0, null);
    }

    public BaseArtemisPacket(int mode, int flags, int packetType, byte[] bucket) {
        mMode = mode;
        mFlags = flags;
        mType = packetType;
        mData = bucket;
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
        writeLendInt(os, mFlags);
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
        if (mData == null) return "(No Data)";
        return String.format("[%-16s~%s]", 
                String.format("%1d:%-5s:%s", mMode,
                    Integer.toHexString(mFlags),
                    Integer.toHexString(mType)),
                byteArrayToHexString(mData));
    }
    
    protected static String byteArrayToHexString(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            if (b <= 0x0F && b >= 0){
                buf.append('0');
            }
            final String hex = Integer.toHexString(b);
//            System.out.println("Read: " + hex + "(" + (b <= 0x0F));
            final int len = hex.length();
            if (len > 2 && (b > 0x0F || b < 0))
                buf.append(hex.substring(len-2));
            else if (len > 2)
                buf.append(hex.substring(len-1));
            else
                buf.append(hex);
        }
        return buf.toString();
    }
}
