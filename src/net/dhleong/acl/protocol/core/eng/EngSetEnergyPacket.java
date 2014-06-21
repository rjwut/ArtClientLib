package net.dhleong.acl.protocol.core.eng;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.world.Artemis;

/**
 * Sets the amount of energy allocated to a system.
 */
public class EngSetEnergyPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x0351A5AC;
    private static final int SUBTYPE = 0x04;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngSetEnergyPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngSetEnergyPacket(reader);
			}
		});
	}

    private ShipSystem mSystem;
    private float mValue;

    /**
     * Sets the given system's energy allocation.
     * @param system The ShipSystem whose energy allocation is to be set
     * @param value A number between 0 (no energy) and 1 (max possible energy),
     * 		inclusive. A value of 0.333333... is the default allocation level.
     */
    public EngSetEnergyPacket(ShipSystem system, float value) {
        super(ConnectionType.CLIENT, TYPE);

        if (system == null) {
        	throw new IllegalArgumentException("You must provide a system");
        }

        if (value < 0) {
        	throw new IllegalArgumentException(
        			"You cannot allocate a negative amount of energy"
        	);
        }

        if (value > 1) {
        	throw new IllegalArgumentException(
        			"You cannot allocate more than 300% energy"
        	);
        }
    }

    /**
     * Sets the given system's energy allocation.
     * @param system The ShipSystem whose energy allocation is to be set
     * @param percentage A number between 0 (no energy) and 300 (max possible
     * 		energy), inclusive. This value corresponds to the energy allocation
     * 		percentage as seen in the UI. A value of 100 (100%) is the default
     * 		allocation level.
     */
    public EngSetEnergyPacket(ShipSystem system, int percentage) {
        this(system, percentage / (float) Artemis.MAX_ENERGY_ALLOCATION_PERCENT);
    }

    private EngSetEnergyPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
    	int subtype = reader.readInt();

    	if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
    	}

    	mValue = reader.readFloat();
    	mSystem = ShipSystem.values()[reader.readInt()];
    }

	@Override
	protected void writePayload(PacketWriter writer) {
    	writer	.writeInt(SUBTYPE)
				.writeFloat(mValue)
				.writeInt(mSystem.ordinal());
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSystem).append(" = ").append(mValue * 300).append('%');
	}
}