package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * "Take" or "untake" a bridge station.
 * @author dhleong
 */
public class SetStationPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetStationPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetStationPacket(reader);
			}
		});
	}

	private BridgeStation mStation;
	private boolean mSelected;

	/**
	 * @param station The BridgeStation being updated
	 * @param selected Whether the player is taking this station or not
	 */
	public SetStationPacket(BridgeStation station, boolean selected) {
        super(TYPE_SET_STATION);

        if (station == null) {
        	throw new IllegalArgumentException("You must specify a station");
        }

        mStation = station;
        mSelected = selected;
    }

	private SetStationPacket(PacketReader reader) {
        super(TYPE_SET_STATION);
		int subtype = reader.readInt();

		if (subtype != TYPE_SET_STATION) {
        	throw new UnexpectedTypeException(subtype, TYPE_SET_STATION);
		}

		mStation = BridgeStation.values()[reader.readInt()];
		mSelected = reader.readInt() == 1;
	}

	@Override
    public void writePayload(PacketWriter writer) {
    	writer	.writeInt(TYPE_SET_STATION)
    			.writeInt(mStation.ordinal())
    			.writeInt(mSelected ? 1 : 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mStation).append(' ').append(mSelected ? "selected" : "deselected");
	}
}