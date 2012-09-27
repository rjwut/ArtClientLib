package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisShip {
    
    private boolean mIsScanned = false;
    
    private final float[] mShieldFreqs = new float[5];
    
    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name, hullId);
        
    }
    
    @Override
    public int getType() {
        return TYPE_ENEMY;
    }
    
    public boolean isScanned() {
        return mIsScanned;
    }
    
    public void setScanned() {
        mIsScanned = true;
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
        
        // it SHOULD be an ArtemisEnemy
        if (eng instanceof ArtemisEnemy) {
            ArtemisEnemy cast = (ArtemisEnemy)eng;
            if (cast.isScanned())
                setScanned();
            
            for (int i=0; i<mShieldFreqs.length; i++) {
                if (cast.mShieldFreqs[i] != -1)
                    mShieldFreqs[i] = cast.mShieldFreqs[i];
            }
        }
        
    }

    @Override
    public String toString() {
        return String.format("[ENEMY:%s:%d:%c:[%.2f,%.2f,%.2f,%.2f,%.2f]]@%s", 
                mName, 
                mHullId,
                mIsScanned ? 'S' : '_',
                mShieldFreqs[0],mShieldFreqs[1],mShieldFreqs[2],
                    mShieldFreqs[3],mShieldFreqs[4],
                super.toString());
    }
}
