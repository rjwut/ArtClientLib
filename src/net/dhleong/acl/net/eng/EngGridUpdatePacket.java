package net.dhleong.acl.net.eng;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.util.GridCoord;

/**
 * Updates damage to the various system grids on the
 *  ship, as well as (I think!) DamCon team status/location
 * @author dhleong
 */
public class EngGridUpdatePacket extends BaseArtemisPacket {
    public static final int TYPE = 0x77e9f3c;

    private static final List<GridDamage> EMPTY_DAMAGE = new ArrayList<GridDamage>();
    private static final List<DamconStatus> EMPTY_DAMCON = new ArrayList<DamconStatus>();
    private static final byte END_GRID_MARKER = (byte) 0xff;
    private static final byte END_DAMCON_MARKER = (byte) 0xfe;
    private static final int TEAM_NUMBER_OFFSET = 0x0a;
    private static final float PROGRESS_EPSILON = 0.001f;

    private List<GridDamage> mDamage = null;
    private List<DamconStatus> mDamconUpdates = null;

    public EngGridUpdatePacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE); // TODO don't save the byte[]?
        reader.skip(1);

        while (reader.peekByte() != END_GRID_MARKER) {
            GridCoord coord = GridCoord.getInstance(
                    reader.readByte(), 
                    reader.readByte(), 
                    reader.readByte()
            );
            float damage = reader.readFloat();
            
            // init here to limit unnecessary allocations
            if (mDamage == null) {
                mDamage = new ArrayList<GridDamage>();
            }

            mDamage.add(new GridDamage(coord, damage));
        }
        
        if (mDamage == null) {
            mDamage = EMPTY_DAMAGE;
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
            
            // init here to limit unnecessary allocations
            if (mDamconUpdates == null) {
                mDamconUpdates = new ArrayList<DamconStatus>();
            }

            mDamconUpdates.add(new DamconStatus(teamNumber, members, 
                    xGoal, yGoal, zGoal, x, y, z, progress));
        }
        
        if (mDamconUpdates == null) {
            mDamconUpdates = EMPTY_DAMCON;
        }
    }

    public List<GridDamage> getDamage() {
        return mDamage;
    }

    public List<DamconStatus> getDamcons() {
        return mDamconUpdates;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Damage updates:");

		if (mDamage.isEmpty()) {
			b.append("\n\tnone");
		} else {
			for (GridDamage damage : mDamage) {
				b.append("\n\t").append(damage);
			}
		}

		b.append("DAMCON status updates:");

		if (mDamconUpdates.isEmpty()) {
			b.append("\n\tnone");
		} else {
			for (DamconStatus status : mDamconUpdates) {
				b.append("\n\t").append(status);
			}
		}
	}
    
    public static final class GridDamage {
        public final GridCoord coord;
        public final float damage;
        
        private GridDamage(GridCoord coord, float damage) {
            this.coord = coord;
            this.damage = damage;
        }
        
        public GridDamage(int x, int y, int z, float damage) {
            coord = GridCoord.getInstance(x, y, z);
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
        
        public int getTeamNumber() {
            return teamNumber;
        }
        
        public int getMembers() {
            return members;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getZ() {
            return z;
        }
        
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