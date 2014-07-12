package net.dhleong.acl.protocol.core.eng;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.util.GridCoord;

/**
 * Updates damage to the various system grids on the ship, as well as DamCon
 * team status/location.
 * @author dhleong
 */
public class EngGridUpdatePacket extends BaseArtemisPacket {
    private static final int TYPE = 0x77e9f3c;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngGridUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngGridUpdatePacket(reader);
			}
		});
	}

    private static final byte END_GRID_MARKER = (byte) 0xff;
    private static final byte END_DAMCON_MARKER = (byte) 0xfe;
    private static final int TEAM_NUMBER_OFFSET = 0x0a;
    private static final float PROGRESS_EPSILON = 0.001f;

    private List<GridDamage> mDamage = new ArrayList<GridDamage>();
    private List<DamconStatus> mDamconUpdates = new ArrayList<DamconStatus>();

    /**
     * Creates a new EngGridUpdatePacket with no updates. Use the
     * addDamageUpdate() and addDamconUpdate() methods to add update information
     * to this packet.
     */
    public EngGridUpdatePacket() {
        super(ConnectionType.SERVER, TYPE);
    }

    private EngGridUpdatePacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        reader.readUnknown("Unknown", 1);

        while (reader.peekByte() != END_GRID_MARKER) {
            GridCoord coord = GridCoord.getInstance(
                    reader.readByte(), 
                    reader.readByte(), 
                    reader.readByte()
            );
            float damage = reader.readFloat();
            mDamage.add(new GridDamage(coord, damage));
        }

        reader.skip(1); // read the 0xff byte

        while (reader.peekByte() != END_DAMCON_MARKER) {
            byte teamIndicator = reader.readByte();
            int teamNumber = teamIndicator - TEAM_NUMBER_OFFSET;
            int xGoal = reader.readInt();
            int x = reader.readInt();
            int yGoal = reader.readInt();
            int y = reader.readInt();
            int zGoal = reader.readInt();
            int z = reader.readInt();
            float progress = reader.readFloat();
            int members = reader.readInt();
            addDamconUpdate(teamNumber, members, xGoal, yGoal, zGoal, x, y, z,
            		progress);
        }

        reader.skip(1); // read the 0xfe byte
    }

    /**
     * Adds a damage update to this packet.
     */
    public void addDamageUpdate(int x, int y, int z, float damage) {
    	mDamage.add(new GridDamage(GridCoord.getInstance(x, y, z), damage));
    }

    /**
     * Adds a DAMCON team update to this packet.
     */
    public void addDamconUpdate(int teamNumber, int members, int xGoal,
    		int yGoal, int zGoal, int x, int y, int z, float progress) {
    	mDamconUpdates.add(new DamconStatus(teamNumber, members, xGoal, yGoal,
    			zGoal, x, y, z, progress));
    }

    /**
     * Returns a List of GridDamage objects that describe the damage data
     * encoded in this packet.
     */
    public List<GridDamage> getDamage() {
        return mDamage;
    }

    /**
     * Returns a List of DamconStatus objects that provide the DAMCON team
     * updates encoded in this packet.
     */
    public List<DamconStatus> getDamcons() {
        return mDamconUpdates;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeByte((byte) 1);

		for (GridDamage damage : mDamage) {
			GridCoord coord = damage.coord;
			writer	.writeByte((byte) coord.getX())
					.writeByte((byte) coord.getY())
					.writeByte((byte) coord.getZ())
					.writeFloat(damage.damage);
		}

		writer.writeByte(END_GRID_MARKER);

		for (DamconStatus update : mDamconUpdates) {
			writer	.writeByte((byte) (update.teamNumber + TEAM_NUMBER_OFFSET))
					.writeInt(update.xGoal)
					.writeInt(update.x)
					.writeInt(update.yGoal)
					.writeInt(update.y)
					.writeInt(update.zGoal)
					.writeInt(update.z)
					.writeFloat(update.progress)
					.writeInt(update.members);
		}

		writer.writeByte(END_DAMCON_MARKER);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("\nDamage updates:");

		if (mDamage.isEmpty()) {
			b.append("\n\tnone");
		} else {
			for (GridDamage damage : mDamage) {
				b.append("\n\t").append(damage);
			}
		}

		b.append("\nDAMCON status updates:");

		if (mDamconUpdates.isEmpty()) {
			b.append("\n\tnone");
		} else {
			for (DamconStatus status : mDamconUpdates) {
				b.append("\n\t").append(status);
			}
		}
	}


	/**
	 * Updates the level of damage to a node in the system grid.
     * @author dhleong
	 */
    public static final class GridDamage {
        public final GridCoord coord;
        public final float damage;

        private GridDamage(GridCoord coord, float damage) {
            this.coord = coord;
            this.damage = damage;
        }

        @Override
        public boolean equals(Object other) {
        	if (this == other) {
        		return true;
        	}

        	if (!(other instanceof GridDamage)) {
        		return false;
        	}

            GridDamage cast = (GridDamage) other;
            return coord.equals(cast.coord);
        }

		@Override
		public int hashCode() {
			return coord.hashCode();
		}

		@Override
		public String toString() {
			return coord + ": " + damage;
		}
    }
    
    /**
     * Updates the status of a DAMCON team.
     * @author dhleong
     */
    public static final class DamconStatus {
        int teamNumber, members;
        int xGoal, yGoal, zGoal;
        int x, y, z;
        float progress;
        
        public DamconStatus(int teamNumber, int members, int xGoal,
                int yGoal, int zGoal, int x, int y, int z, float progress) {
            this.teamNumber = teamNumber;
            this.members = members;
            this.xGoal = xGoal;
            this.yGoal = yGoal;
            this.zGoal = zGoal;
            this.x = x;
            this.y = y;
            this.z = z;
            this.progress = progress;
        }

        /**
         * The number assigned to this DAMCON team.
         */
        public int getTeamNumber() {
            return teamNumber;
        }

        /**
         * The number of people in this DAMCON team that are still alive.
         */
        public int getMembers() {
            return members;
        }

        /**
         * The grid location of this DAMCON team on the X-axis.
         */
        public int getX() {
            return x;
        }
        
        /**
         * The grid location of this DAMCON team on the Y-axis.
         */
        public int getY() {
            return y;
        }
        
        /**
         * The grid location of this DAMCON team on the Z-axis.
         */
        public int getZ() {
            return z;
        }

        /**
         * The DAMCON team's progress towards their destination.
         */
        public float getProgress() {
            return progress;
        }

        public void updateFrom(DamconStatus other) {
            this.members = other.members;
            
            if (other.progress < PROGRESS_EPSILON && progress > 0) {
                // we've made it to our goal!
                this.x = xGoal;
                this.y = yGoal;
                this.z = zGoal;
            } else {
                this.x = other.x;
                this.y = other.y;
                this.z = other.z;
            }
            
            this.xGoal = other.xGoal;
            this.yGoal = other.yGoal;
            this.zGoal = other.zGoal;
            this.progress = other.progress;
        }
     
        @Override
        public String toString() {
            return String.format("dc#%d(%d)@[%d->%d|%d->%d|%d->%d]==%.3f",
                    teamNumber, members, 
                    x, y, z, xGoal, yGoal, zGoal, 
                    progress);
        }
    }
}