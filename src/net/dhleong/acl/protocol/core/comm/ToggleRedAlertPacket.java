package net.dhleong.acl.protocol.core.comm;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * Toggles red alert on and off.
 */
public class ToggleRedAlertPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return ToggleRedAlertPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new ToggleRedAlertPacket(reader);
			}
		});
	}

	public ToggleRedAlertPacket() {
        super(TYPE_TOGGLE_REDALERT, 0);
    }

	private ToggleRedAlertPacket(PacketReader reader) {
        super(TYPE_TOGGLE_REDALERT, reader);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}