package net.dhleong.acl.protocol.core.eng;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * Set whether DAMCON teams should be autonomous or not.
 * @author dhleong
 */
public class EngSetAutoDamconPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_AUTO_DAMCON,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngSetAutoDamconPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngSetAutoDamconPacket(reader);
			}
		});
	}

	/**
	 * @param useAutonomous Whether DAMCON teams should be autonomous
	 */
    public EngSetAutoDamconPacket(boolean useAutonomous) {
        super(TYPE_AUTO_DAMCON, useAutonomous ? 1 : 0);
    }

    private EngSetAutoDamconPacket(PacketReader reader) {
        super(TYPE_AUTO_DAMCON, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg == 1 ? "on" : "off");
	}
}