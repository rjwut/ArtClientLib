package net.dhleong.acl.world;

public interface ArtemisObject {
    
    public static final byte TYPE_PLAYER = 0x01;
    public static final byte TYPE_ENEMY  = 0x02;
    /** Neutral ships AND friendly ships */
    public static final byte TYPE_OTHER  = 0x03;
    public static final byte TYPE_STATION= 0x04;
    
    public static final byte TYPE_MINE      = 0x05;
    public static final byte TYPE_ANOMALY   = 0x06; 
    // TODO whales?
    public static final byte TYPE_NEBULA    = 0x08;
    // TODO monsters?
    public static final byte TYPE_BLACK_HOLE= 0x0a;
    public static final byte TYPE_ASTEROID  = 0x0b;
    
    public int getType();
    
    public String getName();

    int getId();
    
}
