package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class SetBeamFreqPacket extends ShipActionPacket {
    /**
     * 
     * @param freq [0, 4] representing [A...E]
     */
    public SetBeamFreqPacket(int freq) {
        super(TYPE_SET_BEAMFREQ, freq);
    }
}