package net.dhleong.acl.net.setup;

import java.io.IOException;

import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.ShipType;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set the name, drive, and type of ship you want.
 * @author dhleong
 */
public class SetShipSettingsPacket extends ShipActionPacket {
	private DriveType mDrive;
	private int mHullId;
	private String mName;

	public SetShipSettingsPacket(DriveType drive, int hullId, String name) {
        super(TYPE_SHIP_SETUP);

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