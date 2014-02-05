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
 * Sent by the server when the game ends.
 * @author rjwut
 */
public class GameOverPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x06;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GameOverPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GameOverPacket(reader);
			}
		});
	}

    private GameOverPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }
    }

    public GameOverPacket() {
    	super(ConnectionType.SERVER, TYPE);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE);
	}
}