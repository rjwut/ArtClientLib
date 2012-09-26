package net.dhleong.acl.world;

public abstract class BaseArtemisShip extends BaseArtemisObject {

    protected final int mHullId;
    
    private float mX, mY, mZ, mBearing;

    public BaseArtemisShip(int objId, String name, int hullId) {
        super(objId, name);
        
        mHullId = hullId;
    }

    public int getHullId() {
        return mHullId;
    }

    public float getX() {
        return mX;
    }

    public void setX(float mX) {
        this.mX = mX;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getZ() {
        return mZ;
    }

    public void setZ(float z) {
        mZ = z;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

}
