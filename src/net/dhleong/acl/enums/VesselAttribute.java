package net.dhleong.acl.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * A list of attributes for Vessels. Corresponds to the <vessel broadType="">
 * attribute in vesselData.xml.
 * @author rjwut
 */
public enum VesselAttribute {
	// vessel class
	PLAYER,
	BASE,
	FIGHTER,
	SMALL,
	MEDIUM,
	LARGE,

	// civilian type
	WARSHIP,
	SCIENCE,
	CARGO,
	LUXURY,
	TRANSPORT,

	// behavior
	CARRIER,
	ASTEROIDEATER,
	ANOMALYEATER,
	SENTIENT;

	public static Set<VesselAttribute> build(String broadType) {
		String[] tokens = broadType.split(" ");
		Set<VesselAttribute> attrs = new HashSet<VesselAttribute>();

		for (String token : tokens) {
			attrs.add(VesselAttribute.valueOf(token.toUpperCase()));
		}

		return attrs;
	}
}