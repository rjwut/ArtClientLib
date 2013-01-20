package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;


/**
 * The official client sends this sometimes in 1.7....
 *   
 * @author dhleong
 *
 */
public class ReadyPacket2 extends BaseArtemisPacket {
    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public ReadyPacket2() {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        // ??
        PacketParser.putLendInt(0x19, mData, 0);
        PacketParser.putLendInt(0, mData, 4);
    }
}
