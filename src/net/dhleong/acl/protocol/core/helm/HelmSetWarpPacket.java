package net.dhleong.acl.protocol.core.helm;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Set warp speed.
 * @author dhleong
 */
public class HelmSetWarpPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return HelmSetWarpPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new HelmSetWarpPacket(reader);
			}
		});
	}

	/**
	 * @param warp Value between 0 (no warp) and 4 (max warp)
	 */
    public HelmSetWarpPacket(int warp) {
        super(TYPE_WARPSPEED, warp);

        if (warp < 0 || warp > Artemis.MAX_WARP) {
        	throw new IndexOutOfBoundsException("Warp speed out of range");
        }
    }

    private HelmSetWarpPacket(PacketReader reader) {
        super(TYPE_WARPSPEED, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}