package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.world.ArtemisObject;

public abstract class AbstractObjectParser implements ObjectParser {
	protected abstract ArtemisObject parseImpl(PacketReader reader);

	protected ObjectType objectType;

	protected AbstractObjectParser(ObjectType objectType) {
		this.objectType = objectType;
	}

	@Override
	public final ArtemisObject parse(PacketReader reader) {
		byte typeId = reader.hasMore() ? reader.readByte() : 0;

		if (typeId == 0) {
			return null; // no more objects to parse
		}

		ObjectType parsedObjectType = ObjectType.fromId(typeId);

		if (objectType != parsedObjectType) {
			throw new IllegalStateException("Expected to parse " + objectType +
					" but received " + parsedObjectType);
		}

		reader.startObject(objectType, getBits());
		return parseImpl(reader);
	}

	@Override
	public void appendDetail(ArtemisObject obj, StringBuilder b) {
		b.append("\nObject #").append(obj.getId()).append(obj);
	}
}