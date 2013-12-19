package net.dhleong.acl.enums;

/**
 * The various factions in the Artemis universe.
 * @author rwalker
 */
public enum Faction {
	TSN("TSN"),
	CIVILIAN("Civilian"),
	KRALIEN("Kralien"),
	ARVONIAN("Arvonian"),
	TORGOTH("Torgoth"),
	SKARAAN("Skaraan"),
	BIOMECH("Biomech");

	private String label;

	Faction(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}