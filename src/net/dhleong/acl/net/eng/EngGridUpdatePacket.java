package net.dhleong.acl.net.eng;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ObjectParser;

/**
 * Updates damage to the various system grids on the
 *  ship, as well as (I think!) DamCon team status/location
 * @author dhleong
 */
public class EngGridUpdatePacket extends BaseArtemisPacket {
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
    }
    
    /**
     * 
     * @author dhleong
     *
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
            
            if (other.progress < 0.001 && progress > 0) {
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

    public static final int TYPE = 0x77e9f3c;
    private static final List<GridDamage> EMPTY_DAMAGE = new ArrayList<GridDamage>();
    private static final List<DamconStatus> EMPTY_DAMCON = new ArrayList<DamconStatus>();
    
    private List<GridDamage> mDamage = null;
    private List<DamconStatus> mDamconUpdates = null;

    public EngGridUpdatePacket(byte[] bucket) {
        super(ConnectionType.SERVER, TYPE, bucket); // TODO don't save the byte[]?
        ObjectParser p =  new ObjectParser(bucket, 1);

        while (p.peekByte() != (byte)0xff) {
            GridCoord coord = GridCoord.getInstance(
                    p.readByte(), 
                    p.readByte(), 
                    p.readByte());
            float damage = p.readFloat();
            
            // init here to limit unnecessary allocations
            if (mDamage == null)
                mDamage = new ArrayList<GridDamage>();
            
            mDamage.add(new GridDamage(coord, damage));
        }
        
        if (mDamage == null)
            mDamage = EMPTY_DAMAGE;
        
        p.readByte(); // read the 0xff byte
        while (p.peekByte() != (byte)0xfe) {
            byte teamIndicator = p.readByte();
            int teamNumber = teamIndicator - 0x0a;
            
            int xGoal = p.readInt();
            int x = p.readInt();
            
            int yGoal = p.readInt();
            int y = p.readInt();

            int zGoal = p.readInt();
            int z = p.readInt();            
            
            float progress = p.readFloat();
            
            int members = p.readInt();
            
            // init here to limit unnecessary allocations
            if (mDamconUpdates == null)
                mDamconUpdates = new ArrayList<DamconStatus>();
            
            mDamconUpdates.add(new DamconStatus(teamNumber, members, 
                    xGoal, yGoal, zGoal, x, y, z, progress));
        }
        
        if (mDamconUpdates == null)
            mDamconUpdates = EMPTY_DAMCON;
    }

    public void debugPrint() {
        System.out.println("** DAMAGE");
        for (GridDamage d : mDamage) {
            System.out.println("--> @ " + d.coord + ": " + d.damage);
        }
        System.out.println("** DAMCON");
        for (DamconStatus d : mDamconUpdates) {
            System.out.println("--> " + d);
        }
    }

    public List<GridDamage> getDamage() {
        return mDamage;
    }

    public List<DamconStatus> getDamcons() {
        return mDamconUpdates;
    }
}
