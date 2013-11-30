package net.dhleong.acl.world;

/**
 * Now unused? 
 */
public class ArtemisOtherShip extends BaseArtemisShip {

    public ArtemisOtherShip(int objId, String name, int hullId) {
        super(objId, name, hullId);
    }

    @Override
    public int getType() {
        return -1;
    }

    @Override
    public String toString() {
        return String.format("[OTHER:%s:%d]@%s", mName, mHullId, super.toString());
    }

}
