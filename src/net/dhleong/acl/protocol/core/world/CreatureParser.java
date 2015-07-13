package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.CreatureType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisObject;

public class CreatureParser extends AbstractObjectParser {
	private enum Bit {
    	X,
    	Y,
    	Z,
    	NAME,
    	HEADING,
    	PITCH,
    	ROLL,
    	CREATURE_TYPE,

    	UNK_2_1,
    	UNK_2_2,
    	UNK_2_3,
    	UNK_2_4,
    	UNK_2_5,
    	UNK_2_6
    }
    private static final Bit[] BITS = Bit.values();

    CreatureParser() {
		super(ObjectType.CREATURE);
	}

	@Override
	public Bit[] getBits() {
		return BITS;
	}

	@Override
	protected ArtemisCreature parseImpl(PacketReader reader) {
        final ArtemisCreature creature = new ArtemisCreature(reader.getObjectId());
        creature.setX(reader.readFloat(Bit.X, Float.MIN_VALUE));
        creature.setY(reader.readFloat(Bit.Y, Float.MIN_VALUE));
        creature.setZ(reader.readFloat(Bit.Z, Float.MIN_VALUE));
		creature.setName(reader.readString(Bit.NAME));
        creature.setHeading(reader.readFloat(Bit.HEADING, Float.MIN_VALUE));
        creature.setPitch(reader.readFloat(Bit.PITCH, Float.MIN_VALUE));
        creature.setRoll(reader.readFloat(Bit.ROLL, Float.MIN_VALUE));

        if (reader.has(Bit.CREATURE_TYPE)) {
            creature.setCreatureType(CreatureType.values()[reader.readInt()]);
        }

        reader.readObjectUnknown(Bit.UNK_2_1, 4);
        reader.readObjectUnknown(Bit.UNK_2_2, 4);
        reader.readObjectUnknown(Bit.UNK_2_3, 4);
        reader.readObjectUnknown(Bit.UNK_2_4, 4);
        reader.readObjectUnknown(Bit.UNK_2_5, 4);
        reader.readObjectUnknown(Bit.UNK_2_6, 4);
        creature.setUnknownProps(reader.getUnknownObjectProps());
        return creature;
	}

	@Override
	public void write(ArtemisObject obj, PacketWriter writer) {
		ArtemisCreature creature = (ArtemisCreature) obj;
		writer	.writeFloat(Bit.X, creature.getX(), Float.MIN_VALUE)
				.writeFloat(Bit.Y, creature.getY(), Float.MIN_VALUE)
				.writeFloat(Bit.Z, creature.getZ(), Float.MIN_VALUE)
				.writeString(Bit.NAME, creature.getName())
				.writeFloat(Bit.HEADING, creature.getHeading(), Float.MIN_VALUE)
				.writeFloat(Bit.PITCH, creature.getPitch(), Float.MIN_VALUE)
				.writeFloat(Bit.ROLL, creature.getRoll(), Float.MIN_VALUE);

		CreatureType creatureType = creature.getCreatureType();

		if (creatureType != null) {
			writer.writeInt(Bit.CREATURE_TYPE, creatureType.ordinal(), -1);
		}

		writer	.writeUnknown(Bit.UNK_2_1)
				.writeUnknown(Bit.UNK_2_2)
				.writeUnknown(Bit.UNK_2_3)
				.writeUnknown(Bit.UNK_2_4)
				.writeUnknown(Bit.UNK_2_5)
				.writeUnknown(Bit.UNK_2_6);
	}
}