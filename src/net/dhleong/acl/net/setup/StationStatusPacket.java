package net.dhleong.acl.net.setup;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.util.ObjectParser;

/**
 * Indicates which stations are taken. Not
 *  "pushed" out, but we get it in response
 *  to a SetStationPacket or SetShipPacket.
 *  
 * This only describes actually "take-able"
 *  stations: helm, weapons, and engineering
 *  
 * @author dhleong
 *
 */
public class StationStatusPacket implements ArtemisPacket {
    
    /**
     * Describes the status of a station
     * @author dhleong
     *
     */
    public enum Status {
        /**
         * Un-taken; available
         */
        OPEN,
        
        /**
         * *I* Have taken it for myself
         */
        MINE,
        
        /**
         * Someone else has taken it; unavailable
         */
        OTHER
    }
    
    public static final int TYPE = 0x19c6e2d4;
    
    /**
     * The number of the ship we describe, 
     *  starting from 1 (Artemis)
     */
    public final int shipNumber;
    
    public final Status helm, weapons, engineer;
    
    public StationStatusPacket(byte[] bucket) {
        
        ObjectParser p = new ObjectParser(bucket, 0);
        shipNumber = p.readInt();
        
        // ?
        p.readByte();
        
        final Status[] values = Status.values();
        
        helm = values[ p.readByte() ];
        weapons = values[ p.readByte() ];
        engineer = values[ p.readByte() ];
    }

    @Override
    public long getMode() {
        return 0x01;
    }
    
    /**
     * Get the status for a specific station
     * @param station
     * @return
     */
    public Status get(StationType station) {
        switch (station) {
        case HELM:
            return helm;
        case WEAPONS:
            return weapons;
        case ENGINEERING:
            return engineer;
            
        default:
            // the rest are always open
            return Status.OPEN;
        }
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        // nothing to ever write
        return false;
    }

    @Override
    public int getType() {
        return TYPE;
    }
    
    @Override
    public String toString() {
        return String.format("[Ship #%d:%s%s%s]",
                shipNumber,
                (helm != Status.OPEN) ? " HELM=" + helm : "",
                (weapons  != Status.OPEN) ? " WEAP=" + weapons : "",
                (engineer  != Status.OPEN) ? " ENG=" + engineer : ""
                );
    }
}
