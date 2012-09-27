package net.dhleong.acl.util;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * A 3d grid coordinate, for referencing
 *  internal systems on the Player's ship
 *  
 * @author dhleong
 *
 */
public final class GridCoord {

    private static final int CACHE_SIZE = 50;

    private static final boolean DEBUG = false;

    private static final Queue<GridCoord> sCache = new ArrayDeque<GridCoord>(CACHE_SIZE);

    public final int x, y, z;

    private GridCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) 
            return true;
        if (other == null || !(other instanceof GridCoord)) 
            return false;

        GridCoord cast = (GridCoord) other;

        return equals(cast.x, cast.y, cast.z);
    }

    public final boolean equals(int x, int y, int z) {
        return (x == this.x 
                && y == this.y 
                && z == this.z);
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

    /**
     * This factory method uses a very simple LRU queue
     *  to maintain a cache of GridCoords, since we will
     *  probably reuse just a handful but fairly often.
     *  This should keep our memory footprint to a minimum.
     * 
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static final GridCoord getInstance(int x, int y, int z) {
        synchronized(sCache) {
            Iterator<GridCoord> iter = sCache.iterator();
            while (iter.hasNext()) {
                GridCoord c = iter.next();
                if (c.equals(x, y, z)) {
                    iter.remove(); // pop out so we can move it to the head
                    sCache.offer(c);
                    if (DEBUG) System.out.println("~~ Move to head: " + c);
                    return c;
                }
            }
        }

        GridCoord c = new GridCoord(x, y, z);

        // put it in the queue, if there's room. 
        int size = sCache.size();
        if (size >= CACHE_SIZE) {
            GridCoord old = sCache.poll(); // free up space
            if (DEBUG) System.out.println("~~ Removed: " + old + " for " + c);
        }

        sCache.offer(c);

        return c;
    }
}