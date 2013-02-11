package net.dhleong.acl.world;

/**
 * Some sort of object in the World.
 *  Every object has a Type, a Name
 *  and an ID
 * 
 * @author dhleong
 *
 */
public interface ArtemisObject {
    
    public static final byte TYPE_PLAYER_MAIN = 0x01;
    public static final byte TYPE_PLAYER_WEAP  = 0x02;
    public static final byte TYPE_PLAYER_ENG  = 0x03;
    
    public static final byte TYPE_ENEMY  = 0x04;
    /** Neutral ships AND friendly ships */
    public static final byte TYPE_OTHER  = 0x05;
    public static final byte TYPE_STATION= 0x06; 
    
    public static final byte TYPE_MINE      = 0x07;
    public static final byte TYPE_ANOMALY   = 0x08;  // TODO CONFIRM

    public static final byte TYPE_NEBULA    = 0x0a;
    
    public static final byte TYPE_TORPEDO   = 0x0b; 

    public static final byte TYPE_BLACK_HOLE= 0x0c;
    public static final byte TYPE_ASTEROID  = 0x0d;
    public static final byte TYPE_MESH      = 0x0e; // TODO CONFIRM
    
    public static final byte TYPE_MONSTER   = 0x0f; // TODO CONFIRM
    public static final byte TYPE_WHALE     = 0x10; // TODO CONFIRM
    
    public int getType();
    
    public String getName();

    int getId();
    
}
