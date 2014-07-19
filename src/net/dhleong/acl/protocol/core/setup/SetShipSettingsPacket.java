package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.VesselAttribute;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.protocol.core.ShipActionPacket;
import net.dhleong.acl.vesseldata.Vessel;
import net.dhleong.acl.vesseldata.VesselData;


/**
 * Set the name, type and drive of ship your console has selected.
 * @author dhleong
 */
public class SetShipSettingsPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_SHIP_SETUP,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetShipSettingsPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetShipSettingsPacket(reader);
			}
		});
	}

	private DriveType mDrive;
	private int mHullId;
	private String mName;

	/**
	 * Use this constructor if you wish to use a Vessel instance from the
	 * VesselData class.
	 */
	public SetShipSettingsPacket(DriveType drive, Vessel vessel, String name) {
        super(TYPE_SHIP_SETUP);

        if (vessel == null) {
        	throw new IllegalArgumentException("You must specify a Vessel");
        }

        if (!vessel.is(VesselAttribute.PLAYER)) {
        	throw new IllegalArgumentException("Must select a player vessel");
        }

        init(drive, vessel.getId(), name);
	}

	/**
	 * Use this constructor if you wish to use a hull ID.
	 */
	public SetShipSettingsPacket(DriveType drive, int hullId, String name) {
        super(TYPE_SHIP_SETUP);
        init(drive, hullId, name);
    }

	private SetShipSettingsPacket(PacketReader reader) {
        super(TYPE_SHIP_SETUP);
		int subtype = reader.readInt();

		if (subtype != TYPE_SHIP_SETUP) {
        	throw new UnexpectedTypeException(subtype, TYPE_SHIP_SETUP);
		}

		mDrive = DriveType.values()[reader.readInt()];
		mHullId = reader.readInt();
		reader.skip(4); // RJW: UNKNOWN INT (always seems to be 1 0 0 0)
		mName = reader.readString();
	}

	private void init(DriveType drive, int hullId, String name) {
        if (drive == null) {
        	throw new IllegalArgumentException("You must specify a drive type");
        }

        if (name == null) {
        	throw new IllegalArgumentException("You must specify a name");
        }

        mDrive = drive;
        mHullId = hullId;
        mName = name;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer	.writeInt(TYPE_SHIP_SETUP)
				.writeInt(mDrive.ordinal())
				.writeInt(mHullId)
				.writeInt(1) // RJW: UNKNOWN INT (always seems to be 1 0 0 0)
				.writeString(mName);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		Vessel vessel = VesselData.get().getVessel(mHullId);
    	b	.append(mName).append(": ")
    		.append(vessel != null ? vessel.getName() : "UNKNOWN TYPE")
    		.append(" [").append(mDrive).append(']');
	}
}