package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisObject implements ArtemisObject {
    
    private final int mHullId;

    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name);
        
        mHullId = hullId;
    }
    
    public int getHullId() {
        return mHullId;
    }

    @Override
    public int getType() {
        return TYPE_ENEMY;
    }

    @Override
    public String toString() {
        return String.format("[ENEMY:%s:%d]", mName, mHullId);
    }
}
