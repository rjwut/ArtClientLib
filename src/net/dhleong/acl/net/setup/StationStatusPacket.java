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
    
    public final Status mainScreen, helm, weapons, engineer, science, comms, observer,
    	captainsMap, gameMaster;
    
    public StationStatusPacket(byte[] bucket) {
        ObjectParser p = new ObjectParser(bucket, 0);
        shipNumber = p.readInt();
        final Status[] values = Status.values();
        mainScreen = values[ p.readByte() ];
        helm = values[ p.readByte() ];
        weapons = values[ p.readByte() ];
        engineer = values[ p.readByte() ];
        science = values[ p.readByte() ];
        comms = values[ p.readByte() ];
        observer = values[ p.readByte() ];
        captainsMap = values[ p.readByte() ];
        gameMaster = values[ p.readByte() ];
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
        if (station == null) {
            throw new IllegalArgumentException("Null station");
        }

        switch (station) {
        case MAINSCREEN:
        	return mainScreen;
        case HELM:
            return helm;
        case WEAPONS:
            return weapons;
        case ENGINEERING:
            return engineer;
        case SCIENCE:
        	return science;
        case COMMS:
        	return comms;
        case OBSERVER:
        	return observer;
        case CAPTAINS_MAP:
        	return captainsMap;
        case GAME_MASTER:
        	return gameMaster;
        default:
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
                Integer.valueOf(shipNumber),
                (mainScreen != Status.OPEN) ? " MAINSCR=" + mainScreen : "",
                (helm != Status.OPEN) ? " HELM=" + helm : "",
                (weapons != Status.OPEN) ? " WEAP=" + weapons : "",
                (engineer != Status.OPEN) ? " ENG=" + engineer : "",
                (science != Status.OPEN) ? " SCI=" + science : "",
                (comms != Status.OPEN) ? " COMMS=" + comms : "",
                (observer != Status.OPEN) ? " OBSRV=" + observer : "",
                (captainsMap != Status.OPEN) ? " CPTNMAP=" + captainsMap : "",
                (gameMaster != Status.OPEN) ? " GAMEMSTR=" + gameMaster : ""
        );
    }
}
