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
 * Sets the science officer's current target.
 */
public class SciSelectPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_SCI_SELECT,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SciSelectPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SciSelectPacket(reader);
			}
		});
	}

	/**
	 * @param target The target to select (or null to clear the taregt)
	 */
    public SciSelectPacket(ArtemisObject target) {
        super(TYPE_SCI_SELECT, target == null ? 1 : target.getId());
    }

    public SciSelectPacket(PacketReader reader) {
    	super(TYPE_SCI_SELECT, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}