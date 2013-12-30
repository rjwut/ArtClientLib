package net.dhleong.acl.net;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;

/**
 * Sent by the server when an object is destroyed.
 */
public class DestroyObjectPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xcc5a3e30;
    
    private final ObjectType mTargetType;
    private final int mTarget;

    public DestroyObjectPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        mTargetType = ObjectType.fromId(reader.readByte());
        mTarget = reader.readInt();
    }

    /**
     * The ObjectType of the destroyed object
     */
    public ObjectType getTargetType() {
        return mTargetType;
    }

    /**
     * The destroyed object's ID
     */
    public int getTarget() {
        return mTarget;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mTarget).append(" (").append(mTargetType).append(')');
	}
}