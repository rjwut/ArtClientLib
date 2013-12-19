package net.dhleong.acl.enums;

import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.world.ArtemisNpc;
import net.dhleong.acl.world.ArtemisObject;

public enum CommsTargetType {
	PLAYER,
	ENEMY,
	STATION,
	OTHER;

	public static CommsTargetType fromObject(ArtemisObject obj) {
		ObjectType type = obj.getType();

		switch (type) {
		case PLAYER_SHIP:
			return PLAYER;
		case SPACE_STATION:
			return STATION;
		case NPC_SHIP:
			BoolState enemy = ((ArtemisNpc) obj).isEnemy();
			return BoolState.safeValue(enemy) ? ENEMY : OTHER;
		default:
			return null;
		}
	}
}