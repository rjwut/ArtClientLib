package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Set the target for our weapons
 * 
 * @author dhleong
 *
 */
public class SetWeaponsTargetPacket extends BaseArtemisPacket {

    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public SetWeaponsTargetPacket(ArtemisObject target) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(2, mData);
        PacketParser.putLendInt(target == null ? 1 : target.getId(), mData, 4);
    }
}
