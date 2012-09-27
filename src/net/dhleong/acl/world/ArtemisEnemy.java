package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisShip {
    
    private boolean mIsScanned = false;
    
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

    @Override
    public String toString() {
        return String.format("[ENEMY:%s:%d:%c]@%s", 
                mName, 
                mHullId,
                mIsScanned ? 'S' : '_',
                        super.toString());
    }
}
