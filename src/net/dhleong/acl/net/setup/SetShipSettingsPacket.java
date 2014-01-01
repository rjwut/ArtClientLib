package net.dhleong.acl.net.setup;

import java.io.IOException;

import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.ShipType;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set the name, type and drive of ship your station has selected.
 * @author dhleong
 */
public class SetShipSettingsPacket extends ShipActionPacket {
	private DriveType mDrive;
	private int mHullId;
	private String mName;

	/**
	 * Use this constructor if you wish to use the ShipType enum. This may be
	 * incompatible with changes to vesselData.xml.
	 * @param drive The desired type of drive
	 * @param type
	 * @param name The desired ship name
	 */
	public SetShipSettingsPacket(DriveType drive, ShipType type, String name) {
        super(TYPE_SHIP_SETUP);

        if (type == null) {
        	throw new IllegalArgumentException("You must specify a ship type");
        }

        if (!type.isPlayerShip()) {
        	throw new IllegalArgumentException("Can't select " + type);
        }

        init(drive, type.getId(), name);
	}

	/**
	 * Use this constructor if you wish to use a hull ID. This allows you to
	 * select ships from a modified vesselData.xml.
	 * @param drive The desired type of drive
	 * @param hullId The ID for the desired ship type
	 * @param name The desired ship name
	 */
	public SetShipSettingsPacket(DriveType drive, int hullId, String name) {
        super(TYPE_SHIP_SETUP);
        init(drive, hullId, name);
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
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(TYPE_SHIP_SETUP)
				.writeInt(mDrive.ordinal())
				.writeInt(mHullId)
				.writeInt(1) // ?
				.writeString(mName);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
    	ShipType shipType = ShipType.fromId(mHullId);
    	b.append(mName).append(": ");

    	if (shipType != null) {
        	b.append(shipType.getHullName());
    	} else {
        	b.append(mHullId);
    	}

    	b.append(" [").append(mDrive).append(']');
	}
}