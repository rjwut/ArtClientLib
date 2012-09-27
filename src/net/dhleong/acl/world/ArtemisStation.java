package net.dhleong.acl.world;


public class ArtemisStation extends BaseArtemisObject {
    
    public ArtemisStation(int objId, String name) {
        super(objId, name);
    }

    @Override
    public int getType() {
        return TYPE_STATION;
    }

    @Override
    public String toString() {
        return String.format("[STATION:%s]@%s", mName, super.toString());
    }

}
