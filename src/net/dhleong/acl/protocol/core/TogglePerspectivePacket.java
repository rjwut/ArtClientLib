package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;

/**
 * Toggles between first- and third-person perspectives on the main screen.
 * @author rjwut
 */
public class TogglePerspectivePacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return TogglePerspectivePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new TogglePerspectivePacket(reader);
			}
		});
	}

	public TogglePerspectivePacket() {
		super(TYPE_TOGGLE_PERSPECTIVE, 0);
	}

	private TogglePerspectivePacket(PacketReader reader) {
		super(TYPE_TOGGLE_PERSPECTIVE, reader);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}