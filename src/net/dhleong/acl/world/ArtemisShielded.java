package net.dhleong.acl.world;

/**
 * An object which can have shields
 * 
 * @author dhleong
 *
 */
public interface ArtemisShielded extends ArtemisObject {
    public float getShieldsFront();
    public void setShieldsFront(float value);
    public float getShieldsRear();
    public void setShieldsRear(float value);
}