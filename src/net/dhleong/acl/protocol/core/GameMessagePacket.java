package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

/**
 * "Toast" messages sent by the server.
 */
public class GameMessagePacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x0a;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
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

    private GameMessagePacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mMessage = reader.readString();
    }

    public GameMessagePacket(String message) {
        super(ConnectionType.SERVER, TYPE);
        mMessage = message;
    }

    /**
     * The contents of the "toast" message.
     */
    public String getMessage() {
        return mMessage;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeString(mMessage);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mMessage);
	}
}