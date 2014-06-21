package net.dhleong.acl.protocol.core.sci;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Scans the indicated target.
 */
public class SciScanPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SciScanPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SciScanPacket(reader);
			}
		});
	}

	/**
	 * @param target The target to scan
	 */
    public SciScanPacket(ArtemisObject target) {
        super(TYPE_SCI_SCAN, target != null ? target.getId() : 0);

        if (target == null) {
        	throw new IllegalArgumentException("You must provide a target");
        }
    }

    private SciScanPacket(PacketReader reader) {
        super(TYPE_SCI_SCAN, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}