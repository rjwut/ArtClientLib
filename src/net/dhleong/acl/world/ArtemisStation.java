package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;


public class ArtemisStation extends BaseArtemisShielded {
    public ArtemisStation(int objId, String name) {
        super(objId, name);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.SPACE_STATION;
    }

    @Override
    public String toString() {
        return String.format("[STATION:%s <%.1f|%.1f>]@%s", 
                mName,
                getShieldsFront(), getShieldsRear(),
                super.toString());
    }

}
