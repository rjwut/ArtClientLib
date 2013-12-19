package net.dhleong.acl.net.weap;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.net.ShipActionPacket;

public class SetBeamFreqPacket extends ShipActionPacket {
    public SetBeamFreqPacket(BeamFrequency frequency) {
        super(TYPE_SET_BEAMFREQ, frequency.ordinal());
    }
}