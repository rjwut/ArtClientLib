package net.dhleong.acl.net.setup;

import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.enums.BridgeStationStatus;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * Indicates which stations are taken. Not "pushed" out, but we get it in
 * response to a SetStationPacket or SetShipPacket.
 * @author dhleong
 */
public class StationStatusPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x19c6e2d4;
    
    private final int shipNumber;
    private final BridgeStationStatus[] statuses;
    
    public StationStatusPacket(PacketReader reader) {
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
