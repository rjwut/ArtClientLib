package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisCreature extends ArtemisGenericObject implements ArtemisBearable {
    private float mBearing = -1, mVelocity = -1, mSteering = -1;

    public ArtemisCreature(int objId, String name, ObjectType type) {
        super(objId, name, type);
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
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisCreature) {
            ArtemisCreature cast = (ArtemisCreature) eng;

            if (cast.getBearing() != Float.MIN_VALUE) { 
                setBearing(cast.getBearing());
            }
            
            if (cast.getSteering() != Float.MIN_VALUE) {
                setSteering(cast.getSteering());
            }
        }
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
    public void setSteering(float steering) {
        mSteering = steering;
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Heading", mBearing, -1, includeUnspecified);
    	putProp(props, "Velocity", mVelocity, -1, includeUnspecified);
    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    }
}