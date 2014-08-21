package net.dhleong.acl.protocol.core.eng;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.util.GridCoord;

/**
 * Send a DAMCON team to a grid location.
 * @author dhleong
 */
public class EngSendDamconPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final byte SUBTYPE = 0x04;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, SUBTYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngSendDamconPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngSendDamconPacket(reader);
			}
		});
	}

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

    private EngSendDamconPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        int subtype = reader.readInt();

        if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
        }

        mTeamNumber = reader.readInt();
        mX = reader.readInt();
        mY = reader.readInt();
        mZ = reader.readInt();
    }

    public int getTeamNumber() {
    	return mTeamNumber;
    }

    public int getX() {
    	return mX;
    }

    public int getY() {
    	return mY;
    }

    public int getZ() {
    	return mZ;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer	.writeInt(SUBTYPE)
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