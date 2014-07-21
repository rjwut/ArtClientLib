package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

public class PlayerShipDamagePacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;

	public static void register(PacketFactoryRegistry registry) {
		PacketFactory factory = new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return PlayerShipDamagePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new PlayerShipDamagePacket(reader);
			}
		};
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE, factory);
	}

    public static final byte MSG_TYPE = 0x05;

    private byte[] unknown0;
    private byte[] unknown1;

    private PlayerShipDamagePacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }

        unknown0 = reader.readBytes(4);
        unknown1 = reader.readBytes(4);
    }

    public PlayerShipDamagePacket() {
        super(ConnectionType.SERVER, TYPE);
        unknown0 = new byte[] { 0, 0, 0, 0 };
        unknown1 = unknown0;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeBytes(unknown0).writeBytes(unknown1);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}