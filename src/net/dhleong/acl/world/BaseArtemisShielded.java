package net.dhleong.acl.world;

import java.util.SortedMap;

/**
 * Base implementation of a shielded world object.
 */
public abstract class BaseArtemisShielded extends BaseArtemisObject
		implements ArtemisShielded {
    private float mShieldsFront = Float.MIN_VALUE;
    private float mShieldsRear = Float.MIN_VALUE;

    public BaseArtemisShielded(int objId, String name) {
        super(objId, name);
    }

    @Override
    public float getShieldsFront() {
        return mShieldsFront;
    }

    @Override
    public void setShieldsFront(float shieldsFront) {
        mShieldsFront = shieldsFront;
    }
    @Override
    public float getShieldsRear() {
        return mShieldsRear;
    }

    @Override
    public void setShieldsRear(float shieldsRear) {
        mShieldsRear = shieldsRear;
    }

    @Override
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisShielded) {
            ArtemisShielded ship = (ArtemisShielded) eng;

            if (ship.getShieldsFront() != Float.MIN_VALUE) {
                mShieldsFront = ship.getShieldsFront();
            }

            if (ship.getShieldsRear() != Float.MIN_VALUE) {
                mShieldsRear = ship.getShieldsRear();
            }
        }
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Shields: fore", mShieldsFront, Float.MIN_VALUE, includeUnspecified);
    	putProp(props, "Shields: aft", mShieldsRear, Float.MIN_VALUE, includeUnspecified);
    }
}