package net.dhleong.acl.vesseldata;

/**
 * A subclass of VesselPoint for weapon ports. In addition to their location on
 * the Vessel's 3D mesh, WeaponPorts also have damage, cycle time and range
 * values. Beam ports (subclassed as BeamPort), Torgoth drone launchers and
 * base torpedo ports are WeaponPorts. Player ship torpedo tubes, while
 * technically being weapon ports, are simply VesselPoints, since they don't
 * have damage, cycle time and range properties; those properties are dictated
 * by the ordnance loaded in them.
 * @author rjwut
 */
public class WeaponPort extends VesselPoint {
	float damage;
	float cycleTime;
	int range;

	/**
	 * The amount of damage that this weapon port can inflict.
	 */
	public float getDamage() {
		return damage;
	}

	/**
	 * The time (in seconds) this port requires to cool down between shots.
	 */
	public float getCycleTime() {
		return cycleTime;
	}

	public int getRange() {
		return range;
	}
}