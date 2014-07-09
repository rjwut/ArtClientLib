package net.dhleong.acl.enums;

/**
 * The various factions in the Artemis universe.
 * @author rjwut
 */
public enum Faction {
	TSN("TSN", Allegiance.PLAYER),
	CIVILIAN("Civilian", Allegiance.FRIENDLY),
	KRALIEN("Kralien", Allegiance.ENEMY),
	ARVONIAN("Arvonian", Allegiance.ENEMY),
	TORGOTH("Torgoth", Allegiance.ENEMY),
	SKARAAN("Skaraan", Allegiance.ENEMY),
	BIOMECH("Biomech", Allegiance.ENEMY);

	private String label;
	private Allegiance allegiance;

	Faction(String label, Allegiance allegiance) {
		this.label = label;
		this.allegiance = allegiance;
	}

	public Allegiance getAllegiance() {
		return allegiance;
	}

	@Override
	public String toString() {
		return label;
	}
}