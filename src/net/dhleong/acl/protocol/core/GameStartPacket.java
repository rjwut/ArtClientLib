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
 * Sent by the server when the game starts.
 */
public class GameStartPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x00;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
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
    
    private GameStartPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        reader.readUnknown("Unknown", 4);
        mOffset = reader.readInt();
    }

    public GameStartPacket(int offset) {
        super(ConnectionType.SERVER, TYPE);
    	mOffset = offset;
    }

    /**
     * IDs starting offset...?
     */
    public int getOffset() {
        return mOffset;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeInt(mOffset);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Object ID offset = ").append(mOffset);
	}
}