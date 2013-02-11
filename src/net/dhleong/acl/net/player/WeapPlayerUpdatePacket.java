package net.dhleong.acl.net.player;

import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player data related to weapons subsystems
 * @author dhleong
 *
 */
public class WeapPlayerUpdatePacket extends PlayerUpdatePacket {
    
    private static final byte TORP_HOMING   = 0x01;
    private static final byte TORP_NUKES    = 0x02;
    private static final byte TORP_MINES    = 0x04;
    private static final byte TORP_ECMS     = 0x08;

    private static final byte UNKNOWN_BYTE  = 0x10;

    private static final byte TUBE_TIME_1   = 0x20;
    private static final byte TUBE_TIME_2   = 0x40;
    private static final byte TUBE_TIME_3   = (byte) 0x80;
    private static final int TUBE_TIME_4   = 0x00000001;
    private static final int TUBE_TIME_5   = 0x00000002;
    private static final int TUBE_TIME_6   = 0x00000004;

    /* IE: is this tube in use? */
    private static final int TUBE_USE_1    = 0x00000008;
    private static final int TUBE_USE_2    = 0x00000010;
    private static final int TUBE_USE_3    = 0x00000020;
    private static final int TUBE_USE_4    = 0x00000040;

    private static final int TUBE_USE_5    = 0x00000080;
    private static final int TUBE_USE_6    = 0x00000100;

    private static final int TUBE_TYPE_1   = 0x00000200;
    private static final int TUBE_TYPE_2   = 0x00000400;
    private static final int TUBE_TYPE_3   = 0x00000800;
    private static final int TUBE_TYPE_4   = 0x00001000;
    private static final int TUBE_TYPE_5   = 0x00002000;
    private static final int TUBE_TYPE_6   = 0x00004000;
    

    private static final byte[] TORPEDOS = {
        TORP_HOMING, TORP_NUKES, TORP_MINES, TORP_ECMS
    };

    static final byte[] TUBE_TIMES_BYTE = {
        TUBE_TIME_1, TUBE_TIME_2, TUBE_TIME_3,
    };
    static final int[] TUBE_TIMES_INT = {
        TUBE_TIME_4, TUBE_TIME_5, TUBE_TIME_6,
    };

    private static final int[] TUBE_TYPES = {
        TUBE_TYPE_1, TUBE_TYPE_2, TUBE_TYPE_3,
        TUBE_TYPE_4, TUBE_TYPE_5, TUBE_TYPE_6,
    };
    
        
    int[] torps = new int[ TORPEDOS.length ];

    float[] tubeTimes = new float[ArtemisPlayer.MAX_TUBES];
    int[] tubeContents = new int[ArtemisPlayer.MAX_TUBES];
    private ArtemisPlayer mPlayer;


    public WeapPlayerUpdatePacket(byte[] data) {
        super(data);
        
        ObjectParser p = new ObjectParser(mData, 0);
        p.start();
        p.startNoArgs();
                    
        try {
            
            int shortArgs = p.readShort();
            p.setArgs(shortArgs);
        
            for (int i=0; i<torps.length; i++) {
                torps[i] = ((byte)0xff & p.readByte(TORPEDOS[i], (byte)-1));
            }
            
            // I guess?
            p.readByte(UNKNOWN_BYTE, (byte)-1);
               
            for (int i=0; i<TUBE_TIMES_BYTE.length; i++) {
                tubeTimes[i] = p.readFloat(TUBE_TIMES_BYTE[i], -1);
            }
            final int byteOffset = TUBE_TIMES_BYTE.length;
            for (int i=0; i<TUBE_TIMES_INT.length; i++) {
                tubeTimes[byteOffset + i] = p.readFloat(TUBE_TIMES_INT[i], -1);
            }

            // after this, tubeContents[i]...
            // = 0 means that tube is EMPTY; 
            // > 0 means that tube is IN USE;
            // < 0  means we DON'T KNOW
            tubeContents[0] = p.readByte(TUBE_USE_1, (byte)-1);
            tubeContents[1] = p.readByte(TUBE_USE_2, (byte)-1);
            tubeContents[2] = p.readByte(TUBE_USE_3, (byte)-1);
            tubeContents[3] = p.readByte(TUBE_USE_4, (byte)-1);
            tubeContents[4] = p.readByte(TUBE_USE_5, (byte)-1);
            tubeContents[5] = p.readByte(TUBE_USE_6, (byte)-1);

            // after this, tubeContents[i]...
            // = -1 means EMPTY;
            // = Integer.MIN_VALUE means we DON'T KNOW
            // else the type of torpedo there
            for (int i=0; i<TUBE_TYPES.length; i++) {
                byte torpType = p.readByte(TUBE_TYPES[i], (byte)-1);
                if (tubeContents[i] == 0)
                    tubeContents[i] = ArtemisPlayer.TUBE_EMPTY; // empty tube
                else if (tubeContents[i] < 0) {
                    // what's there? I don't even know
                    tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN;
                } else if (tubeContents[i] > 0 && torpType != (byte) -1)
                    tubeContents[i] = torpType;
                else
                    // IE: it's "in use" but type is unspecified/changed
                    //  DO we need another constant for this?
                    tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN; 
            }


            mPlayer = new ArtemisPlayer(p.getTargetId());

            for (int i=0; i<TORPEDOS.length; i++) {
                mPlayer.setTorpedoCount(i, torps[i]);
            }

            for (int i=0; i<TUBE_TIMES_BYTE.length; i++) {
                mPlayer.setTubeStatus(i, tubeTimes[i], tubeContents[i]);
            }

        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            debugPrint();
            System.out.println("this -->" + this);
            throw e;
        }
    }

    @Override
    public void debugPrint() {
        System.out.println(String.format("-------Torp Cnts: %d:%d:%d:%d", 
            torps[0], torps[1], torps[2], torps[3]));
        for (int i=0; i<TUBE_TIMES_BYTE.length; i++) {
            System.out.println(String.format("Tube#%d: (%f) %d", 
                i, tubeTimes[i], tubeContents[i]));
        }
    }

    @Override
    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }

}
