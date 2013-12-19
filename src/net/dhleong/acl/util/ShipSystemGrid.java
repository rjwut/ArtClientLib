package net.dhleong.acl.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.net.PacketParser;

/**
 * Some basic management of the internal systems
 *  grid for a Player Ship. This is by no means
 *  comprehensive---we don't keep track of pixel
 *  coordinates, for example, just the "grid"
 *  coordinates---but is just enough so we can
 *  get the Engineering station to be more
 *  complete.
 *  
 * @author dhleong
 *
 */
public class ShipSystemGrid {
    public static class GridEntry {
        public final ShipSystem system;
        
        /** The index of this system among its types, [0,N) */
        public final int index;
        
        private GridEntry(ShipSystem system, int index) {
            this.system = system;
            this.index = index;
        }
    }
    
    private final HashMap<GridCoord, GridEntry> mSystems = new HashMap<GridCoord, GridEntry>();
    private final int[] mSystemCounts = new int[ShipSystem.values().length];

    /**
     * Load the Grid from an InputStream of a .snt file.
     *  This is a BLOCKING constructor!
     *  
     * @param is
     * @throws IOException 
     */
    public ShipSystemGrid(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is, 64);
        byte[] row = new byte[32];
        int x=-1, y=-1, z=-1;
        float xVal=Float.MAX_VALUE, yVal=0;
        while (bis.read(row) > -1) {
            float newX = PacketParser.getLendFloat(row, 0);
            float newY = PacketParser.getLendFloat(row, 4);

            // update coords
            if (newX != xVal) {
                x++;
                y = 0;
                z = 0;
            } else if (newY != yVal) {
                y++;
                z = 0;
            } else {
                z++;
            }

            // flip over
            xVal = newX;
            yVal = newY;
            
            int system = PacketParser.getLendInt(row, 12);
            if (system >= 0) {
                mSystems.put(GridCoord.getInstance(x, y, z), 
                        new GridEntry(ShipSystem.values()[system],
                                mSystemCounts[system]));
                mSystemCounts[system]++;
            }
        }
        
        bis.close();
    }
    
    /**
     * Get the number of nodes we have of the given SystemType 
     * 
     * @param sys
     * @return
     */
    public int getSystemCount(ShipSystem sys) {
        return mSystemCounts[sys.ordinal()];
    }
    
    public GridEntry getGridAt(GridCoord coord) {
        return mSystems.get(coord);
    }
    
    public ShipSystem getSystemTypeAt(GridCoord coord) {
        return mSystems.get(coord).system;
    }

    /**
     * Get the set of GridCoords contained on this grid
     * @return
     */
    public Set<GridCoord> getCoords() {
        return mSystems.keySet();
    }
    
    public Collection<GridCoord> getCoordsFor(ShipSystem sys) {
        List<GridCoord> coords = new ArrayList<GridCoord>(); 
        for (Entry<GridCoord, GridEntry> e : mSystems.entrySet()) {
            if (e.getValue().system == sys)
                coords.add(e.getKey());
        }
        return coords;
    }
}
