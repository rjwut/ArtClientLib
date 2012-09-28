package net.dhleong.acl.world;

public class ArtemisOtherShip extends BaseArtemisShip {

    public ArtemisOtherShip(int objId, String name, int hullId) {
        super(objId, name, hullId);
    }

    @Override
    public int getType() {
        return TYPE_OTHER;
    }

    @Override
    public String toString() {
        return String.format("[OTHER:%s:%d]@%s", mName, mHullId, super.toString());
    }

}
