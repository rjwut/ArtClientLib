package net.dhleong.acl.world;

public interface ArtemisPositionable extends ArtemisObject {

    public abstract void setZ(float z);

    public abstract float getZ();

    public abstract void setY(float y);

    public abstract float getY();

    public abstract void setX(float mX);

    public abstract float getX();

    public void updateFrom(ArtemisPositionable other);
}
