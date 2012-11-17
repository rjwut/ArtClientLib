package net.dhleong.acl.world;

/**
 * An Object in the world which has some
 *  position in space. Pretty much all objects
 *  are Positionable.
 *  
 * The position might change; we may want to be able
 *  to update it from some reference object
 *  
 * @author dhleong
 *
 */
public interface ArtemisPositionable extends ArtemisObject {

    public abstract void setZ(float z);

    public abstract float getZ();

    public abstract void setY(float y);

    public abstract float getY();

    public abstract void setX(float x);

    public abstract float getX();

    public void updateFrom(ArtemisPositionable other);
}
