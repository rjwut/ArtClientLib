package net.dhleong.acl.net;

import java.io.IOException;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;

/**
 * A superclass for handling all type 0x4c821d3c client packets.
 * @author rjwut
 */
public abstract class ShipActionPacket extends BaseArtemisPacket {
    protected static final int TYPE = ArtemisPacket.SHIP_ACTION_TYPE;
    
    protected static final int TYPE_WARPSPEED          = 0x00;
    protected static final int TYPE_MAINSCREEN         = 0x01;
    protected static final int TYPE_SET_TARGET         = 0x02;
    protected static final int TYPE_TOGGLE_AUTO_BEAMS  = 0x03;
    protected static final int TYPE_TOGGLE_SHIELDS     = 0x04;

    protected static final int TYPE_REQUEST_DOCK       = 0x07;
    protected static final int TYPE_FIRE_TUBE          = 0x08;
    protected static final int TYPE_UNLOAD_TUBE        = 0x09;
    protected static final int TYPE_TOGGLE_REDALERT    = 0x0a;
    protected static final int TYPE_SET_BEAMFREQ       = 0x0b;
    protected static final int TYPE_AUTO_DAMCON        = 0x0c;
    protected static final int TYPE_SET_SHIP           = 0x0d;
    protected static final int TYPE_SET_STATION        = 0x0e;
    protected static final int TYPE_READY              = 0x0f;
    protected static final int TYPE_SCI_SELECT         = 0x10; 
    protected static final int TYPE_CAPTAIN_SELECT     = 0x11;

    protected static final int TYPE_SCI_SCAN           = 0x13;
    protected static final int TYPE_KEYSTROKE          = 0x14;

    protected static final int TYPE_SHIP_SETUP         = 0x16;

    protected static final int TYPE_REVERSE_ENGINES    = 0x18;
    protected static final int TYPE_READY2             = 0x19;
    protected static final int TYPE_TOGGLE_PERSPECTIVE = 0x1a;

    private int mSubType;
    protected int mArg = -1;

    /**
     * Use this constructor if you intend to override write() with your own
     * implementation and not call ShipActionPacket.write().
     * @param subType The desired packet subtype
     */
    public ShipActionPacket(int subType) {
        super(ConnectionType.CLIENT, TYPE);
        mSubType = subType;
    }

    /**
     * Use this constructor if the packet has a single int argument that is
     * written to the payload after the subtype. In this case, you will not need
     * to override write().
     * @param subType The desired packet subtype
     * @param arg A single argument to write to the payload after the subtype
     */
    public ShipActionPacket(int subType, int arg) {
        this(subType);
        mArg = arg;
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(mSubType)
				.writeInt(mArg);
    }
}