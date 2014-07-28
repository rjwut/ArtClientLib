package net.dhleong.acl.enums;

import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.world.ArtemisNpc;
import net.dhleong.acl.world.ArtemisObject;

public enum CommsRecipientType {
	PLAYER {
		@Override
		public CommsMessage messageFromId(int id) {
			return PlayerMessage.values()[id];
		}
	},
	ENEMY {
		@Override
		public CommsMessage messageFromId(int id) {
			return EnemyMessage.values()[id];
		}
	},
	BASE {
		@Override
		public CommsMessage messageFromId(int id) {
			return BaseMessage.values()[id];
		}
	},
	OTHER {
		@Override
		public CommsMessage messageFromId(int id) {
			return OtherMessage.fromId(id);
		}
	};

	public static CommsRecipientType fromObject(ArtemisObject obj) {
		ObjectType type = obj.getType();

		switch (type) {
		case PLAYER_SHIP:
			return PLAYER;
		case BASE:
			return BASE;
		case NPC_SHIP:
			BoolState enemy = ((ArtemisNpc) obj).isEnemy();
			return BoolState.safeValue(enemy) ? ENEMY : OTHER;
		default:
			return null;
		}
	}

	public abstract CommsMessage messageFromId(int id);
}