package net.dhleong.acl.net.setup;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;

/**
 * Indicates which stations are taken.
 *  NOT super reliable...
 *  
 * @author dhleong
 *
 */
public class StationStatusPacket implements ArtemisPacket {
    
    public static final int TYPE = 0x19c6e2d4;
    
    /**
     * The number of the ship we describe, 
     *  starting from 1 (Artemis)
     */
    public final int shipNumber;
    
    public final boolean helmTaken, weaponsTaken, engineerTaken;
    
    public StationStatusPacket(byte[] bucket) {
        
        ObjectParser p = new ObjectParser(bucket, 0);
        shipNumber = p.readInt();
        
        // ?
        p.readByte();
        
        helmTaken = p.readByte() != 0;
        weaponsTaken = p.readByte() != 0;
        engineerTaken = p.readByte() != 0;
    }

    @Override
    public long getMode() {
        return 0x01;
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
                helmTaken ? " HELM" : "",
                weaponsTaken ? " WEAP" : "",
                engineerTaken ? " ENG" : ""
                );
    }
}
