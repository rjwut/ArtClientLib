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
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        if (eng instanceof BaseArtemisShip) {
            BaseArtemisShip ship = (BaseArtemisShip) eng;
            if (ship.getBearing() != Float.MIN_VALUE) 
                setBearing(ship.getBearing());
        }
    }
}
