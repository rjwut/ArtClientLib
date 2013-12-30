package net.dhleong.acl.net.eng;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.util.GridCoord;

/**
 * Send a DAMCON team to a grid location.
 * @author dhleong
 */
public class EngSendDamconPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final int SUBTYPE = 0x04;

    private int mTeamNumber;
    private int mX;
    private int mY;
    private int mZ;

    /**
     * Send the team to grid node at x,y,z.
     * @param teamNumber int [0, TEAMS) where TEAMS is probably 3
     * @param x Destination X-coordinate in the system grid
     * @param y Destination Y-coordinate in the system grid
     * @param z Destination Z-coordinate in the system grid
     */
    public EngSendDamconPacket(int teamNumber, int x, int y, int z) {
        super(ConnectionType.CLIENT, TYPE);

        if (teamNumber < 0) {
        	throw new IllegalArgumentException(
        			"DAMCON team number can't be less than 0"
        	);
        }

        mTeamNumber = teamNumber;
        mX = x;
        mY = y;
        mZ = z;
    }

    /**
     * @param teamNumber int [0, TEAMS) where TEAMS is probably 3
     * @param coord Destination coordinates in the system grid
     */
    public EngSendDamconPacket(int teamNumber, GridCoord coord) {
        this(teamNumber, coord.getX(), coord.getY(), coord.getZ());
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeInt(mTeamNumber)
    			.writeInt(mX)
    			.writeInt(mY)
    			.writeInt(mZ);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Team #").append(mTeamNumber).append(" to [")
		.append(mX).append(',').append(mY).append(',').append(mZ);
	}
}