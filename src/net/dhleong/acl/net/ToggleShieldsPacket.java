package net.dhleong.acl.net;


/**
 * Toggle shields
 * 
 * @author dhleong
 *
 */
public class ToggleShieldsPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x4C821D3C;
    private static final int FLAGS = 0x0c;
    
    public ToggleShieldsPacket() {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(0x04, mData);
        PacketParser.putLendInt(0x00, mData, 4);
    }
}
