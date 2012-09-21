package net.dhleong.acl.world;


public class ArtemisPlayer extends BaseArtemisObject {

    private final boolean mRedAlert;

    public ArtemisPlayer(int objId, String name, boolean redAlert) {
        super(objId, name);
        
        mRedAlert = redAlert;
    }
    
    @Override
    public int getType() {
        return TYPE_PLAYER;
    }
    
    public boolean isRedAlert() {
        return mRedAlert;
    }

    @Override
    public String toString() {
        return String.format("[PLAYER:%s:%b]", mName, mRedAlert);
    }
}
