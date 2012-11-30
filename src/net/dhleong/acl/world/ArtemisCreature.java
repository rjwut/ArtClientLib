package net.dhleong.acl.world;

public class ArtemisCreature extends ArtemisGenericObject implements ArtemisBearable {

    private float mBearing, mVelocity;

    public ArtemisCreature(int objId, String name, ArtemisGenericObject.Type type) {
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
}
