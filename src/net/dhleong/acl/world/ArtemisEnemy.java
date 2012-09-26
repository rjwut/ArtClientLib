package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisShip {
    
    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name, hullId);
        
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
