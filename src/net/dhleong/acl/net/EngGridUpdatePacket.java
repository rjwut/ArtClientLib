package net.dhleong.acl.net;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.util.GridCoord;

public class EngGridUpdatePacket extends BaseArtemisPacket {
    
    public static final class GridDamage {
        public final GridCoord coord;
        public final float damage;
        
        private GridDamage(GridCoord coord, float damage) {
            this.coord = coord;
            this.damage = damage;
        }
    }

    public static final int TYPE = 0x77e9f3c;
    
    private final List<GridDamage> mDamage = new ArrayList<GridDamage>();

    public EngGridUpdatePacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
        
        int offset = 1;
        while (offset < bucket.length && bucket[offset] >= 0) {
                try {
                GridCoord coord = new GridCoord(
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
