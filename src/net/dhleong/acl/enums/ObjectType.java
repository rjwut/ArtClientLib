package net.dhleong.acl.enums;

import net.dhleong.acl.world.ArtemisAnomaly;
import net.dhleong.acl.world.ArtemisBase;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisDrone;
import net.dhleong.acl.world.ArtemisGenericObject;
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisNebula;
import net.dhleong.acl.world.ArtemisNpc;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * World object types.
 * @author rjwut
 */
public enum ObjectType {
	PLAYER_SHIP(1, true, ArtemisPlayer.class),
	WEAPONS_CONSOLE(2, false, ArtemisPlayer.class),
	ENGINEERING_CONSOLE(3, false, ArtemisPlayer.class),
	UPGRADES(4, false, ArtemisPlayer.class),
	NPC_SHIP(5, true, ArtemisNpc.class),
	BASE(6, true, ArtemisBase.class),
	MINE(7, false, ArtemisGenericObject.class),
	ANOMALY(8, true, ArtemisAnomaly.class),
	// 9 is unused
	NEBULA(10, false, ArtemisNebula.class),
	TORPEDO(11, false, ArtemisGenericObject.class),
	BLACK_HOLE(12, false, ArtemisGenericObject.class),
	ASTEROID(13, false, ArtemisGenericObject.class),
	GENERIC_MESH(14, true, ArtemisMesh.class),
	CREATURE(15, true, ArtemisCreature.class),
	DRONE(16, false, ArtemisDrone.class);

	public static ObjectType fromId(int id) {
		if (id == 0) {
			return null;
		}

		for (ObjectType objectType : values()) {
			if (objectType.id == id) {
				return objectType;
			}
		}

		throw new IllegalArgumentException("No ObjectType with this ID: " + id);
	}

	private byte id;
	private boolean named;
	private Class<? extends ArtemisObject> objectClass;

	ObjectType(int id, boolean named, Class<? extends ArtemisObject> objectClass) {
		this.id = (byte) id;
		this.named = named;
		this.objectClass = objectClass;
	}

	/**
	 * Returns the ID of this type.
	 */
	public byte getId() {
		return id;
	}

	/**
	 * Returns true if objects of this type can have a name; false otherwise.
	 */
	public boolean isNamed() {
		return named;
	}

	/**
	 * Returns true if the given object is compatible with this ObjectType.
	 */
	public boolean isCompatible(ArtemisObject obj) {
		return objectClass.equals(obj.getClass());
	}

	/**
	 * Returns the class of object represented by this ObjectType.
	 */
	public Class<? extends ArtemisObject> getObjectClass() {
		return objectClass;
	}
}