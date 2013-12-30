package net.dhleong.acl.net.setup;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.ShipType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.world.Artemis;

/**
 * Sent by the server to update the names, types and drives for each ship.
 * @author dhleong
 */
public class AllShipSettingsPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    public static final byte MSG_TYPE = 0x0f;
    
    public final DriveType[] drives;
    public final int[] shipTypes;
    public final String[] shipNames;

    public AllShipSettingsPacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        drives = new DriveType[Artemis.SHIP_COUNT];
        shipTypes = new int[Artemis.SHIP_COUNT];
        shipNames = new String[Artemis.SHIP_COUNT];
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
        }
        
        for (int i = 0; i < Artemis.SHIP_COUNT; i++) {
            drives[i] = DriveType.values()[reader.readInt()];
            shipTypes[i] = reader.readInt();
            reader.skip(4);	// RJW: UNKNOWN INT (always seems to be 1 0 0 0)
            				// TODO Figure out what this int is.
            shipNames[i] = reader.readString();
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
        for (int i = 0; i < Artemis.SHIP_COUNT; i++) {
        	int shipTypeVal = shipTypes[i];
        	ShipType shipType = ShipType.fromId(shipTypeVal);
        	b.append("\n\t").append(shipNames[i]).append(": ");

        	if (shipType != null) {
            	b.append(shipType.getHullName());
        	} else {
            	b.append(shipTypeVal);
        	}

        	b.append(" [").append(drives[i]).append(']');
        }
	}
}
