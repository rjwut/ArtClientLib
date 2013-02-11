package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class SetBeamFreqPacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    /**
     * 
     * @param freq [0, 4] representing [A...E]
     */
    public SetBeamFreqPacket(int freq) {
        super(FLAGS, TYPE_SET_BEAMFREQ, freq);
    }
}
