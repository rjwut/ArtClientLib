package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisObject implements ArtemisObject {
    
    public ArtemisEnemy(int objId, String name) {
        super(objId, name);
    }

    @Override
    public int getType() {
        return TYPE_ENEMY;
    }

    @Override
    public String toString() {
        return String.format("[ENEMY:%s]", mName);
    }
}
