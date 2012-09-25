package net.dhleong.acl.util;

/**
 * A 3d grid coordinate, for referencing
 *  internal systems on the Player's ship
 *  
 * @author dhleong
 *
 */
public final class GridCoord {
    
    public final int x, y, z;

    public GridCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) 
            return true;
        if (other == null || !(other instanceof GridCoord)) 
            return false;

        GridCoord cast = (GridCoord) other;

        return (x == cast.x 
                && y == cast.y 
                && z == cast.z);
    }

    @Override
    public int hashCode() {
        int result = (x ^ (x >>> 32));
        result = 31 * result + (y ^ (y >>> 32));
        result = 31 * result + (z ^ (z >>> 32));
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", x, y, z);
    }
}