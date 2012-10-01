package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Request to dock. There must be some ACK when it 
 *  works, but I haven't figured it out yet...
 * @author dhleong
 *
 */
public class HelmRequestDockPacket extends BaseArtemisPacket {
    
    public static final int TYPE = 0x4C821D3C;
    
    private static final int FLAGS = 0x0c;
    
    public HelmRequestDockPacket() {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(5, mData);
        PacketParser.putLendInt(0, mData, 4);
    }
}
