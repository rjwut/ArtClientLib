package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.ShipType;

public abstract class BaseArtemisShip extends BaseArtemisShielded
implements ArtemisBearable {
    protected int mHullId = -1;
    private float mBearing = Float.MIN_VALUE;
    private float mVelocity = -1;
    private float mShieldsFrontMax = -1;
    private float mShieldsRearMax = -1;
    private final float[] mShieldFreqs = new float[5];
    private float mSteering = -1;
    private float mTopSpeed = -1;
    private float mTurnRate = -1;

    public BaseArtemisShip(int objId, String name, int hullId) {
        super(objId, name);
        mHullId = hullId;

        for (int i = 0; i < 5; i++) {
        	mShieldFreqs[i] = -1;
        }
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
    public float getSteering() {
        return mSteering;
    }

    @Override
    public void setSteering(float steeringSlider) {
        mSteering = steeringSlider;
    }

    public float getTopSpeed() {
        return mTopSpeed;
    }

    public void setTopSpeed(float topSpeed) {
        mTopSpeed = topSpeed;
    }
    
    public float getTurnRate() {
        return mTurnRate;
    }
    
    public void setTurnRate(float turnRate) {
        mTurnRate = turnRate;
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

            if (ship.mHullId != -1) {
                mHullId = ship.mHullId;
            }
            
            if (ship.mBearing != Float.MIN_VALUE) { 
                mBearing = ship.mBearing;
            }
            
            if (ship.mSteering != -1) {
                mSteering = ship.mSteering;
            }
            
            if (ship.mVelocity != -1) {
                mVelocity = ship.mVelocity;
            }

            if (ship.mTopSpeed != -1) {
                mTopSpeed = ship.mTopSpeed;
            }

            if (ship.mTurnRate != -1) {
                mTurnRate = ship.mTurnRate;
            }
            
            if (ship.mShieldsFrontMax != -1) {
                mShieldsFrontMax = ship.mShieldsFrontMax;
            }

            if (ship.mShieldsRearMax != -1) {
                mShieldsRearMax = ship.mShieldsRearMax;
            }
            
            for (int i = 0; i < mShieldFreqs.length; i++) {
            	float value = ship.mShieldFreqs[i];

            	if (value != -1) {
                    mShieldFreqs[i] = value;
            	}
            }
        }
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Hull ID", mHullId, -1, includeUnspecified);

    	if (includeUnspecified || mHullId != -1) {
        	ShipType shipType = ShipType.fromId(mHullId);
    		putProp(
    				props,
    				"Ship type",
    				shipType != null ? shipType.getFullName() : null,
    				includeUnspecified
    		);
    	}

    	putProp(props, "Heading", mBearing, Float.MIN_VALUE, includeUnspecified);
    	putProp(props, "Velocity", mVelocity, -1, includeUnspecified);
    	putProp(props, "Shields: fore max", mShieldsFrontMax, -1, includeUnspecified);
    	putProp(props, "Shields: aft max", mShieldsRearMax, -1, includeUnspecified);
    	BeamFrequency[] freqs = BeamFrequency.values();

    	for (int i = 0; i < mShieldFreqs.length; i++) {
    		putProp(props, "Shield frequency " + freqs[i], mShieldFreqs[i],
    				-1, includeUnspecified);
    	}

    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    	putProp(props, "Top speed", mTopSpeed, -1, includeUnspecified);
    	putProp(props, "Turn rate", mTurnRate, -1, includeUnspecified);
    }
}