package net.dhleong.acl.net.setup;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.ShipType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;
import net.dhleong.acl.world.Artemis;

/**
 * Sent by the server to update the names, types and drives for each ship.
 * @author dhleong
 */
public class AllShipSettingsPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x0f;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return AllShipSettingsPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new AllShipSettingsPacket(reader);
			}
		});
	}

    private final DriveType[] drives;
    private final int[] shipTypes;
    private final String[] shipNames;

    private AllShipSettingsPacket(PacketReader reader) throws ArtemisPacketException {
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

    public DriveType getDrive(int shipIndex) {
    	return drives[shipIndex];
    }

    public int getShipType(int shipIndex) {
    	return shipTypes[shipIndex];
    }

    public String getShipName(int shipIndex) {
    	return shipNames[shipIndex];
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
