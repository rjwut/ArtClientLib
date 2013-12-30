package net.dhleong.acl.enums;

/**
 * The bridge stations.
 * @author rjwut
 */
public enum BridgeStation {
	MAIN_SCREEN("Main screen"),
	HELM("Helm"),
	WEAPONS("Weapons"),
	ENGINEERING("Engineering"),
	SCIENCE("Science"),
	COMMUNICATIONS("Communications"),
	OBSERVER("Observer"),
	CAPTAINS_MAP("Captain's map"),
	GAME_MASTER("Game master");

	private String label;

	BridgeStation(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}