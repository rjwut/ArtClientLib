package net.dhleong.acl.world;

public abstract class BaseArtemisShip extends BaseArtemisShielded
implements ArtemisBearable {

    protected int mHullId;
    
    private float mBearing, mVelocity;

    private float mShieldsFrontMax, mShieldsRearMax;

    private final float[] mShieldFreqs = new float[5];

    public BaseArtemisShip(int objId, String name, int hullId) {
        super(objId, name);
        
        mHullId = hullId;
    }

    public int getHullId() {
        return mHullId;
    }
    

    public void setHullId(int hullId) {
        mHullId = hullId;
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
    public float getVelocity() {
        return mVelocity;
    }
    
    @Override
    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }
    
    @Override
    public String toString() {
        return String.format("[%.2f,%.2f,%.2f,%.2f,%.2f]%s<%f>;[%.1f/%.1f  %.1f/%.1f]",
                mShieldFreqs[0],mShieldFreqs[1],mShieldFreqs[2],
                mShieldFreqs[3],mShieldFreqs[4],
                super.toString(), mBearing,
                getShieldsFront(), getShieldsFrontMax(), getShieldsRear(), getShieldsRearMax());
    }

    public float getShieldsFrontMax() {
        return mShieldsFrontMax;
    }

    public void setShieldsFrontMax(float shieldsFrontMax) {
        this.mShieldsFrontMax = shieldsFrontMax;
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
            
            if (ship.mBearing != Float.MIN_VALUE) 
                mBearing = ship.mBearing;
            
            if (ship.mVelocity != -1)
                mVelocity = ship.mVelocity;
            
            if (ship.mShieldsFrontMax != -1)
                mShieldsFrontMax = ship.mShieldsFrontMax;
            if (ship.mShieldsRearMax != -1)
                mShieldsRearMax = ship.mShieldsRearMax;
            
            for (int i=0; i<mShieldFreqs.length; i++) {
                if (ship.mShieldFreqs[i] != -1)
                    mShieldFreqs[i] = ship.mShieldFreqs[i];
            }
        }
    }
}
