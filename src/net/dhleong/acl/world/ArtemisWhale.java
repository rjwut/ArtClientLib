package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

/**
 * Space whales!
 */
public class ArtemisWhale extends BaseArtemisObject implements ArtemisBearable {
    private float mBearing = Float.MIN_VALUE, mVelocity = -1, mSteering = -1;

    public ArtemisWhale(int objId, String name) {
        super(objId, name);
    }

	@Override
	public ObjectType getType() {
		return ObjectType.WHALE;
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
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisWhale) {
            ArtemisWhale cast = (ArtemisWhale) eng;

            if (cast.getBearing() != Float.MIN_VALUE) { 
                setBearing(cast.getBearing());
            }

            if (cast.getVelocity() != -1) {
                setVelocity(cast.getVelocity());
            }

            if (cast.getSteering() != -1) {
                setSteering(cast.getSteering());
            }
        }
    }

    /**
     * Returns the object's velocity, in ls. (No idea what this unit
     * represents.) Note that ArtClientLib currently doesn't know how to parse
     * velocity from space whale update packets, so this property is always
     * unspecified.
     * Unspecified: -1
     */
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
    public void setSteering(float steering) {
        mSteering = steering;
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Heading", mBearing, Float.MIN_VALUE, includeUnspecified);
    	putProp(props, "Velocity", mVelocity, -1, includeUnspecified);
    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    }
}