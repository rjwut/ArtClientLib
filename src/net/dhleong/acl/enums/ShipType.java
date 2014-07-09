package net.dhleong.acl.enums;

import java.util.Arrays;

/**
 * The default list of ship types. Note that this enumeration will only be
 * accurate if the vesselData.xml file is unchanged.
 * @author rjwut
 */
public enum ShipType {
	// TSN vessels
	TSN_LIGHT_CRUISER(0, Faction.TSN, "Light Cruiser"),
	TSN_SCOUT(1, Faction.TSN, "Scout"),
	TSN_BATTLESHIP(2, Faction.TSN, "Battleship"),
	TSN_MISSILE_CRUISER(3, Faction.TSN, "Missile Cruiser"),
	TSN_DREADNOUGHT(4, Faction.TSN, "Dreadnought"),

	// friendly bases
	DEEP_SPACE_BASE(1000, Faction.CIVILIAN, "Deep Space Base"),
	CIVILIAN_BASE(1001, Faction.CIVILIAN, "Civilian Base"),
	COMMAND_BASE(1002, Faction.CIVILIAN, "Command Base"),
	INDUSTRIAL_BASE(1003, Faction.CIVILIAN, "Industrial Base"),
	SCIENCE_BASE(1004, Faction.CIVILIAN, "Science Base"),

	// enemy bases
	KRALIEN_BASE(1005, Faction.KRALIEN, "Kralien Base"),
	ARVONIAN_BASE(1006, Faction.ARVONIAN, "Arvonian Base"),
	TORGOTH_BASE(1007, Faction.TORGOTH, "Torgoth Base"),
	SKARAAN_BASE(1008, Faction.SKARAAN, "Skaraan Base"),

	// civilian vessels
	CIVILIAN_ESCORT(1500, Faction.CIVILIAN, "Escort"),
	CIVILIAN_DESTROYER(1501, Faction.CIVILIAN, "Destroyer"),
	CIVILIAN_SCIENCE_VESSEL(1502, Faction.CIVILIAN, "Science Vessel"),
	CIVILIAN_BULK_CARGO(1503, Faction.CIVILIAN, "Bulk Cargo"),
	CIVILIAN_LUXURY_LINER(1504, Faction.CIVILIAN, "Luxury Liner"),
	CIVILIAN_TRANSPORT(1505, Faction.CIVILIAN, "Transport"),

	// Kralien vessels
	KRALIEN_CRUISER(2000, Faction.KRALIEN, "Cruiser"),
	KRALIEN_BATTLESHIP(2001, Faction.KRALIEN, "Battleship"),
	KRALIEN_DREADNOUGHT(2002, Faction.KRALIEN, "Dreadnought"),

	// Arvonian vessels
	ARVONIAN_FIGHTER(3000, Faction.ARVONIAN, "Fighter"),
	ARVONIAN_LIGHT_CARRIER(3001, Faction.ARVONIAN, "Light Carrier"),
	ARVONIAN_CARRIER(3002, Faction.ARVONIAN, "Carrier"),

	// Torgoth vessels
	TORGOTH_GOLIATH(4000, Faction.TORGOTH, "Goliath"),
	TORGOTH_LEVIATHAN(4001, Faction.TORGOTH, "Leviathan"),
	TORGOTH_BEHEMOTH(4002, Faction.TORGOTH, "Behemoth"),

	// Skaraan vessels
	SKARAAN_DEFILER(5000, Faction.SKARAAN, "Defiler"),
	SKARAAN_ENFORCER(5001, Faction.SKARAAN, "Enforcer"),
	SKARAAN_EXECUTOR(5002, Faction.SKARAAN, "Executor"),

	// Biomech vessels
	BIOMECH_STAGE_1(6000, Faction.BIOMECH, "Stage 1"),
	BIOMECH_STAGE_2(6001, Faction.BIOMECH, "Stage 2"),
	BIOMECH_STAGE_3(6002, Faction.BIOMECH, "Stage 3"),
	BIOMECH_STAGE_4(6003, Faction.BIOMECH, "Stage 4");

	private static final ShipType[] PLAYER_SHIPS = Arrays.copyOfRange(values(), 0, DEEP_SPACE_BASE.ordinal());
	private static int FIRST_BASE_INDEX = DEEP_SPACE_BASE.ordinal();
	private static int LAST_BASE_INDEX = SKARAAN_BASE.ordinal();

	public static ShipType[] playerShips() {
		return Arrays.copyOf(PLAYER_SHIPS, PLAYER_SHIPS.length);
	}

	public static ShipType fromId(int id) {
		for (ShipType shipType : ShipType.values()) {
			if (shipType.id == id) {
				return shipType;
			}
		}

		return null;
	}

	private int id;
	private Faction faction;
	private String hullName;
	private String fullName;

	ShipType(int id, Faction faction, String hullName) {
		this.id = id;
		this.faction = faction;
		this.hullName = hullName;
		fullName = (isBase() ? "" : (faction + " ")) + hullName;
	}

	public int getId() {
		return id;
	}

	public boolean isPlayerShip() {
		return faction.getAllegiance() == Allegiance.PLAYER;
	}

	public boolean isBase() {
		int ordinal = ordinal();
		return ordinal >= FIRST_BASE_INDEX && ordinal <= LAST_BASE_INDEX;
	}

	public Faction getFaction() {
		return faction;
	}

	public Allegiance getAllegiance() {
		return faction.getAllegiance();
	}

	public String getHullName() {
		return hullName;
	}

	@Override
	public String toString() {
		return fullName;
	}
}