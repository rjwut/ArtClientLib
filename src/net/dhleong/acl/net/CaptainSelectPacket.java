package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisObject;

public class CaptainSelectPacket extends ShipActionPacket {
    public CaptainSelectPacket(ArtemisObject target) {
        super(TYPE_CAPTAIN_SELECT, target == null ? 1 : target.getId());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}