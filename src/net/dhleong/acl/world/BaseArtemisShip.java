package net.dhleong.acl.world;

public abstract class BaseArtemisShip extends BaseArtemisObject implements ArtemisBearable {

    protected int mHullId;
    
    private float mBearing;

    private float mShieldsFront, mShieldsFrontMax, mShieldsRear, mShieldsRearMax;

    private final float[] mShieldFreqs = new float[5];

    public BaseArtemisShip(int objId, String name, int hullId) {
        super(objId, name);
        
        mHullId = hullId;
    }

    public int getHullId() {
        return mHullId;
    }

    @Override
    public float getBearing() {
        return mBearing;
    }

    @Override
    public void setBearing(float bearing) {
        mBearing = bearing;
    }
    
    @Override
    public String toString() {
        return String.format("[%.2f,%.2f,%.2f,%.2f,%.2f]%s<%f>;[%.1f/%.1f  %.1f/%.1f]",
                mShieldFreqs[0],mShieldFreqs[1],mShieldFreqs[2],
                mShieldFreqs[3],mShieldFreqs[4],
                super.toString(), mBearing,
                getShieldsFront(), getShieldsFrontMax(), getShieldsRear(), getShieldsRearMax());
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
    
    public float getShieldFreq(int freq) {
        return mShieldFreqs[freq];
    }
    
    public void setShieldFreq(int freq, float value) {
        mShieldFreqs[freq] = value;
    }

    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        if (eng instanceof BaseArtemisShip) {
            BaseArtemisShip ship = (BaseArtemisShip) eng;
            if (mHullId == -1 && ship.mHullId != -1)
                mHullId = ship.mHullId;
            
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
            
            for (int i=0; i<mShieldFreqs.length; i++) {
                if (ship.mShieldFreqs[i] != -1)
                    mShieldFreqs[i] = ship.mShieldFreqs[i];
            }
        }
    }
}
