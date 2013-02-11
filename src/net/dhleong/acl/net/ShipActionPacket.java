package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;

public class ShipActionPacket extends BaseArtemisPacket {

    public static final int TYPE = ArtemisPacket.SHIP_ACTION_TYPE;
    
    protected static final int TYPE_WARPSPEED      = 0x00; // TODO wrong, I think
    protected static final int TYPE_MAINSCREEN     = 0x01;
    protected static final int TYPE_SET_TARGET     = 0x02;
    
    protected static final int TYPE_TOGGLE_SHIELDS = 0x04;
    protected static final int TYPE_REQUEST_DOCK   = 0x05;
    protected static final int TYPE_FIRE_TUBE      = 0x06; // may be wrong
    
    protected static final int TYPE_TOGGLE_REDALERT= 0x08; // TODO no longer true, I think
    protected static final int TYPE_SET_BEAMFREQ   = 0x18; // TODO WRONG
    protected static final int TYPE_UNLOAD_TUBE    = 0x09; 
    
    protected static final int TYPE_AUTO_DAMCON    = 0x0A; // I *think* this is still correct
    protected static final int TYPE_SET_SHIP       = 0x0B; // I *think* this is still correct
    
    protected static final int TYPE_SET_STATION    = 0x0E; 
    protected static final int TYPE_SCI_SELECT     = 0x1E; // TODO WRONG
    protected static final int TYPE_READY          = 0x0F; // ?
    
    protected static final int TYPE_SCI_SCAN       = 0x11; // I *think* this is still correct
    protected static final int TYPE_SHIP_SETUP     = 0x13; // I *think* this is still correct
    protected static final int TYPE_READY2         = 0x19; // ...

    public ShipActionPacket(int flags, int subType, int arg) {
        super(0x02, flags, TYPE, new byte[8]);
        
        PacketParser.putLendInt(subType, mData);
        PacketParser.putLendInt(arg, mData, 4);
    }

    public ShipActionPacket(int flags, int subType, byte[] bytes) {
        super(0x02, flags, TYPE, bytes);
        
        PacketParser.putLendInt(subType, mData);
    }

}
