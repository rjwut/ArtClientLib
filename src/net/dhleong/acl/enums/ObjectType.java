package net.dhleong.acl.enums;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.DroneUpdatePacket;
import net.dhleong.acl.net.NpcUpdatePacket;
import net.dhleong.acl.net.GenericMeshPacket;
import net.dhleong.acl.net.GenericUpdatePacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.StationPacket;
import net.dhleong.acl.net.WhaleUpdatePacket;
import net.dhleong.acl.net.player.EngPlayerUpdatePacket;
import net.dhleong.acl.net.player.MainPlayerUpdatePacket;
import net.dhleong.acl.net.player.WeapPlayerUpdatePacket;

/**
 * World object types.
 * @author rwalker
 */
public enum ObjectType {
	PLAYER_SHIP(1, true) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new MainPlayerUpdatePacket(reader);
		}
	},
	WEAPONS_BRIDGE_STATION(2, false) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new WeapPlayerUpdatePacket(reader);
		}
	},
	ENGINEERING_BRIDGE_STATION(3, false) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new EngPlayerUpdatePacket(reader);
		}
	},
	NPC_SHIP(4, true) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new NpcUpdatePacket(reader);
		}
	},
	SPACE_STATION(5, true) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new StationPacket(reader);
		}
	},
	MINE(6, false),
	ANOMALY(7, true),
	NEBULA(9, false),
	TORPEDO(10, false),
	BLACK_HOLE(11, true),
	ASTEROID(12, false),
	GENERIC_MESH(13, true) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
            return new GenericMeshPacket(reader);
		}
	},
	MONSTER(14, true),
	WHALE(15, true) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
        	return new WhaleUpdatePacket(reader);
		}
	},
	DRONE(16, false) {
		@Override
		public ArtemisPacket buildPacket(PacketReader reader) {
        	return new DroneUpdatePacket(reader);
		}
	};

	public static ObjectType fromId(int id) {
		for (ObjectType objectType : values()) {
			if (objectType.id == id) {
				return objectType;
			}
		}

		return null;
	}

	private int id;
	private boolean named;

	ObjectType(int id, boolean named) {
		this.id = id;
		this.named = named;
	}

	/**
	 * Returns the ID of this type.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns true if objects of this type can have a name; false otherwise.
	 */
	public boolean isNamed() {
		return named;
	}

	/**
	 * Converts the given byte array into the ArtemisPacket subtype that
	 * corresponds to this object type.
	 */
	public ArtemisPacket buildPacket(PacketReader reader) {
        return new GenericUpdatePacket(reader);
	}
}