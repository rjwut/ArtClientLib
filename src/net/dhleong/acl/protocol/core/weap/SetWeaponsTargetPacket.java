package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Set the target for our weapons.
 * @author dhleong
 */
public class SetWeaponsTargetPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetWeaponsTargetPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetWeaponsTargetPacket(reader);
			}
		});
	}

	/**
	 * @param target The desired target (or null to release target lock)
	 */
    public SetWeaponsTargetPacket(ArtemisObject target) {
        super(TYPE_SET_TARGET, target == null ? 1 : target.getId());
    }

    private SetWeaponsTargetPacket(PacketReader reader) {
    	super(TYPE_SET_TARGET, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}