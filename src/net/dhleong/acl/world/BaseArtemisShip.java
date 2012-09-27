package net.dhleong.acl.world;

public abstract class BaseArtemisShip extends BaseArtemisObject {

    protected final int mHullId;
    
    private float mBearing;

    private float mShieldsFront, mShieldsFrontMax, mShieldsRear, mShieldsRearMax;

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
    public String toString() {
        return String.format("%s<%f>;[%.1f/%.1f  %.1f/%.1f]", 
                super.toString(), mBearing,
                getShieldsFront(), getShieldsFrontMax(), getShieldsRear(), getShieldsRearMax());
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        if (eng instanceof BaseArtemisShip) {
            BaseArtemisShip ship = (BaseArtemisShip) eng;
            if (ship.getBearing() != Float.MIN_VALUE) 
                setBearing(ship.getBearing());
            
            if (ship.mShieldsFront != -1)
                mShieldsFront = ship.mShieldsFront;
            if (ship.mShieldsFrontMax != -1)
                mShieldsFrontMax = ship.mShieldsFrontMax;
            if (ship.mShieldsRear != -1)
                mShieldsRear = ship.mShieldsRear;
            if (ship.mShieldsRearMax != -1)
                mShieldsRearMax = ship.mShieldsRearMax;
        }
    }

    public float getShieldsFront() {
        return mShieldsFront;
    }

    public void setShieldsFront(float shieldsFront) {
        this.mShieldsFront = shieldsFront;
    }

    public float getShieldsFrontMax() {
        return mShieldsFrontMax;
    }

    public void setShieldsFrontMax(float shieldsFrontMax) {
        this.mShieldsFrontMax = shieldsFrontMax;
    }

    public float getShieldsRear() {
        return mShieldsRear;
    }

    public void setShieldsRear(float shieldsRear) {
        this.mShieldsRear = shieldsRear;
    }

    public float getShieldsRearMax() {
        return mShieldsRearMax;
    }

    public void setShieldsRearMax(float shieldsRearMax) {
        this.mShieldsRearMax = shieldsRearMax;
    }
}
