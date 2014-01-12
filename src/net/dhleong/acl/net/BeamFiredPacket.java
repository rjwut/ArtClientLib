package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

public class BeamFiredPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xb83fd2c4;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, new PacketFactory() {
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
	protected void appendPacketDetail(StringBuilder b) {
		b.append("from #").append(mOriginId).append(" to #").append(mTargetId);
	}
}