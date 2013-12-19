package net.dhleong.acl.net.setup;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.enums.BridgeStationStatus;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.util.ObjectParser;

/**
 * Indicates which stations are taken. Not
 *  "pushed" out, but we get it in response
 *  to a SetStationPacket or SetShipPacket.
 *  
 * @author dhleong
 *
 */
public class StationStatusPacket implements ArtemisPacket {
    public static final int TYPE = 0x19c6e2d4;
    
    /**
     * The number of the ship we describe, 
     *  starting from 1 (Artemis)
     */
    public final int shipNumber;
    public final BridgeStationStatus[] statuses;
    
    public StationStatusPacket(byte[] bucket) {
        ObjectParser p = new ObjectParser(bucket, 0);
        shipNumber = p.readInt();
        final BridgeStation[] stationValues = BridgeStation.values();
        final BridgeStationStatus[] statusValues = BridgeStationStatus.values();
        statuses = new BridgeStationStatus[stationValues.length];

        for (BridgeStation station : stationValues) {
        	statuses[station.ordinal()] = statusValues[p.readByte()];
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.SERVER;
    }
    
    /**
     * Get the status for a specific station
     * @param station
     * @return
     */
    public BridgeStationStatus get(StationType stationType) {
        if (stationType == null) {
            throw new IllegalArgumentException("Null station");
        }

        return statuses[stationType.ordinal()];
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        // nothing to ever write
        return false;
    }

    @Override
    public int getType() {
        return TYPE;
    }
    
    @Override
    public String toString() {
    	StringBuilder b = new StringBuilder();
    	b.append("[Ship #").append(shipNumber);

    	for (BridgeStation station : BridgeStation.values()) {
    		BridgeStationStatus status = statuses[station.ordinal()];

    		if (status != BridgeStationStatus.AVAILABLE) {
    			b.append(' ').append(station).append('=').append(status);
    		}
    	}

    	b.append(']');
    	return b.toString();
    }
}
