package net.dhleong.acl.enums;

import java.util.Arrays;

/**
 * The default list of ship types. Note that this enumeration will only be
 * accurate if the vesselData.xml file is unchanged.
 * @author rwalker
 */
public enum ShipType {
	TSN_LIGHT_CRUISER(0, Faction.TSN, "Light Cruiser"),
	TSN_SCOUT(1, Faction.TSN, "Scout"),
	TSN_BATTLESHIP(2, Faction.TSN, "Battleship"),
	TSN_MISSILE_CRUISER(3, Faction.TSN, "Missile Cruiser"),
	TSN_DREADNOUGHT(4, Faction.TSN, "Dreadnought"),
	TSN_BASE(1000, Faction.TSN, "Base"),
	CIVILIAN_ESCORT(1500, Faction.CIVILIAN, "Escort"),
	CIVILIAN_DESTROYER(1501, Faction.CIVILIAN, "Destroyer"),
	CIVILIAN_SCIENCE_VESSEL(1502, Faction.CIVILIAN, "Science Vessel"),
	CIVILIAN_BULK_CARGO(1503, Faction.CIVILIAN, "Bulk Cargo"),
	CIVILIAN_LUXURY_LINER(1504, Faction.CIVILIAN, "Luxury Liner"),
	CIVILIAN_TRANSPORT(1505, Faction.CIVILIAN, "Transport"),
	KRALIEN_CRUISER(2000, Faction.KRALIEN, "Cruiser"),
	KRALIEN_BATTLESHIP(2001, Faction.KRALIEN, "Battleship"),
	KRALIEN_DREADNOUGHT(2002, Faction.KRALIEN, "Dreadnought"),
	ARVONIAN_FIGHTER(3000, Faction.ARVONIAN, "Fighter"),
	ARVONIAN_LIGHT_CARRIER(3001, Faction.ARVONIAN, "Light Carrier"),
	ARVONIAN_CARRIER(3002, Faction.ARVONIAN, "Carrier"),
	TORGOTH_GOLIATH(4000, Faction.TORGOTH, "Goliath"),
	TORGOTH_LEVIATHAN(4001, Faction.TORGOTH, "Leviathan"),
	TORGOTH_BEHEMOTH(4002, Faction.TORGOTH, "Behemoth"),
	SKARAAN_DEFILER(5000, Faction.SKARAAN, "Defiler"),
	SKARAAN_ENFORCER(5001, Faction.SKARAAN, "Enforcer"),
	SKARAAN_EXECUTOR(5002, Faction.SKARAAN, "Executor"),
	BIOMECH_STAGE_1(6000, Faction.BIOMECH, "Stage 1"),
	BIOMECH_STAGE_2(6001, Faction.BIOMECH, "Stage 2"),
	BIOMECH_STAGE_3(6002, Faction.BIOMECH, "Stage 3"),
	BIOMECH_STAGE_4(6003, Faction.BIOMECH, "Stage 4");

	private static final ShipType[] PLAYER_SHIPS = Arrays.copyOfRange(values(), 0, TSN_BASE.ordinal());

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
		fullName = faction + " " + hullName;
	}

	public int getId() {
		return id;
	}

	public Faction getFaction() {
		return faction;
	}

	public String getHullName() {
		return hullName;
	}

	public String getFullName() {
		return fullName;
	}
}