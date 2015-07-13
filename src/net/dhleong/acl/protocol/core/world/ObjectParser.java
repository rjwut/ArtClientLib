package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.world.ArtemisObject;

public interface ObjectParser {
	public Enum<?>[] getBits();
	public ArtemisObject parse(PacketReader reader);
	public void write(ArtemisObject obj, PacketWriter writer);
	public void appendDetail(ArtemisObject obj, StringBuilder b);
}
