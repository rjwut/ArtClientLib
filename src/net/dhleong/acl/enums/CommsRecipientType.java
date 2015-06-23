package net.dhleong.acl.enums;

import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.vesseldata.Vessel;
import net.dhleong.acl.world.ArtemisNpc;
import net.dhleong.acl.world.ArtemisObject;

/**
 * The types of ArtemisObjects to which players can send COMMs messages. 
 * @author rjwut
 */
public enum CommsRecipientType {
	/**
	 * Other player ships
	 */
	PLAYER {
		@Override
		public CommsMessage messageFromId(int id) {
			return PlayerMessage.values()[id];
		}
	},
	/**
	 * NCP enemy ships
	 */
	ENEMY {
		@Override
		public CommsMessage messageFromId(int id) {
			return EnemyMessage.values()[id];
		}
	},
	/**
	 * Bases
	 */
	BASE {
		@Override
		public CommsMessage messageFromId(int id) {
			return BaseMessage.values()[id];
		}
	},
	/**
	 * Other (civilian NPCs)
	 */
	OTHER {
		@Override
		public CommsMessage messageFromId(int id) {
			return OtherMessage.fromId(id);
		}
	};

	/**
	 * Returns the CommsRecipientType that corresponds to the given
	 * ArtemisObject; or null if the object in question cannot receive COMMs
	 * messages.
	 */
	public static CommsRecipientType fromObject(ArtemisObject obj) {
		ObjectType type = obj.getType();

		switch (type) {
		case PLAYER_SHIP:
			return PLAYER;
		case BASE:
			return BASE;
		case NPC_SHIP:
			ArtemisNpc npc = (ArtemisNpc) obj;
			Vessel vessel = npc.getVessel();
			boolean enemy;

			if (vessel != null) {
				enemy = vessel.getFaction().is(FactionAttribute.ENEMY);
			} else {
				enemy = BoolState.safeValue(npc.isEnemy());
			}

			return enemy ? ENEMY : OTHER;
		default:
			return null;
		}
	}

	/**
	 * Returns the CommsMessage value that corresponds to the given message ID.
	 */
	public abstract CommsMessage messageFromId(int id);
}