package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

/**
 * Provides intel on another vessel, typically as the result of a level 2 scan.
 * @author rjwut
 */
public class IntelPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xee665279;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return IntelPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new IntelPacket(reader);
			}
		});
	}

	private final int mId;
	private final String mIntel;

	private IntelPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
    	mId = reader.readInt();
    	reader.readUnknown("Unknown", 1);
        mIntel = reader.readString();
    }

	/**
	 * The ID of the ship in question
	 */
	public int getId() {
		return mId;
	}

	/**
	 * The intel on that ship, as human-readable text
	 */
	public String getIntel() {
		return mIntel;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(mId).writeByte((byte) 3).writeString(mIntel);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Obj #").append(mId).append(": ").append(mIntel);
	}
}