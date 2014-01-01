package net.dhleong.acl.world;

/**
 * An ArtemisObject which also has a bearing and velocity.
 * @author dhleong
 */
public interface ArtemisBearable extends ArtemisObject {
    /**
     * Base velocity (IE: corresponding to vel = 1.0) in distance units per
     * second.
     */
    public static final float BASE_VELOCITY = 100f;

    /**
     * @see #BASE_VELOCITY; this is in millseconds, for convenience
     */
    public static final float BASE_VELOCITY_MS = BASE_VELOCITY / 1000f;

    /**
     * Empirically determined base rotation per ms
     */
    public static final float BASE_ROTATION_MS = 0.1367f;

    /**
     * Returns the direction the object is facing. This is expressed as a value
     * from negative pi to pi. A value of pi corresponds to a heading of 0
     * degrees. The ship turns to port as the value decreases. A value of 0
     * corresponds to a heading of 180 degrees.
     * Unspecified: Float.MIN_VALUE
     * TODO Is "heading" the more accurate term for this?
     */
    public float getBearing();
    public void setBearing(float bearing);

    /**
     * Returns the position of the object's "rudder". This is expressed as a
     * value between 0 (hard to port) and 1 (hard to starboard). A value of 0.5
     * is straight ahead (rudder amidships).
     * Unspecified: -1
     */
    public float getSteering();
    public void setSteering(float steering);

    /**
     * Returns the object's velocity, in ls. (No idea what this unit
     * represents.)
     * Unspecified: -1
     */
    public float getVelocity();
    public void setVelocity(float velocity);
}