package net.dhleong.acl.net.comms;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Received when an incoming COMMs message arrives.
 */
public class CommsIncomingPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xD672C35F;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return CommsIncomingPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new CommsIncomingPacket(reader);
			}
		});
	}

    private final int mPriority;
    private final String mFrom;
    private final String mMessage;

    private CommsIncomingPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        mPriority = reader.readInt();
        mFrom = reader.readString();
        mMessage = reader.readString().replace('^', '\n');
    }

    /**
     * Returns the message priority, with lower values having higher priority.
     * @return An integer between 0 and 8, inclusive
     */
    public int getPriority() {
        return mPriority;
    }

    /**
     * A String identifying the sender. This may not correspond to the name of
     * a game entity. For example, some messages from stations or friendly ships
     * have additional detail after the entity's name ("DS3 TSN Base"). Messages
     * in scripted scenarios can have any String for the sender.
     */
    public String getFrom() {
        return mFrom;
    }

    /**
     * The content of the message.
     */
    public String getMessage() {
        return mMessage;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("from ").append(mFrom).append(": ").append(mMessage);
	}
}