package net.dhleong.acl.world;

public class ArtemisOtherShip extends BaseArtemisObject {

    public ArtemisOtherShip(int objId, String name) {
        super(objId, name);
    }

    @Override
    public int getType() {
        return TYPE_OTHER;
    }

    @Override
    public String toString() {
        return String.format("[OTHER:%s]", mName);
    }

}
