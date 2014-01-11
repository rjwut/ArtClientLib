package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Sent by the server when the game ends.
 * @author rjwut
 */
public class GameOverPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x06;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
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

    private GameOverPacket(PacketReader reader) throws ArtemisPacketException {
    	super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}