package net.dhleong.acl.enums;

/**
 * The four types of ordnance that player ships can fire.
 * @author rwalker
 */
public enum OrdnanceType {
	HOMING("Homing"),
	NUKE("Nuke"),
	MINE("Mine"),
	EMP("EMP");

	public static final int COUNT = values().length;

	private String label;

	OrdnanceType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}