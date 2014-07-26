package net.dhleong.acl.protocol.core.comm;

import net.dhleong.acl.enums.CommsMessage;
import net.dhleong.acl.enums.CommsRecipientType;
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
    public static final int NO_ARG = 0x00730078;
    private static final int NO_ARG_2 = 0x004f005e;

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

    private CommsRecipientType mRecipientType;
    private int mRecipientId;
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

        init(target, msg, NO_ARG);
    }
    
    /**
     * Use this constructor for messages which have an argument; otherwise, an
     * IllegalArgumentException will be thrown. At this writing only the
     * {@link net.dhleong.acl.enums.OtherMessage#GO_DEFEND}
     * message has an argument, which is the ID of the object to be defended.
     */
    public CommsOutgoingPacket(ArtemisObject recipient, CommsMessage msg,
            int arg) {
        super(ConnectionType.CLIENT, TYPE);

        if (!msg.hasArgument()) {
        	throw new IllegalArgumentException(
        			"Message " + msg + " does not accept an argument"
        	);
        }

        init(recipient, msg, arg);
    }

    private CommsOutgoingPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        mRecipientType = CommsRecipientType.values()[reader.readInt()];
        mRecipientId = reader.readInt();
        mMsg = mRecipientType.messageFromId(reader.readInt());
        mArg = reader.readInt();
        reader.skip(4);	// arg 2 placeholder
    }

    private void init(ArtemisObject recipient, CommsMessage msg, int arg) {
        if (recipient == null) {
        	throw new IllegalArgumentException("You must provide a recipient");
        }

        if (msg == null) {
        	throw new IllegalArgumentException("You must provide a message");
        }

        mRecipientType = CommsRecipientType.fromObject(recipient);

    	if (mRecipientType == null) {
    		throw new IllegalArgumentException("Recipient cannot receive messages");
    	}

    	CommsRecipientType messageRecipientType = msg.getRecipientType();

    	if (mRecipientType != messageRecipientType) {
    		throw new IllegalArgumentException(
    				"Recipient type is " + mRecipientType +
    				", but message recipient type is " + messageRecipientType
    		);
    	}

    	mRecipientId = recipient.getId();
    	mMsg = msg;
    	mArg = arg;
    }

    public CommsRecipientType getRecipientType() {
    	return mRecipientType;
    }

    public int getRecipientId() {
    	return mRecipientId;
    }

    public CommsMessage getMessage() {
    	return mMsg;
    }

	public int getArgument() {
		return mArg;
	}

    @Override
	protected void writePayload(PacketWriter writer) {
    	writer	.writeInt(mRecipientType.ordinal())
    			.writeInt(mRecipientId)
    			.writeInt(mMsg.getId())
    			.writeInt(mArg)
    			.writeInt(NO_ARG_2);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("to obj #").append(mRecipientId).append(": ").append(mMsg);

		if (mArg != NO_ARG) {
			b.append(" object #").append(mArg);
		}
	}
}