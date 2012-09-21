package net.dhleong.acl.net;


public class SystemInfoPacket extends BaseArtemisPacket {
    
    public static final byte ACTION_CREATE = (byte) 0xff;

    public static final int TYPE = 0x80803df9;
    
    public final boolean isEmpty;

    private final byte mTargetType;
    private final int mTarget;
    private final byte mAction;
    
    public SystemInfoPacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
        
        // only gets the first one, but
        //  they seem to be grouped
        mTargetType = bucket[0];

        isEmpty = mTargetType == 0;
        
        if (!isEmpty) {
            mTarget = PacketParser.getLendInt(bucket, 1);
            mAction = bucket[5];
        } else {
            mTarget = 0;
            mAction = 0;
        }
    }

    public int getTarget() {
        return mTarget;
    }

    public byte getTargetType() {
        return mTargetType;
    }

    public byte getAction() {
        return mAction;
    }

}
