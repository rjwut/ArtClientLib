package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisShip {
    
    // elite enemy bits
    public static final int ELITE_INVIS_TO_MAIN_SCREEN  = 1;
    public static final int ELITE_INVIS_TO_SCIENCE      = 2;
    public static final int ELITE_INVIS_TO_TACTICAL     = 4;
    public static final int ELITE_CLOAKING              = 8;
    public static final int ELITE_HET                   = 16;
    public static final int ELITE_WARP                  = 32;
    public static final int ELITE_TELEPORT              = 64;

    // scan levels... only 2 for now
    public static final byte SCAN_LEVEL_BASIC = 1;
    public static final byte SCAN_LEVEL_FULL  = 2;
    
    private byte mScannedStatus = -1;
    private int mElite;
    
    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name, hullId);
        
    }
    
    @Override
    public int getType() {
        return TYPE_ENEMY;
    }
    
    public boolean hasEliteAbility(int ability) {
        return mElite != -1 && (mElite & ability) != 0;
    }
    
    public boolean isScanned(byte scanLevel) {
        return mScannedStatus >= scanLevel;
    }
    
    public void setEliteBits(int elite) {
        mElite = elite;
    }

    public void setScanned(byte scanned) {
        mScannedStatus = scanned;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it SHOULD be an ArtemisEnemy
        if (eng instanceof ArtemisEnemy) {
            ArtemisEnemy cast = (ArtemisEnemy)eng;
            if (cast.mScannedStatus != -1)
                setScanned(cast.mScannedStatus);
            
            if (cast.mElite != -1)
                setEliteBits(cast.mElite);
        }
        
    }

    @Override
    public String toString() {
        return String.format("[ENEMY:%s:%d:%c]%s", 
                mName, 
                mHullId,
                (mScannedStatus > 0) ? mScannedStatus : '_',
                super.toString());
    }
}
