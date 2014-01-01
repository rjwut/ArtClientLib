package net.dhleong.acl.enums;

/**
 * Elite abilities that enemy ships may have.
 * TODO Update for Artemis 2.0
 * @author rjwut
 */
public enum EliteAbility {
	INVISIBLE_TO_MAIN_SCREEN,
	INVISIBLE_TO_SCIENCE,
	INVISIBLE_TO_TACTICAL,
	CLOAKING,
	HIGH_ENERGY_TURN,
	WARP,
	TELEPORT;

	private int bit;

	EliteAbility() {
		bit = 0x01 << ordinal();
	}

	public boolean on(int flags) {
		return (flags & bit) != 0;
	}
}