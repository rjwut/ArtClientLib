package net.dhleong.acl.net.comms;

import java.io.IOException;

import net.dhleong.acl.enums.CommsMessage;
import net.dhleong.acl.enums.CommsTargetType;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Sends a message to another entity.
 */
public class CommsOutgoingPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x574C4C4b;
    private static final int ARG_1_PLACEHOLDER = 0x00730078;
    private static final int ARG_2_PLACEHOLDER = 0x004f005e;

    public CommsOutgoingPacket(ArtemisObject target, CommsMessage msg) {
        super(ConnectionType.CLIENT, TYPE);

        if (msg.hasArgument()) {
        	throw new IllegalArgumentException(
        			"Message " + msg + " requires an argument"
        	);
        }

        init(target, msg, ARG_1_PLACEHOLDER);
    }
    
    /**
     * Use this constructor when sending a
     * {@link net.dhleong.acl.enums.AllyMessage#GO_DEFEND}
     * message; arg should be the objId of the target to be defended.
     */
    public CommsOutgoingPacket(ArtemisObject target, CommsMessage msg,
            int arg) {
        super(ConnectionType.CLIENT, TYPE);

        if (!msg.hasArgument()) {
        	throw new IllegalArgumentException(
        			"Message " + msg + " does not accept an argument"
        	);
        }

        init(target, msg, arg);
    }

    private void init(ArtemisObject target, CommsMessage msg, int arg) {
        if (target == null) {
        	throw new IllegalArgumentException("You must provide a target");
        }

        if (msg == null) {
        	throw new IllegalArgumentException("You must provide a message");
        }

        mTargetType = CommsTargetType.fromObject(target);

    	if (mTargetType == null) {
    		throw new IllegalArgumentException("Target cannot receive messages");
    	}

    	CommsTargetType messageTargetType = msg.getTargetType();

    	if (mTargetType != messageTargetType) {
    		throw new IllegalArgumentException(
    				"Target type is " + mTargetType +
    				", but message target type is " + messageTargetType
    		);
    	}

    	mTarget = target;
    	mMsg = msg;
    	mArg = arg;
    }

    private CommsTargetType mTargetType;
    private ArtemisObject mTarget;
    private CommsMessage mMsg;
    private int mArg;

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(mTargetType.ordinal())
    			.writeInt(mTarget.getId())
    			.writeInt(mMsg.getId())
    			.writeInt(mArg)
    			.writeInt(ARG_2_PLACEHOLDER);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("to obj #").append(mTarget).append(": ").append(mMsg);

		if (mArg != ARG_1_PLACEHOLDER) {
			b.append(" object #").append(mArg);
		}
	}
}