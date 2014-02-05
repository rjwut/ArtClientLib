package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

public class BeamFiredPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xb83fd2c4;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return BeamFiredPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new BeamFiredPacket(reader);
			}
		});
	}

	private int mOriginId;
	private int mTargetId;

	private BeamFiredPacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		reader.readUnknown("Unknown 0", 4);
		reader.readUnknown("Unknown 1", 4);
		reader.readUnknown("Unknown 2", 4);
		reader.readUnknown("Unknown 3", 4);
		reader.readUnknown("Unknown 4", 4);
		reader.readUnknown("Unknown 5", 4);
		mOriginId = reader.readInt();
		mTargetId = reader.readInt();
		reader.readUnknown("Unknown 8", 4);
		reader.readUnknown("Unknown 9", 4);
		reader.readUnknown("Unknown 10", 4);
		reader.readUnknown("Unknown 11", 4);
	}

	public int getOriginId() {
		return mOriginId;
	}

	public int getTargetId() {
		return mTargetId;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("from #").append(mOriginId).append(" to #").append(mTargetId);
	}
}