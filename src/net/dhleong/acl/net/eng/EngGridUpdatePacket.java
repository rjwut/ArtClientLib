package net.dhleong.acl.net.eng;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.util.GridCoord;

/**
 * Updates damage to the various system grids on the
 *  ship, as well as (I think!) DamCon team status/location
 * @author dhleong
 *
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
            if (other == null || !(other instanceof GridDamage))
                return false;
            
            GridDamage cast = (GridDamage) other;
            return coord.equals(cast.coord) 
                    && (Math.abs(damage - cast.damage)) < 0.01f;
        }
    }

    public static final int TYPE = 0x77e9f3c;
    
    private final List<GridDamage> mDamage = new ArrayList<GridDamage>();

    public EngGridUpdatePacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
        
        int offset = 1;
        while (offset < bucket.length && bucket[offset] >= 0) {
            try {
                GridCoord coord = GridCoord.getInstance(
                        bucket[offset], 
                        bucket[offset+1], 
                        bucket[offset+2]);
                float damage = PacketParser.getLendFloat(bucket, offset+3);
                mDamage.add(new GridDamage(coord, damage));
                
                offset += 7;
            } catch (ArrayIndexOutOfBoundsException e) {
                debugPrint();
                System.out.println("DEBUG: offset = " + offset);
                System.out.println("DEBUG: Packet = " + this);
                throw e;
            }
        }
    }

    public void debugPrint() {
        System.out.println("** DAMAGE");
        for (GridDamage d : mDamage) {
            System.out.println("--> @ " + d.coord + ": " + d.damage);
        }
    }

    public List<GridDamage> getDamage() {
        return mDamage;
    }
}
