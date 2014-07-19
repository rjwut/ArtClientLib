package net.dhleong.acl.vesseldata;

public class WeaponPort extends VesselPoint {
	float damage;
	float cycleTime;
	int range;

	public float getDamage() {
		return damage;
	}

	public float getCycleTime() {
		return cycleTime;
	}

	public int getRange() {
		return range;
	}
}