package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * Sets the frequency at which to tune the beams.
 */
public class SetBeamFreqPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetBeamFreqPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetBeamFreqPacket(reader);
			}
		});
	}

	/**
	 * @param frequency The desired beam frequency
	 */
    public SetBeamFreqPacket(BeamFrequency frequency) {
        super(TYPE_SET_BEAMFREQ, frequency != null ? frequency.ordinal(): -1);

        if (frequency == null) {
        	throw new IllegalArgumentException(
        			"You must specify a beam frequency"
        	);
        }
    }

    private SetBeamFreqPacket(PacketReader reader) {
    	super(TYPE_SET_BEAMFREQ, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(BeamFrequency.values()[mArg]);
	}
}