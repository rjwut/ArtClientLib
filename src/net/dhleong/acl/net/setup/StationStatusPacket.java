package net.dhleong.acl.net.setup;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.enums.BridgeStationStatus;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Indicates which stations are taken. Not "pushed" out, but we get it in
 * response to a SetStationPacket or SetShipPacket.
 * @author dhleong
 */
public class StationStatusPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x19c6e2d4;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return StationStatusPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new StationStatusPacket(reader);
			}
		});
	}

    private final int shipNumber;
    private final BridgeStationStatus[] statuses;
    
    private StationStatusPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        shipNumber = reader.readInt();
        final BridgeStation[] stationValues = BridgeStation.values();
        final BridgeStationStatus[] statusValues = BridgeStationStatus.values();
        statuses = new BridgeStationStatus[stationValues.length];

        for (BridgeStation station : stationValues) {
        	statuses[station.ordinal()] = statusValues[reader.readByte()];
        }
    }

    /**
     * Returns the ship number whose stations this packet reports.
     */
    public int getShipNumber() {
    	return shipNumber;
    }

    /**
     * Get the status for a specific BridgeStation
     * @param station The desired BridgeStation
     * @return BridgeStationStatus The status of that station
     */
    public BridgeStationStatus get(BridgeStation station) {
        if (station == null) {
            throw new IllegalArgumentException("You must specify a station");
        }

        return statuses[station.ordinal()];
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Ship #").append(shipNumber);

		for (BridgeStation station : BridgeStation.values()) {
    		BridgeStationStatus status = statuses[station.ordinal()];
			b.append("\n\t").append(station).append(": ").append(status);
    	}
	}
}
