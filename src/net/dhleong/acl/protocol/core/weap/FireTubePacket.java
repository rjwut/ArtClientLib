package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Fire whatever's in the given tube.
 * @author dhleong
 */
public class FireTubePacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_FIRE_TUBE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return FireTubePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new FireTubePacket(reader);
			}
		});
	}

	/**
	 * @param tube The index of the tube to fire, [0 - Artemis.MAX_TUBES)
	 */
    public FireTubePacket(int tube) {
        super(TYPE_FIRE_TUBE, tube);

        if (tube < 0 || tube >= Artemis.MAX_TUBES) {
        	throw new IndexOutOfBoundsException(
        			"Invalid tube index: " + tube
        	);
        }
    }

    private FireTubePacket(PacketReader reader) {
    	super(TYPE_FIRE_TUBE, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}