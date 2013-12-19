package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisCreature extends ArtemisGenericObject implements ArtemisBearable {

    private float mBearing, mVelocity;
    
    // default to "straight ahead"
    private float mSteering = 0.5f;

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
            if (cast.getBearing() != Float.MIN_VALUE) 
                setBearing(cast.getBearing());
            
            if (cast.getSteering() != Float.MIN_VALUE)
                setSteering(cast.getSteering());
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format("<%.2f>", mBearing);
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
}
