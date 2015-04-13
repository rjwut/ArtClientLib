package net.dhleong.acl.vesseldata;

/**
 * Describes a single beam port on a vessel. Corresponds to the <beam_port>
 * element in vesselData.xml.
 * @author rjwut
 */
public class BeamPort extends WeaponPort {
	float arcWidth;

	/**
	 * Returns the width of the beam arc in radians.
	 */
	public float getArcWidth() {
		return arcWidth;
	}
}