package net.dhleong.acl.net;

public class DestroyObjectPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xcc5a3e30;
    
    private final byte mTargetType;
    private final int mTarget;

    public DestroyObjectPacket(byte[] bucket) {
        super(0x01, TYPE, null);
        mTargetType = bucket[0];
        mTarget = PacketParser.getLendInt(bucket, 1);
    }
    
    public byte getTargetType() {
        return mTargetType;
    }
    
    public int getTarget() {
        return mTarget;
    }
    
    @Override
    public String toString() {
        return String.format("[DESTROY: %d:%s|%s]",
                mTargetType,
                Integer.toHexString(mTarget),
                mTarget);
    }
}
