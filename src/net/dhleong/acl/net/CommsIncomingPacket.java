package net.dhleong.acl.net;

public class CommsIncomingPacket extends BaseArtemisPacket {

    public static final int TYPE = 0xD672C35F;
    
    private final int mPriority;

    private final String mFrom;
    private final String mMessage;

    public CommsIncomingPacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
        
        mPriority = PacketParser.getLendInt(bucket);
        int nameLen = PacketParser.getNameLengthBytes(bucket, 4);
        mFrom = PacketParser.getNameString(bucket, 8, nameLen);
        // the extra +2 is for the 2-byte null char
        int msgLen = PacketParser.getNameLengthBytes(bucket, nameLen + 10);
        mMessage = PacketParser.getNameString(bucket, nameLen + 14, msgLen)
                .replace('^', '\n');
    }
    
    public int getPriority() {
        return mPriority;
    }
    
    public String getFrom() {
        return mFrom;
    }
    
    public String getMessage() {
        return mMessage;
    }
}
