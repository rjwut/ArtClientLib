package net.dhleong.acl.net.helm;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Set impulse power
 * @author dhleong
 */
public class HelmSetImpulsePacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    
    /**
     * @param power Impulse power percentage
     */
    public HelmSetImpulsePacket(float power) {
        super(ConnectionType.CLIENT, TYPE, new byte[8]);
        PacketParser.putLendInt(0, mData);
        PacketParser.putLendFloat(power, mData, 4);
    }
}