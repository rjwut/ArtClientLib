package net.dhleong.acl.world;

/**
 * An Positionable in the world which
 *  also has a bearing and velocity
 *  
 * @author dhleong
 *
 */
public interface ArtemisBearable extends ArtemisPositionable {

    public float getBearing();

    public void setBearing(float bearing);
    
    public float getVelocity();

    public void setVelocity(float velocity);
}
