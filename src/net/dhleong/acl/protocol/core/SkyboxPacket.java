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

public class SkyboxPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x09;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SkyboxPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SkyboxPacket(reader);
			}
		});
	}

	private int mSkyboxId;

	private SkyboxPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mSkyboxId = reader.readInt();
	}

    public SkyboxPacket(int offset) {
        super(ConnectionType.SERVER, TYPE);
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeInt(mSkyboxId);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSkyboxId);
	}
}