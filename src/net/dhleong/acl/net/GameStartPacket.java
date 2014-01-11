package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Sent by the server when the game starts.
 */
public class GameStartPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x00;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GameStartPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GameStartPacket(reader);
			}
		});
	}

    private final int mOffset;
    
    private GameStartPacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }

        reader.readUnknown("Unknown", 4);
        mOffset = reader.readInt();
    }

    /**
     * IDs starting offset...?
     */
    public int getOffset() {
        return mOffset;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Object ID offset = ").append(mOffset);
	}
}