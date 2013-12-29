package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

/**
 * There are many "generic" objects which are very similar in implementation
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

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Type", mType, includeUnspecified);
    }
}