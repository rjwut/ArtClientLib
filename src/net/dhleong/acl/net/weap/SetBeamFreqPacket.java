package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class SetBeamFreqPacket extends BaseArtemisPacket {

    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    /**
     * 
     * @param freq [0, 4] representing [A...E]
     */
    public SetBeamFreqPacket(int freq) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(9, mData);
        PacketParser.putLendInt(freq, mData, 4);
    }
}
