package net.dhleong.acl.world;

public abstract class BaseArtemisObject implements ArtemisObject {

    private final int mId;
    protected final String mName;

    public BaseArtemisObject(int objId, String name) {
        mId = objId;
        mName = name;
    }

    @Override
    public int getId() {
        return mId;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ArtemisObject))
            return false;
        
        return getId() == ((ArtemisObject)other).getId();
    }
    
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String getName() {
        return mName;
    }

}
