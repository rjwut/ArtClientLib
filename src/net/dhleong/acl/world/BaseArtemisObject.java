package net.dhleong.acl.world;

public abstract class BaseArtemisObject implements ArtemisPositionable {

    protected final int mId;
    public final String mName;
    
    private float mX;
    private float mY;
    private float mZ;

    public BaseArtemisObject(int objId, String name) {
        mId = objId;
        mName = name;
    }

    @Override
    public int getId() {
        return mId;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ArtemisObject))
            return false;
        
        return getId() == ((ArtemisObject)other).getId();
    }
    
    @Override
    public int hashCode() {
        return getId();
    }
    
    @Override
    public String toString() {
        return String.format("(%.0f,%.0f,%.0f)", mX, mY, mZ);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public void setX(float mX) {
        this.mX = mX;
    }

    @Override
    public float getY() {
        return mY;
    }

    @Override
    public void setY(float y) {
        mY = y;
    }

    @Override
    public float getZ() {
        return mZ;
    }

    @Override
    public void setZ(float z) {
        mZ = z;
    }

}
