package net.dhleong.acl.world;

public abstract class BaseArtemisShielded extends BaseArtemisObject 
implements ArtemisShielded {
    
    private float mShieldsFront, mShieldsRear;

    public BaseArtemisShielded(int objId, String name) {
        super(objId, name);
        
        mShieldsFront = mShieldsRear = -1;
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
            if (ship.getShieldsFront() != -1)
                mShieldsFront = ship.getShieldsFront();
            if (ship.getShieldsRear() != -1)
                mShieldsRear = ship.getShieldsRear();
        }
    }
}
