package net.dhleong.acl.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
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
    
    private final HashMap<GridCoord, SystemType> mSystems = new HashMap<GridCoord, SystemType>();
    private final int[] mSystemCounts = new int[SystemType.values().length];

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
            
            int system = PacketParser.getLendInt(row, 12);
            if (system >= 0) {
                mSystems.put(new GridCoord(x, y, z), SystemType.values()[system]);
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
    public int getSystemCount(SystemType sys) {
        return mSystemCounts[sys.ordinal()];
    }
    
    public SystemType getSystemTypeAt(GridCoord coord) {
        return mSystems.get(coord);
    }
}
