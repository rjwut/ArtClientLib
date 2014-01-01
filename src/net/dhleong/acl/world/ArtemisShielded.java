package net.dhleong.acl.world;

/**
 * An ArtemisObject which can have shields. Note that shield values can be
 * negative.
 * @author dhleong
 */
public interface ArtemisShielded extends ArtemisObject {
	/**
	 * The strength of the forward shields.
	 * Unspecified: Float.MIN_VALUE
	 */
    public float getShieldsFront();
    public void setShieldsFront(float value);

    /**
	 * The strength of the aft shields.
	 * Unspecified: Float.MIN_VALUE
	 */
    public float getShieldsRear();
    public void setShieldsRear(float value);
}