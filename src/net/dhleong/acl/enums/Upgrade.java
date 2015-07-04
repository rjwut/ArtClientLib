package net.dhleong.acl.enums;

/**
 * Upgrade types
 * @author rjwut
 */
public enum Upgrade {
	ANOMALY(null),                           // +500 energy (on pickup)
	VIGORANIUM_NODULE(null),                 // resurrects 1 DAMCON (on pickup)
	CETROCITE_HEATSINK(Console.ENGINEERING), // coolant buff
	LATERAL_ARRAY(Console.SCIENCE),          // scanning is instantaneous
	TAURON_FOCUSERS(Console.WEAPONS),        // +10% beam damage and cooldown buff
	INFUSION_P_COILS(Console.HELM),          // +10% warp and impulse speed
	CARAPACTION_COILS(Console.WEAPONS),      // +10% shield recharge rate
	DOUBLE_AGENT(Console.COMMUNICATIONS);    // force 1 enemy to accept surrender
	/*
	 * Unknown types:
	 * HYDROGEN_RAM
	 * POLYPHASIC_CAPACITORS
	 * COOLANT_RESERVES
	 * ECM_STARPULSE
	 * WARTIME_PRODUCTION
	 * PROTONIC_VERNIERS
	 * REGENERATIVE_PAU_GRIDS
	 * VETERAN_DAMCON_TEAMS
	 * TACHYON_SCANNERS
	 * GRIDSCAN_OVERLOAD
	 * OVERRIDE_AUTHORIZATION
	 * RESUPPLY_IMPERATIVES
	 * PATROL_GROUP
	 * FAST_SUPPLY
	 * VANGUARD_REFIT (x6)
	 */

	private Console activatedBy;

	private Upgrade(Console activatedBy) {
		this.activatedBy = activatedBy;
	}

	/**
	 * Returns the Console that can activate this Upgrade, or null if the
	 * Upgrade is used immediately when picked up.
	 */
	public Console getActivatedby() {
		return activatedBy;
	}
}