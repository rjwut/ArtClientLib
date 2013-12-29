package net.dhleong.acl.world;

import net.dhleong.acl.enums.ShipSystem;

public final class Artemis {
    public static final int DEFAULT_COOLANT = 8;
    public static final int MAX_COOLANT_PER_SYSTEM = 8;
	public static final int MAX_ENERGY_ALLOCATION_PERCENT = 300;
	public static final int MAX_TUBES = 6;
	public static final int MAX_WARP = 4;
	public static final int SHIP_COUNT = 8;
    public static final int SYSTEM_COUNT = ShipSystem.values().length;

	private Artemis() {
	}
}