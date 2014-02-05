package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.enums.BridgeStationStatus;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Indicates which stations are taken; received in response to a
 * SetStationPacket or SetShipPacket.
 * @author dhleong
 */
public class StationStatusPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x19c6e2d4;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
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

    public StationStatusPacket(int shipNumber, BridgeStationStatus[] statuses) {
    	super(ConnectionType.SERVER, TYPE);
    	if (shipNumber < 1 || shipNumber > Artemis.SHIP_COUNT) {
    		throw new IllegalArgumentException(
    				"Ship number must be between 1 and " + Artemis.SHIP_COUNT
    		);
    	}

    	if (statuses.length != BridgeStationStatus.values().length) {
    		throw new IllegalArgumentException(
    				"Must provide a status for each bridge station"
    		);
    	}

    	for (BridgeStationStatus status : statuses) {
    		if (status == null) {
        		throw new IllegalArgumentException(
        				"Must provide a status for each bridge station"
        		);
    		}
    	}

    	this.shipNumber = shipNumber;
    	this.statuses = statuses;
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
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(shipNumber);

		for (BridgeStationStatus status : statuses) {
			writer.writeByte((byte) status.ordinal());
		}
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