package net.dhleong.acl.net.setup;

import java.io.IOException;

import net.dhleong.acl.enums.BridgeStation;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.net.ShipActionPacket;

/**
 * "Take" or "untake" a bridge station.
 * @author dhleong
 */
public class SetStationPacket extends ShipActionPacket {
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

	@Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(TYPE_SET_STATION)
    			.writeInt(mStation.ordinal())
    			.writeInt(mSelected ? 1 : 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mStation).append(' ').append(mSelected ? "selected" : "deselected");
	}
}