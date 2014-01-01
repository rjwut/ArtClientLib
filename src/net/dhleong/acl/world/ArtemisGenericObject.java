package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;

/**
 * There are many "generic" objects which are very similar in implementation.
 * They are all handled by this class. Specifically, the objects implemented by
 * this class are: mines, anomalies, nebulae, torpedoes, black holes, asteroids
 * and space monsters.
 * @author dhleong
 */
public class ArtemisGenericObject extends BaseArtemisObject {
    private final ObjectType mType;

    public ArtemisGenericObject(int objId, String name, ObjectType type) {
        super(objId, (name == null ? type.toString() : name));
        mType = type;
    }

    @Override
    public ObjectType getType() {
        return mType;
    }
}