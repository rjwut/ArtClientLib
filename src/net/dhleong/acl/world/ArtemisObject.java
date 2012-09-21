package net.dhleong.acl.world;

public interface ArtemisObject {
    
    public static final byte TYPE_PLAYER = 0x01;
    public static final byte TYPE_ENEMY  = 0x02;
    /** Neutral ships AND friendly ships */
    public static final byte TYPE_OTHER  = 0x03;
    public static final byte TYPE_STATION= 0x04;
    
    public int getType();
    
    public String getName();

    int getId();
    
}
