package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

/**
 * Space whales!
 */
public class ArtemisWhale extends BaseArtemisOrientable {
    private float mSteering = -1;

    public ArtemisWhale(int objId, String name) {
        super(objId, name);
    }

	@Override
	public ObjectType getType() {
		return ObjectType.WHALE;
	}

    @Override
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisWhale) {
            ArtemisWhale cast = (ArtemisWhale) eng;

            if (cast.getSteering() != -1) {
                setSteering(cast.getSteering());
            }
        }
    }

    public float getSteering() {
        return mSteering;
    }

    public void setSteering(float steering) {
        mSteering = steering;
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    }
}