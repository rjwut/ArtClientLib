package net.dhleong.acl.world;

import java.util.SortedMap;

public abstract class BaseArtemisShielded extends BaseArtemisObject 
implements ArtemisShielded {
    private float mShieldsFront = -1;
    private float mShieldsRear = -1;

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
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisShielded) {
            ArtemisShielded ship = (ArtemisShielded) eng;

            if (ship.getShieldsFront() != -1) {
                mShieldsFront = ship.getShieldsFront();
            }

            if (ship.getShieldsRear() != -1) {
                mShieldsRear = ship.getShieldsRear();
            }
        }
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Shields: fore", mShieldsFront, -1, includeUnspecified);
    	putProp(props, "Shields: aft", mShieldsRear, -1, includeUnspecified);
    }
}