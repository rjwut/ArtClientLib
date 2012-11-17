package net.dhleong.acl.world;

/**
 * An Positionable in the world which
 *  also has a bearing
 *  
 * @author dhleong
 *
 */
public interface ArtemisBearable extends ArtemisPositionable {

    public float getBearing();

    public void setBearing(float bearing);
}
