package net.dhleong.acl.enums;

/**
 * World object types.
 * @author rjwut
 */
public enum ObjectType {
	PLAYER_SHIP(1, true),
	WEAPONS_BRIDGE_STATION(2, false),
	ENGINEERING_BRIDGE_STATION(3, false),
	NPC_SHIP(4, true),
	SPACE_STATION(5, true),
	MINE(6, false),
	ANOMALY(7, true),
	NEBULA(9, false),
	TORPEDO(10, false),
	BLACK_HOLE(11, true),
	ASTEROID(12, false),
	GENERIC_MESH(13, true),
	MONSTER(14, true),
	WHALE(15, true),
	DRONE(16, false);

	public static ObjectType fromId(int id) {
		for (ObjectType objectType : values()) {
			if (objectType.id == id) {
				return objectType;
			}
		}

		return null;
	}

	private byte id;
	private boolean named;

	ObjectType(int id, boolean named) {
		this.id = (byte) id;
		this.named = named;
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
}