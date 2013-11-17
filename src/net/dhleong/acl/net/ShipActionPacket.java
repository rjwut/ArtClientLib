package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;

public class ShipActionPacket extends BaseArtemisPacket {

    public static final int TYPE = ArtemisPacket.SHIP_ACTION_TYPE;
    
    protected static final int TYPE_WARPSPEED          = 0x00; 
    protected static final int TYPE_MAINSCREEN         = 0x01;
    protected static final int TYPE_SET_TARGET         = 0x02;
    protected static final int TYPE_TOGGLE_AUTO_BEAMS  = 0x03;
    
    protected static final int TYPE_TOGGLE_SHIELDS     = 0x04;
    protected static final int TYPE_REQUEST_DOCK       = 0x07;
    
    protected static final int TYPE_FIRE_TUBE          = 0x08; 
    protected static final int TYPE_UNLOAD_TUBE        = 0x09; 
    protected static final int TYPE_TOGGLE_REDALERT    = 0x0A; 
    protected static final int TYPE_SET_BEAMFREQ       = 0x0B; 
    protected static final int TYPE_AUTO_DAMCON        = 0x0C; 
    protected static final int TYPE_SET_SHIP           = 0x0D; 
    protected static final int TYPE_SET_STATION        = 0x0E; 
    protected static final int TYPE_READY              = 0x0F; 
    
    protected static final int TYPE_SCI_SELECT         = 0x10; 
    protected static final int TYPE_CAPTAIN_SELECT     = 0x11; 
    protected static final int TYPE_SCI_SCAN           = 0x13; 
    protected static final int TYPE_SHIP_SETUP         = 0x16; 
    
    protected static final int TYPE_REVERSE_ENGINES    = 0x18; 
    protected static final int TYPE_READY2             = 0x19; // maybe "enter" simulation? or explicit info request?
    protected static final int TYPE_TOGGLE_PERSPECTIVE = 0x1A;

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
