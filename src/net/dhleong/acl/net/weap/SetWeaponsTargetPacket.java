package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Set the target for our weapons
 * 
 * @author dhleong
 *
 */
public class SetWeaponsTargetPacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    public SetWeaponsTargetPacket(ArtemisObject target) {
        super(FLAGS, TYPE_SET_TARGET, target == null ? 1 : target.getId());
    }
}
