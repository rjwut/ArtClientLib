package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Set the target for our weapons.
 * @author dhleong
 */
public class SetWeaponsTargetPacket extends ShipActionPacket {
    public SetWeaponsTargetPacket(ArtemisObject target) {
        super(TYPE_SET_TARGET, target == null ? 1 : target.getId());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}