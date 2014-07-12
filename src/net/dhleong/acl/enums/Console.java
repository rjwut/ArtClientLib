package net.dhleong.acl.enums;

/**
 * The bridge conoles.
 * @author rjwut
 */
public enum Console {
	MAIN_SCREEN("Main screen"),
	HELM("Helm"),
	WEAPONS("Weapons"),
	ENGINEERING("Engineering"),
	SCIENCE("Science"),
	COMMUNICATIONS("Communications"),
	OBSERVER("Observer"),
	CAPTAINS_MAP("Captain's map"),
	GAME_MASTER("Game master"),
	DATA("Data");

	private String label;

	Console(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}