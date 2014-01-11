package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * "Toast" messages sent by the server.
 */
public class GameMessagePacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x0a;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GameMessagePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GameMessagePacket(reader);
			}
		});
	}

    private final String mMessage;

    private GameMessagePacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }

        mMessage = reader.readString();
    }

    /**
     * The contents of the "toast" message.
     */
    public String getMessage() {
        return mMessage;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mMessage);
	}
}