package net.dhleong.acl.world;

public abstract class BaseArtemisShip extends BaseArtemisObject {

    protected final int mHullId;
    
    private float mBearing;

    public BaseArtemisShip(int objId, String name, int hullId) {
        super(objId, name);
        
        mHullId = hullId;
    }

    public int getHullId() {
        return mHullId;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

}
