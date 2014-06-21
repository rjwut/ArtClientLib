package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * Toggles auto beams on/off.
 * @author rjwut
 */
public class ToggleAutoBeamsPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return ToggleAutoBeamsPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new ToggleAutoBeamsPacket(reader);
			}
		});
	}

	public ToggleAutoBeamsPacket() {
		super(TYPE_TOGGLE_AUTO_BEAMS, 0);
	}

	private ToggleAutoBeamsPacket(PacketReader reader) {
		super(TYPE_TOGGLE_AUTO_BEAMS, reader);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}