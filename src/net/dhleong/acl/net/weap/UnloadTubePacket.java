package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

public class UnloadTubePacket extends ShipActionPacket {
    public UnloadTubePacket(int tube) {
        super(TYPE_UNLOAD_TUBE, tube);

        if (tube < 0 || tube >= Artemis.MAX_TUBES) {
        	throw new IndexOutOfBoundsException(
        			"Invalid tube index: " + tube
        	);
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}