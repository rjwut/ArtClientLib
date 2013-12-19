package net.dhleong.acl.net;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;

public class DestroyObjectPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xcc5a3e30;
    
    private final ObjectType mTargetType;
    private final int mTarget;

    public DestroyObjectPacket(byte[] bucket) {
        super(ConnectionType.SERVER, TYPE, null);
        mTargetType = ObjectType.fromId(bucket[0]);
        mTarget = PacketParser.getLendInt(bucket, 1);
    }
    
    public ObjectType getTargetType() {
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