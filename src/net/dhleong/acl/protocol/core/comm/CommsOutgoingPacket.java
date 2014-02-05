package net.dhleong.acl.protocol.core.comm;

import net.dhleong.acl.enums.CommsMessage;
import net.dhleong.acl.enums.CommsTargetType;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Sends a message to another entity.
 */
public class CommsOutgoingPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x574C4C4b;
    private static final int ARG_1_PLACEHOLDER = 0x00730078;
    private static final int ARG_2_PLACEHOLDER = 0x004f005e;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return CommsOutgoingPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new CommsOutgoingPacket(reader);
			}
		});
	}

    private CommsTargetType mTargetType;
    private int mTargetId;
    private CommsMessage mMsg;
    private int mArg;

    /**
     * Use this constructor for messages which do not have an argument;
     * otherwise, an IllegalArgumentException will be thrown.
     * @param target The message recipient
     * @param msg The message to be sent
     */
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
     * Use this constructor for messages which have an argument; otherwise, an
     * IllegalArgumentException will be thrown. At this writing only the
     * {@link net.dhleong.acl.enums.OtherMessage#GO_DEFEND}
     * message has an argument, which is the ID of the object to be defended.
     * @param target The message recipient
     * @param msg The message to be sent
     * @param arg The message argument
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

    private CommsOutgoingPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        mTargetType = CommsTargetType.values()[reader.readInt()];
        mTargetId = reader.readInt();
        mMsg = mTargetType.messageFromId(reader.readInt());
        mArg = reader.readInt();
        reader.skip(4);	// arg 2 placeholder
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

    	mTargetId = target.getId();
    	mMsg = msg;
    	mArg = arg;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
    	writer	.writeInt(mTargetType.ordinal())
    			.writeInt(mTargetId)
    			.writeInt(mMsg.getId())
    			.writeInt(mArg)
    			.writeInt(ARG_2_PLACEHOLDER);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("to obj #").append(mTargetId).append(": ").append(mMsg);

		if (mArg != ARG_1_PLACEHOLDER) {
			b.append(" object #").append(mArg);
		}
	}
}