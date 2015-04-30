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

/**
 * Set the amount of coolant in a system.
 * @author dhleong
 */
public class EngSetCoolantPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final byte SUBTYPE = 0x00;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, SUBTYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngSetCoolantPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngSetCoolantPacket(reader);
			}
		});
	}

    private ShipSystem mSystem;
    private int mCoolant;

    /**
     * @param system The ShipSystem whose coolant level is to be set
     * @param coolant The amount of coolant to allocate
     */
    public EngSetCoolantPacket(ShipSystem system, int coolant) {
        super(ConnectionType.CLIENT, TYPE);

        if (system == null) {
        	throw new IllegalArgumentException("You must provide a system");
        }

        if (coolant < 0) {
        	throw new IllegalArgumentException(
        			"You cannot allocate a negative amount of coolant"
        	);
        }

        mSystem = system;
        mCoolant = coolant;
    }

    private EngSetCoolantPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
    	int subtype = reader.readInt();

    	if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
    	}

    	mSystem = ShipSystem.values()[reader.readInt()];
    	mCoolant = reader.readInt();
    }

    public ShipSystem getSystem() {
    	return mSystem;
    }

    public int getCoolant() {
    	return mCoolant;
    }

    @Override
	protected void writePayload(PacketWriter writer) {
    	writer	.writeInt(SUBTYPE)
    			.writeInt(mSystem.ordinal())
    			.writeInt(mCoolant);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSystem).append(" = ").append(mCoolant);
	}
}