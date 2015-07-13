package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

public class UpgradesParser extends AbstractObjectParser {
	private enum Bit {
		// don't care
	}
	private static final Bit[] BITS = Bit.values();

	UpgradesParser() {
		super(ObjectType.UPGRADES);
	}

	@Override
	public Bit[] getBits() {
		return BITS;
	}

	@Override
	protected ArtemisPlayer parseImpl(PacketReader reader) {
		reader.readObjectUnknown("UNKNOWN", reader.getBytesLeft());
		System.out.println(TextUtil.byteArrayToHexString(reader.getUnknownObjectProps().get("UNKNOWN")));
		return new ArtemisPlayer(reader.getObjectId());
	}

	@Override
	public void write(ArtemisObject obj, PacketWriter writer) {
		// do nothing
	}
}