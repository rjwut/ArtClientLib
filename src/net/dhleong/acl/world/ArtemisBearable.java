package net.dhleong.acl.world;

/**
 * An Positionable in the world which
 *  also has a bearing and velocity
 *  
 * @author dhleong
 *
 */
public interface ArtemisBearable extends ArtemisPositionable {
    
    /**
     * Base velocity (IE: corresponding to vel = 1.0)
     *  in distance units per second
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

    public float getBearing();

    public void setBearing(float bearing);
    
    public float getSteering();
    
    public void setSteering(float steering);
    
    public float getVelocity();

    public void setVelocity(float velocity);
}
